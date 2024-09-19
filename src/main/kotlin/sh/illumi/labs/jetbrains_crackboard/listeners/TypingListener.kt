package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class TypingListener : TypedHandlerDelegate() {
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        val crackBoard = project.service<CrackBoard>()
        if (crackBoard.isAppActive) crackBoard.sendHeartbeat(file.virtualFile)
        return super.charTyped(c, project, editor, file)
    }
}