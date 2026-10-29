package net.klindstrom.gutterbreakpointsrustplugin

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.xdebugger.breakpoints.XBreakpointListener
import java.awt.KeyboardFocusManager
import java.net.URI
import java.nio.file.Path

class ToggleRustBreakpointProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        if (isAndroidStudio) {
            // Listen for gutter clicks
            EditorFactory.getInstance()
                .eventMulticaster
                .addEditorMouseListener(
                    ToggleBreakpointClickListener(),
                    PluginDisposable.getInstance(project),
                )

            // Listen for the toggle breakpoint key shortcut
            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(ToggleBreakpointKeyWatcher(project))

            // Handle incoming HTTP breakpoint toggle requests
            AndroidStudioReceiverService.getInstance().eventHandler = ::breakpointEventHandler
        } else {
            // Listen for breakpoint changes and POST them to Android Studio
            project.messageBus.connect(PluginDisposable.getInstance(project))
                .subscribe(XBreakpointListener.TOPIC, BreakpointListener(project))
        }
    }

    private fun breakpointEventHandler(event: BreakpointEvent) {
        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(event.filePath.normalizeFilePath())

        if (virtualFile != null) {
            BreakpointHelper.tryToggleBreakpoint(virtualFile, event.lineNumber)
        } else {
            println("Error: Could not find virtual file ${event.filePath}")
        }
    }
}

private fun String.normalizeFilePath(): String = try {
    if (this.startsWith("file:/")) {
        Path.of(URI.create(this)).toString()
    } else {
        this
    }
} catch (_: Exception) {
    this.removePrefix("file://")
}
