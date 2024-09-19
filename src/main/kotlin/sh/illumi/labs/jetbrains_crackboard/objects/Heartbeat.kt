package sh.illumi.labs.jetbrains_crackboard.objects

import com.intellij.openapi.components.service
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import sh.illumi.labs.jetbrains_crackboard.CrackBoardSettings
import java.time.format.DateTimeFormatter

@Serializable
data class Heartbeat(
    val session_key: String,
    val timestamp: String,
    val language_name: String
) {
    companion object {
        fun create(
            language: String,
            time: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        ) = Heartbeat(service<CrackBoardSettings>().state.sessionKey!!, time.toString(), language)
    }
}
