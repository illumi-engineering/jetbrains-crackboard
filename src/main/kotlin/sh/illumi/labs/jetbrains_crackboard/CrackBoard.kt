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
import kotlinx.datetime.toKotlinInstant
import sh.illumi.labs.jetbrains_crackboard.listeners.*
import sh.illumi.labs.jetbrains_crackboard.ui.SessionKeyDialog
import java.awt.KeyboardFocusManager
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

@Service
class CrackBoard : Disposable {
    private val log = logger<CrackBoard>()
    private var lastHeartbeatTime: LocalDateTime? = null
    private var currentLanguage: String? = null
    private var statusBarHeartbeat: LocalDateTime? = null
    private var lastStatusBarUpdate: LocalDateTime? = null

    init {
        setupStatusBar()
        setupEventListeners()
    }

    fun sendHeartbeat(file: VirtualFile) {
        val now = LocalDateTime.now()

        // only update statusbar every 15 seconds
        if (lastStatusBarUpdate == null || (now.toInstant(ZoneOffset.UTC).toKotlinInstant() - lastStatusBarUpdate!!.toInstant(ZoneOffset.UTC).toKotlinInstant()) >= STATUSBAR_UPDATE_INTERVAL) {
            statusBarHeartbeat = now
            lastStatusBarUpdate = now
            currentLanguage = file.fileType.name
        }

        // ensure to only send a heartbeat once per interval
        if (lastHeartbeatTime == null || (now.toInstant(ZoneOffset.UTC).toKotlinInstant() - lastHeartbeatTime!!.toInstant(ZoneOffset.UTC).toKotlinInstant()) >= HEARTBEAT_INTERVAL) {
            val jsonData = """
                {
                    "session_key": "$sessionKey",
                    "timestamp": "${DateTimeFormatter.ISO_DATE_TIME.format(now)}",
                    "language_name": "${file.fileType.name}"
                }
            """.trimIndent()

            try {
                // URL of the API endpoint
                val url = URI("${service<CrackBoardSettings>().state.baseUrl}/heartbeat").toURL()
                // Open connection
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")

                // Write JSON data to the request body
                conn.outputStream.use { os ->
                    val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                    writer.write(jsonData)
                    writer.flush()
                }

                // Get the response code
                val responseCode = conn.responseCode

                if (responseCode == 429) log.warn("Rate-limited")

                // Read the response
                val response = conn.inputStream.bufferedReader().use { it.readText() }

                log.debug("Response Code: $responseCode")
                log.debug("Response: $response")

                log.info("Heartbeat sent successfully.")

                lastHeartbeatTime = now
            } catch (e: Exception) {
                log.warn("Failed to send heartbeat", e)
            }
        }
    }

    fun getStatusBarText(): String {
        val duration = getDurationFromLastHeartbeat()
        return "${currentLanguage ?: "none"}: $duration"
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

            Disposer.register(this, disposable)
        }
    }

    private fun setupStatusBar() {
        val project: Project = currentProject ?: return
        val statusbar = WindowManager.getInstance().getStatusBar(project) ?: return
        statusbar.updateWidget("CrackBoard")
    }

    private val currentProject get() = ProjectManager.getInstance().defaultProject

    private fun getDurationFromLastHeartbeat(time: LocalDateTime = LocalDateTime.now()) = java.time.Duration.between(statusBarHeartbeat ?: time, time).toKotlinDuration()

    private val sessionKey get() =
        service<CrackBoardSettings>().state.sessionKey ?: run {
            service<CrackBoardSettings>().state.sessionKey = SessionKeyDialog().promptForApiKey()
            service<CrackBoardSettings>().state.sessionKey!!
        }

    companion object {
        val HEARTBEAT_INTERVAL = 2.minutes
        val STATUSBAR_UPDATE_INTERVAL = 15.seconds
    }

    override fun dispose() {
        log.info("Disposing CrackBoard")
    }
}