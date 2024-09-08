package sh.illumi.labs.jetbrains_crackboard.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.util.Consumer
import sh.illumi.labs.jetbrains_crackboard.CrackBoard
import java.awt.event.MouseEvent

class CrackBoardStatusBarWidget(
    project: Project
) : StatusBarWidget {
    private val statusBar = WindowManager.getInstance().getStatusBar(project)

    override fun ID() = "CrackBoard"

    override fun getPresentation() = CrackBoardStatusBarWidgetPresentation(this)

    class CrackBoardStatusBarWidgetPresentation(
        private val widget: CrackBoardStatusBarWidget
    ) : StatusBarWidget.MultipleTextValuesPresentation {
        override fun getTooltipText() = null
        override fun getClickConsumer(): Consumer<MouseEvent> = Consumer { _event ->
            if (widget.statusBar != null) widget.statusBar.updateWidget("CrackBoard")
        }
        override fun getSelectedValue() = service<CrackBoard>().getStatusBarText()
    }

    class Factory : StatusBarWidgetFactory {
        override fun getId() = "CrackBoard"
        override fun getDisplayName() = "CrackBoard"
        override fun isAvailable(project: Project) = true
        override fun createWidget(project: Project) = CrackBoardStatusBarWidget(project)
        override fun canBeEnabledOn(statusBar: StatusBar) = true
    }
}