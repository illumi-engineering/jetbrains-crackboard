package sh.illumi.labs.jetbrains_crackboard

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/// The service is intended to be used instead of a project/application as a parent disposable.
@Service(*[Service.Level.APP, Service.Level.PROJECT])
class CrackBoardDisposable : Disposable {
    override fun dispose() {
        // Do nothing
    }

    companion object {
        val instance: Disposable
            get() = ApplicationManager.getApplication().service<CrackBoardDisposable>()

        fun getInstance(project: Project): Disposable {
            return project.service<CrackBoardDisposable>()
        }
    }
}
