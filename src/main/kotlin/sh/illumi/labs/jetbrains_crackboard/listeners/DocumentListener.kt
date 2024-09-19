package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.project.ProjectManager
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class DocumentListener : BulkAwareDocumentListener.Simple {

    override fun documentChangedNonBulk(event: DocumentEvent) {
        val crackBoard = ProjectManager.getInstance().defaultProject.service<CrackBoard>()
        if (crackBoard.isAppActive) crackBoard.getVirtualFile(event.document)?.let { crackBoard.sendHeartbeat(it) }
    }
}