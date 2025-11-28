package net.klindstrom.gutterbreakpointsrustplugin

import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.editor.event.EditorMouseListener

class ToggleBreakpointClickListener: EditorMouseListener {

    override fun mouseClicked(e: EditorMouseEvent) {
        if (
            e.area == EditorMouseEventArea.LINE_NUMBERS_AREA ||
            e.area == EditorMouseEventArea.LINE_MARKERS_AREA
        ) {
            val editor = e.editor
            val file = editor.virtualFile
            val line = editor.xyToLogicalPosition(e.mouseEvent.point).line

            BreakpointHelper.tryToggleBreakpoint(file, line)
        }
    }
}
