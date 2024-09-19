package sh.illumi.labs.jetbrains_crackboard

import com.intellij.AppTopics
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.util.progress.sleepCancellable
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import sh.illumi.labs.jetbrains_crackboard.listeners.*
import sh.illumi.labs.jetbrains_crackboard.objects.Heartbeat
import sh.illumi.labs.jetbrains_crackboard.objects.MeStats
import sh.illumi.labs.jetbrains_crackboard.ui.SessionKeyDialog
import java.awt.KeyboardFocusManager
import kotlin.time.Duration.Companion.minutes

@Service(Service.Level.PROJECT)
class CrackBoard(
    private val project: Project,
    private val cs: CoroutineScope
) {
    private val log = logger<CrackBoard>()
    private var lastHeartbeatTime: Instant? = null
    private var currentStats: MeStats? = null

    private val client = HttpClient(Java) {
        engine {
            threadsCount = 8
            pipelining = true
            protocolVersion = java.net.http.HttpClient.Version.HTTP_2
        }

        install(ContentNegotiation) {
            json()
        }
    }

    init {
        setupStatusBar()
        setupEventListeners()
    }

    fun getMeStats() = cs.launch {
        val settings = service<CrackBoardSettings>().state
        currentStats = client.get("${settings.baseUrl}/api/user/me") {
            headers {
                append("SessionKey", service<CrackBoardSettings>().state.sessionKey!!)
            }
        }.body()
    }

    val statusBarDisplay
        get() = currentStats?.let { "${it.twitter_handle}: #${it.position} - ${it.total_minutes.minutes}" }
            ?: "CrackBoard Uninitialized"

    fun sendHeartbeat(file: VirtualFile) {
        cs.launch {
            val settings = service<CrackBoardSettings>().state
            sleepCancellable(2.minutes.inWholeMilliseconds)

            val now = Clock.System.now()

            // ensure to only send a heartbeat once per interval
            if (lastHeartbeatTime == null || (now - lastHeartbeatTime!!) >= HEARTBEAT_INTERVAL) {
                try {
                    val resp = client.post("${settings.baseUrl}/heartbeat") {
                        setBody(
                            Heartbeat.create(
                                file.fileType.name,
                                now.toLocalDateTime(TimeZone.currentSystemDefault())
                            )
                        )
                    }

                    if (!resp.status.isSuccess()) {
                        // Only throw if we're not being rate limited
                        if (resp.status != HttpStatusCode.TooManyRequests) throw Exception("Failed to send heartbeat: ${resp.status}")
                    }
                    log.info("Heartbeat sent successfully.")

                    lastHeartbeatTime = now
                } catch (e: Exception) {
                    log.warn("Failed to send heartbeat", e)
                }
            }
        }
    }

    val isAppActive get() = KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow != null

    fun getVirtualFile(document: Document) = FileDocumentManager.getInstance().getFile(document)

    private fun setupEventListeners() {
        val app = ApplicationManager.getApplication()

        app.invokeLater {
            val disposable = Disposer.newDisposable("CrackBoardListener")
            val connection = app.messageBus.connect()

            connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, SaveListener())

            EditorFactory.getInstance().eventMulticaster.addDocumentListener(DocumentListener(), disposable)
            EditorFactory.getInstance().eventMulticaster.addCaretListener(CaretChangeListener(), disposable)
            EditorFactory.getInstance().eventMulticaster.addEditorMouseListener(MouseEventListener(), disposable)
            EditorFactory.getInstance().eventMulticaster.addVisibleAreaListener(VisibilityListener(), disposable)

            Disposer.register(CrackBoardDisposable.instance, disposable)
        }
    }

    private fun setupStatusBar() {
        val project: Project = currentProject ?: return
        val statusbar = WindowManager.getInstance().getStatusBar(project) ?: return
        statusbar.updateWidget("CrackBoard")
    }

    private val currentProject get() = ProjectManager.getInstance().defaultProject

    private val sessionKey get() = service<CrackBoardSettings>().state.sessionKey

    fun checkApiKey() {
        ApplicationManager.getApplication().invokeLater {
            if (sessionKey == null) {
                SessionKeyDialog().promptForApiKey()
            }
        }
    }

    companion object {
        val HEARTBEAT_INTERVAL = 2.minutes
    }
}