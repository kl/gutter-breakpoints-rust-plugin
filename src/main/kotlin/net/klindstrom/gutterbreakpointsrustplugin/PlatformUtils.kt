package net.klindstrom.gutterbreakpointsrustplugin

import com.intellij.openapi.application.ApplicationInfo

val isAndroidStudio: Boolean
    get() = ApplicationInfo.getInstance().build.productCode == "AI"
