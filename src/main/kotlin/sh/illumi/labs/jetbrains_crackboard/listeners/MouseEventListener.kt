package sh.illumi.labs.jetbrains_crackboard.listeners

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.project.ProjectManager
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class MouseEventListener : EditorMouseListener {

    override fun mousePressed(e: EditorMouseEvent) {
        val crackBoard = ProjectManager.getInstance().defaultProject.service<CrackBoard>()
        if (crackBoard.isAppActive) crackBoard.getVirtualFile(e.editor.document)?.let { crackBoard.sendHeartbeat(it) }
    }
}