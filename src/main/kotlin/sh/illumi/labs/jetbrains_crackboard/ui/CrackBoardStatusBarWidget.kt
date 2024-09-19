package sh.illumi.labs.jetbrains_crackboard.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.CustomStatusBarWidget
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.ui.ClickListener
import com.intellij.util.LazyInitializer
import com.intellij.util.ui.update.Activatable
import com.intellij.util.ui.update.UiNotifyConnector
import sh.illumi.labs.jetbrains_crackboard.CrackBoard

class CrackBoardStatusBarWidget(
    project: Project,
) : CustomStatusBarWidget, Activatable {
    override fun ID() = "CrackBoard"

    private val barComponent: LazyInitializer.LazyValue<CrackBoardStatusBarComponent> =
        LazyInitializer.create { CrackBoardStatusBarComponent(this) }

    override fun getComponent(): CrackBoardStatusBarComponent = barComponent.get()


    class CrackBoardStatusBarComponent(widget: CrackBoardStatusBarWidget) : TextPanel() {
        init {
            isFocusable = false
            setTextAlignment(CENTER_ALIGNMENT) // eww magic numbers
            object : ClickListener() {
                override fun onClick(e: java.awt.event.MouseEvent, clickCount: Int): Boolean {
                    service<CrackBoard>().getMeStats().invokeOnCompletion {
                        updateState()
                    }
                    return true
                }
            }.installOn(this, true)
            updateState()
            UiNotifyConnector.installOn(this, widget)
        }

        private fun updateState() {
            if (!isShowing()) return
            text = service<CrackBoard>().statusBarDisplay
            updateUI()
        }
    }
}

class CrackBoardStatusBarWidgetFactory : StatusBarWidgetFactory {
    override fun getId() = "CrackBoard"
    override fun getDisplayName() = "CrackBoard"
    override fun isAvailable(project: Project) = true
    override fun createWidget(project: Project) = CrackBoardStatusBarWidget(project)
    override fun canBeEnabledOn(statusBar: StatusBar) = true
}