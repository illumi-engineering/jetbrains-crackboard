package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.project.ProjectManager
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class VisibilityListener : VisibleAreaListener {

    override fun visibleAreaChanged(e: VisibleAreaEvent) {
        val crackBoard = ProjectManager.getInstance().defaultProject.service<CrackBoard>()
        if (didChange(e) && crackBoard.isAppActive)
            crackBoard.getVirtualFile(e.editor.document)?.let { crackBoard.sendHeartbeat(it) }
    }

    private fun didChange(visibleAreaEvent: VisibleAreaEvent): Boolean {
        val oldRect = visibleAreaEvent.oldRectangle ?: return true
        val newRect = visibleAreaEvent.newRectangle
        return newRect.x != oldRect.x || newRect.y != oldRect.y
    }
}