package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class CaretChangeListener : CaretListener {
    private val crackBoard = service<CrackBoard>()

    override fun caretPositionChanged(e: CaretEvent) {
        if (crackBoard.isAppActive) crackBoard.getVirtualFile(e.editor.document)?.let { crackBoard.sendHeartbeat(it) }
    }
}