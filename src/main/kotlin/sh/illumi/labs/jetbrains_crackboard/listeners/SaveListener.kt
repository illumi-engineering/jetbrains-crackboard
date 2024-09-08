package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class SaveListener : FileDocumentManagerListener {
    private val crackBoard = service<CrackBoard>()

    override fun beforeDocumentSaving(document: Document) {
        if (crackBoard.isAppActive) crackBoard.getVirtualFile(document)?.let { crackBoard.sendHeartbeat(it) }
    }
}