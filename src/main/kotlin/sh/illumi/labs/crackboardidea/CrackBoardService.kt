package sh.illumi.labs.crackboardidea

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val HEARTBEAT_INTERVAL = 2 * 60 * 1000 // 2 minutes in milliseconds

@Service
class CrackBoardService {
    private val apiKey = ApiKey()

    fun sendHeartbeat(language: String) {
        val jsonData = """
            {
                "session_key": "${service<CrackBoardSettings>().state.sessionKey}",
                "timestamp": ${DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())},
                "language_name": "$language"
            }
        """.trimIndent()

        // URL of the API endpoint
        val url = URI("${service<CrackBoardSettings>().state.baseUrl}/heartbeat").toURL()

        try {
            // Open connection
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")

            // Write JSON data to the request body
            conn.outputStream.use { os ->
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                writer.write(jsonData)
                writer.flush()
            }

            // Get the response code
            val responseCode = conn.responseCode

            // Read the response
            val response = conn.inputStream.bufferedReader().use { it.readText() }

            println("Response Code: $responseCode")
            println("Response: $response")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getKey(defaultToCached: Boolean = true): String = if (defaultToCached) {
        service<CrackBoardSettings>()
            .state.sessionKey ?: apiKey.promptForApiKey()
    } else apiKey.promptForApiKey()
}