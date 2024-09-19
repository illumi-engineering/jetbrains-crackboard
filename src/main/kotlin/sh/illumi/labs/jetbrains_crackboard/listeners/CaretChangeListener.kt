package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.project.ProjectManager
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class CaretChangeListener : CaretListener {
    override fun caretPositionChanged(e: CaretEvent) {
        val crackBoard = ProjectManager.getInstance().defaultProject.service<CrackBoard>()
        if (crackBoard.isAppActive) crackBoard.getVirtualFile(e.editor.document)?.let { crackBoard.sendHeartbeat(it) }
    }
}