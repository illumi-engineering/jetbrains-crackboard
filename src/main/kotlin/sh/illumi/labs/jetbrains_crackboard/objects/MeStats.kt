package sh.illumi.labs.jetbrains_crackboard.objects

import kotlinx.serialization.Serializable

// These property names suck because golang does a funny.
@Serializable
data class MeStats(
    val position: Int,
    val total_minutes: Int,
    val language_minutes: Map<String, Int>,
    val twitter_handle: String,
    val profile_photo_url: String,
    val Colors: Map<String, String>
)