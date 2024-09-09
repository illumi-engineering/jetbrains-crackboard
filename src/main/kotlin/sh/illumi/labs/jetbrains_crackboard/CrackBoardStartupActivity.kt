package sh.illumi.labs.jetbrains_crackboard

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class CrackBoardStartupActivity : StartupActivity.Background {
    override fun runActivity(project: Project) {
        service<CrackBoard>().checkApiKey()
    }
}