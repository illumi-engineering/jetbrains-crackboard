package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class MouseEventListener : EditorMouseListener {
    private val crackBoard = service<CrackBoard>()

    override fun mousePressed(e: EditorMouseEvent) {
        if (crackBoard.isAppActive) crackBoard.getVirtualFile(e.editor.document)?.let { crackBoard.sendHeartbeat(it) }
    }
}