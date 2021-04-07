import java.io.OutputStream
import java.lang.IllegalArgumentException
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val parameters : Map<String, String>
    try {
        parameters = cmdArgumentsParser(args)
    } catch (e : IllegalArgumentException){
        println(e.message + "\nUsage: " +
                "\n\tfib -c | -s -h <host> -p <port> " +
                "\n\t-c | --client \t\t\t startup parameter: client" +
                "\n\t-s | --server \t\t\t startup parameter: server" +
                "\n\t-p | --port \t\t\t port number" +
                "\n\t-h | --host \t\t\t host number (ignored when startup parameter is server)")
        exitProcess(-1);
    }
    if (parameters["type"].equals("server")){

    }
}

fun serverStarter(port : Int){
    val server = ServerSocket(port)
    println("Server is running on port ${server.localPort}")
}

fun cmdArgumentsParser(args: Array<String>) : Map<String, String> {

    val parameter = HashMap<String, String>()
    val portRegex = "^()([1-9]|[1-5]?[0-9]{2,4}|6[1-4][0-9]{3}|65[1-4][0-9]{2}|655[1-2][0-9]|6553[1-5])$".toRegex()
    val hostRegex = "^(((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))|(localhost)$".toRegex()

    var i = 0
    try {
        while (i < args.size) {
            when (args[i]) {
                "-s", "--server" -> {
                    if (!parameter.containsKey("type")) {
                        parameter["type"] = "server"; i++
                    }
                    else throw IllegalArgumentException("Input parameter must be either server or client!")
                }
                "-c", "--client" -> {
                    if (!parameter.containsKey("type")) {
                        parameter["type"] = "server"; i++
                    }
                    else throw IllegalArgumentException("Input parameter must be either server or client!")
                }
                "-p", "--port" -> {
                    parameter["port"] = args[i + 1]; i++
                }
                "-h", "--host" -> {
                    parameter["host"] = args[i + 1]; i++
                }
                else -> i++
            }
        }
    } catch (e : ArrayIndexOutOfBoundsException) {
        throw IllegalArgumentException("Missing parameter!")
    }

    if(!(parameter.containsKey("type") && parameter.containsKey("host") && parameter.containsKey("port")))
        throw IllegalArgumentException("One ore more of parameters are missing!")

    if (!parameter["port"]?.matches(portRegex)!!)
        throw IllegalArgumentException("Port is incorrect!")

    if (!parameter["host"]?.matches(hostRegex)!!)
        throw IllegalArgumentException("Host is incorrect!")

    return parameter
}

class Handler : Runnable {

    override fun run() {

    }

}