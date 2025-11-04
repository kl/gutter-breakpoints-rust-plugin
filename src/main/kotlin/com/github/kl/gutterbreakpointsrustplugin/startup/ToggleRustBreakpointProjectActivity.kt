package com.github.kl.gutterbreakpointsrustplugin.startup

import com.github.kl.gutterbreakpointsrustplugin.services.PluginDisposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.breakpoints.XLineBreakpointType

const val CPP_LINE_BREAKPOINT_TYPE = "com.jetbrains.cidr.execution.debugger.OCBreakpointType"

class ToggleRustBreakpointProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {

        val bpType = XBreakpointType.EXTENSION_POINT_NAME.extensionList
            .find { it.id == CPP_LINE_BREAKPOINT_TYPE } as? XLineBreakpointType
            ?: return

        val bpManager = XDebuggerManager.getInstance(project).breakpointManager

        val editorEventMulticaster = EditorFactory.getInstance().eventMulticaster

        editorEventMulticaster.addEditorMouseListener(object : EditorMouseListener {
            override fun mouseClicked(e: EditorMouseEvent) {
                if (e.area == EditorMouseEventArea.LINE_MARKERS_AREA ||
                    e.area == EditorMouseEventArea.LINE_NUMBERS_AREA
                ) {
                    val editor = e.editor
                    val logicalPosition = editor.xyToLogicalPosition(e.mouseEvent.point)
                    val file = editor.virtualFile
                    val line = logicalPosition.line
                    val props = bpType.createProperties()

                    if (shouldToggleRustBreakpoint(editor, line)) {
                        val existing = bpManager.findBreakpointsAtLine(bpType, file, line).firstOrNull()
                        if (existing != null) {
                            bpManager.removeBreakpoint(existing)
                        } else {
                            bpManager.addLineBreakpoint(bpType, file.url, line, props)
                        }
                    }
                }
            }

        }, PluginDisposable.getInstance(project))
    }

    private fun shouldToggleRustBreakpoint(editor: Editor, line: Int): Boolean {
        return editor.virtualFile.extension == "rs"
    }
}

