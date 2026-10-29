package net.klindstrom.gutterbreakpointsrustplugin

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.PROJECT)
class ProjectCoroutineScopeHolder(
    val project: Project,
    val scope: CoroutineScope
) {
    companion object {
        fun getInstance(project: Project): ProjectCoroutineScopeHolder = project.service()
    }
}

val Project.coroutineScope: CoroutineScope
    get() = ProjectCoroutineScopeHolder.getInstance(this).scope
