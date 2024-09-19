package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.ProjectManager
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class SaveListener : FileDocumentManagerListener {

    override fun beforeDocumentSaving(document: Document) {
        val crackBoard = ProjectManager.getInstance().defaultProject.service<CrackBoard>()
        if (crackBoard.isAppActive) crackBoard.getVirtualFile(document)?.let { crackBoard.sendHeartbeat(it) }
    }
}