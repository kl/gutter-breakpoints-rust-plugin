package net.klindstrom.gutterbreakpointsrustplugin

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.awt.KeyboardFocusManager

class ToggleRustBreakpointProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
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
    }
}
