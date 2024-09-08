package sh.illumi.labs.crackboardidea

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class TypingListener : TypedHandlerDelegate() {
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        service<CrackBoardService>().sendHeartbeat(file.fileType.name)
        return super.charTyped(c, project, editor, file)
    }
}