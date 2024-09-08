package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.BulkAwareDocumentListener
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.testFramework.utils.editor.getVirtualFile
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class DocumentListener : BulkAwareDocumentListener.Simple {
    private val crackBoard = service<CrackBoard>()

    override fun documentChangedNonBulk(event: DocumentEvent) {
        if (crackBoard.isAppActive) crackBoard.getVirtualFile(event.document)?.let { crackBoard.sendHeartbeat(it) }
    }
}