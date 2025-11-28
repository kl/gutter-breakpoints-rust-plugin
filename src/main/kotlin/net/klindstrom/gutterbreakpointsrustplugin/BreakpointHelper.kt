package net.klindstrom.gutterbreakpointsrustplugin

import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XBreakpointType
import com.intellij.xdebugger.breakpoints.XLineBreakpointType

object BreakpointHelper {

    private const val CPP_LINE_BREAKPOINT_TYPE =
        "com.jetbrains.cidr.execution.debugger.OCBreakpointType"

    private val BP_TYPE =
        XBreakpointType.EXTENSION_POINT_NAME.extensionList.find {
            it.id == CPP_LINE_BREAKPOINT_TYPE
        } as? XLineBreakpointType

    fun tryToggleBreakpoint(file: VirtualFile, line: Int) {

        val bpType = BP_TYPE ?: return
        val project = ProjectLocator.getInstance().guessProjectForFile(file) ?: return
        val bpManager = XDebuggerManager.getInstance(project).breakpointManager

        if (shouldToggleRustBreakpoint(file)) {
            val existing = bpManager.findBreakpointsAtLine(bpType, file, line)

            if (existing.isNotEmpty()) {
                bpManager.removeBreakpoint(existing.first())
            } else {
                bpManager.addLineBreakpoint(bpType, file.url, line, bpType.createProperties())
            }
        }
    }

    fun shouldToggleRustBreakpoint(file: VirtualFile): Boolean {
        return file.extension == "rs"
    }
}
