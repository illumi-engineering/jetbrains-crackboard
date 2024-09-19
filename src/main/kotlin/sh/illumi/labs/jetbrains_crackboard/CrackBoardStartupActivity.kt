package sh.illumi.labs.jetbrains_crackboard

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class CrackBoardStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        val crackBoard = project.service<CrackBoard>()
        crackBoard.checkApiKey()
    }
}