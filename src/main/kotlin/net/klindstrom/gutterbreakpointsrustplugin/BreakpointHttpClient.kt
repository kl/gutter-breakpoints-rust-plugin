package net.klindstrom.gutterbreakpointsrustplugin

import com.google.gson.Gson
import com.intellij.openapi.components.Service
import com.intellij.util.io.HttpRequests
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Service(Service.Level.PROJECT)
class BreakpointHttpClient(
    private val cs: CoroutineScope
) {
    private val gson = Gson()

    fun toggleRemoteBreakpoint(filePath: String, line: Int) = cs.launch(Dispatchers.IO) {
        HttpRequests.post("http://127.0.0.1:34891/breakpoint", "application/json")
            .connectTimeout(2000)
            .readTimeout(2000)
            .connect { request ->
                val jsonString = gson.toJson(BreakpointEvent(filePath, line))
                request.write(jsonString)
                request.readString()
            }
    }
}
