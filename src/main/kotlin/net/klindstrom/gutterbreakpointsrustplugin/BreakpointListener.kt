package net.klindstrom.gutterbreakpointsrustplugin

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointListener
import com.intellij.xdebugger.breakpoints.XLineBreakpoint

class BreakpointListener(project: Project) : XBreakpointListener<XBreakpoint<*>> {

    private val httpClient: BreakpointHttpClient = project.service<BreakpointHttpClient>()

    override fun breakpointAdded(breakpoint: XBreakpoint<*>) {
        if (breakpoint !is XLineBreakpoint<*>) return
        httpClient.toggleRemoteBreakpoint(breakpoint.fileUrl, breakpoint.line)
    }

    override fun breakpointRemoved(breakpoint: XBreakpoint<*>) {
        if (breakpoint !is XLineBreakpoint<*>) return
        httpClient.toggleRemoteBreakpoint(breakpoint.fileUrl, breakpoint.line)
    }
}
