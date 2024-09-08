package sh.illumi.labs.jetbrains_crackboard.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import sh.illumi.labs.jetbrains_crackboard.ui.SessionKeyDialog

class SetSessionKeyAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        SessionKeyDialog().promptForApiKey()
    }
}