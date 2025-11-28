package net.klindstrom.gutterbreakpointsrustplugin

import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.project.Project
import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

class ToggleBreakpointKeyWatcher(val project: Project): KeyEventDispatcher {
    private val toggleBreakpointKeyStrokes: MutableSet<KeyStroke> = HashSet()

    init {
        val keymap = KeymapManager.getInstance().activeKeymap

        val shortcuts = keymap.getShortcuts("ToggleLineBreakpoint")
        for (shortcut in shortcuts) {
            if (shortcut is KeyboardShortcut) {
                toggleBreakpointKeyStrokes.add(shortcut.firstKeyStroke)
            }
        }
    }

    override fun dispatchKeyEvent(keyEvent: KeyEvent?): Boolean {
        val e = keyEvent ?: return false

        if (e.getID() == KeyEvent.KEY_PRESSED) {
            for (stroke in toggleBreakpointKeyStrokes) {
                if (e.matches(stroke)) {
                    return handleToggleBreakpointKeyShortcut()
                }
            }
        }
        // Let IntelliJ continue processing the event
        return false
    }

    private fun handleToggleBreakpointKeyShortcut(): Boolean {
        val editor: Editor =
            FileEditorManager.getInstance(project).selectedTextEditor ?: return false

        val file = editor.virtualFile
        if (!BreakpointHelper.shouldToggleRustBreakpoint(file)) return false

        val line = editor.caretModel.logicalPosition.line

        BreakpointHelper.tryToggleBreakpoint(file, line)

        return true
    }
}

private fun KeyEvent.matches(stroke: KeyStroke): Boolean =
    KeyStroke.getKeyStrokeForEvent(this) == stroke
