package net.klindstrom.gutterbreakpointsrustplugin

import com.google.gson.Gson
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.net.InetSocketAddress

@Service(Service.Level.APP)
class AndroidStudioReceiverService(
    private val cs: CoroutineScope
) : Disposable {

    var eventHandler: ((BreakpointEvent) -> Unit)? = null
    private val server: HttpServer?

    init {
        server = try {
            HttpServer.create(InetSocketAddress("127.0.0.1", PORT), 0).apply {
                createContext("/breakpoint", EventHandler())
                executor = Dispatchers.IO.asExecutor()
                start()
            }
        } catch (e: Exception) {
            println("Error: Failed to start receiver server: ${e.message}")
            null
        }
    }

    private inner class EventHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (exchange.requestMethod.equals("POST", ignoreCase = true)) {
                val body = InputStreamReader(exchange.requestBody).readText()

                respondOk(exchange)
                val breakpointEvent = parseIncomingRequest(body) ?: return

                cs.launch {
                    withContext(Dispatchers.EDT) {
                        eventHandler?.invoke(breakpointEvent)
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, -1) // Method Not Allowed
            }
        }

        private fun respondOk(exchange: HttpExchange) {
            val response = "OK"
            val responseBytes = response.toByteArray()
            exchange.sendResponseHeaders(200, responseBytes.size.toLong())
            exchange.responseBody.use { it.write(responseBytes) }
        }

        private fun parseIncomingRequest(payload: String): BreakpointEvent? = try {
            Gson().fromJson(payload, BreakpointEvent::class.java)
        } catch (e: Exception) {
            println("Error: Failed to parse JSON: ${e.message}")
            null
        }
    }

    override fun dispose() {
        server?.stop(0)
        eventHandler = null
    }

    companion object {
        private const val PORT = 34891
        fun getInstance(): AndroidStudioReceiverService = service()
    }
}

data class BreakpointEvent(
    val filePath: String,
    val lineNumber: Int
)
