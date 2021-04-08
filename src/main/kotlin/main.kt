import java.io.*
import java.math.BigInteger
import java.net.ConnectException
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    val parameters: Map<String, String>
    try {
        parameters = cmdArgumentsParser(args)
    } catch (e: IllegalArgumentException) {
        println(
            e.message + "\nUsage: " +
                    "\n\tfib -c | -s -h <host> -p <port> " +
                    "\n\t-c | --client \t\t\t startup parameter: client" +
                    "\n\t-s | --server \t\t\t startup parameter: server" +
                    "\n\t-p | --port \t\t\t port number" +
                    "\n\t-h | --host \t\t\t host number (ignored when startup parameter is server)"
        )
        exitProcess(-1)
    }
    when (parameters["type"]) {
        "server" -> Server(parameters["port"]!!).startServer()
        "client" -> Client(parameters["host"]!!, parameters["port"]!!).startClient()
    }
}


class Client(private val host: String, private val port: String) {

    private lateinit var clientSocket: Socket
    private lateinit var reader: BufferedReader
    private lateinit var inputStream: BufferedReader
    private lateinit var outputStream: BufferedWriter


    fun startClient() {
        try {
            init()
            println("Enter the number or the empty string to exit: ")
            while (true) {
                print("> ")
                val word: String? = reader.readLine()
                if (word == null || word == "") {
                    write("exit")
                    shutdown()
                }
                write(word!!)
                val serverWord: String? = inputStream.readLine()
                serverWord?.let {
                    println(serverWord)
                }
            }
        } catch (e: ConnectException) {
            println(e.message)
            shutdown()
        }
    }

    private fun write(message: String) {
        outputStream.write((message + '\n'))
        outputStream.flush()
    }

    private fun init() {
        try {
            clientSocket = Socket(host, port.toInt())
            reader = BufferedReader(InputStreamReader(System.`in`))
            inputStream = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            outputStream = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))
        } catch (e: Exception) {
            println(e.message)
            exitProcess(1)
        }
    }


    private fun shutdown() {
        try {
            inputStream.close()
            outputStream.close()
            clientSocket.close()
        } catch (e: Exception) {
            println(e.message)
        } finally {
            println("Closed")
            exitProcess(0)
        }
    }
}

fun cmdArgumentsParser(args: Array<String>): Map<String, String> {

    val parameters = HashMap<String, String>()
    val portRegex = "^([1-9]|[1-5]?[0-9]{2,4}|6[1-4][0-9]{3}|65[1-4][0-9]{2}|655[1-2][0-9]|6553[1-5])$".toRegex()
    val hostRegex =
        "^(((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))|(localhost)$".toRegex()

    var i = 0
    try {
        while (i < args.size) {
            when (args[i]) {
                "-s", "--server" -> {
                    if (!parameters.containsKey("type")) {
                        parameters["type"] = "server"
                        i++
                    } else throw IllegalArgumentException("Input parameter must be either server or client!")
                }
                "-c", "--client" -> {
                    if (!parameters.containsKey("type")) {
                        parameters["type"] = "client"
                        i++
                    } else throw IllegalArgumentException("Input parameter must be either server or client!")
                }
                "-p", "--port" -> {
                    parameters["port"] = args[i + 1]
                    i++
                }
                "-h", "--host" -> {
                    parameters["host"] = args[i + 1]
                    i++
                }
                else -> i++
            }
        }
    } catch (e: ArrayIndexOutOfBoundsException) {
        throw IllegalArgumentException("Missing parameter!")
    }

    if (!(parameters.containsKey("type") && parameters["type"].equals("client") &&
                parameters.containsKey("host") && parameters.containsKey("port")) &&
        !(parameters.containsKey("type") && parameters["type"].equals("server") &&
                parameters.containsKey("port"))
    )
        throw IllegalArgumentException("One ore more of parameters are missing!")

    if (!parameters["port"]?.matches(portRegex)!!)
        throw IllegalArgumentException("Port is incorrect!")

    parameters["host"]?.let {
        if (!parameters["host"]?.matches(hostRegex)!!)
            throw IllegalArgumentException("Host is incorrect!")
    }

    return parameters
}

class Server(private val port: String) {


    fun startServer() {
        try {
            val serverSocket = ServerSocket(port.toInt())
            println("Server is running on port ${serverSocket.localPort}")
            serverSocket.use { server ->
                while (true) {
                    val socket = server.accept()
                    try {
                        println("New connection from ${socket.inetAddress.hostAddress}")
                        thread { ClientHandler(socket).run() }
                    } catch (e: IOException) {
                        socket.close()
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    class ClientHandler(private val socket: Socket) : Runnable {

        private val fibonacci = FastFibonacci()
        private var active = true
        private val inputStream = Scanner(socket.getInputStream())
        private val outputStream = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))

        override fun run() {
            try {
                while (active) {
                    val input: String? = inputStream.nextLine()
                    if (input == "exit")
                        shutdown()
                    else {
                        input?.let {
                            when (isValidNumber(input)) {
                                true -> write(fibonacci.fastFibonacci(input.toInt()).toString())
                                false -> write("Invalid number! Numbers must be positive or zero. Please try again!")
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                shutdown()
            }
        }

        private fun write(message: String) {
            outputStream.write((message + '\n'))
            outputStream.flush()
        }

        private fun isValidNumber(input: String?): Boolean {
            return when (input?.toIntOrNull()) {
                null -> false
                else -> input.toInt() >= 0
            }
        }

        private fun shutdown() {
            try {
                socket.close()
                inputStream.close()
                outputStream.close()
            } catch (e: Exception) {
                println(e.message)
            } finally {
                active = false
                println("${socket.inetAddress.hostAddress} closed the connection")
            }
        }

        inner class FastFibonacci {

            fun fastFibonacci(n: Int): BigInteger {
                val matrix = arrayOf(BigInteger.ONE, BigInteger.ONE, BigInteger.ONE, BigInteger.ZERO)
                return matrixPow(matrix, n)[1]
            }

            private fun matrixPow(matrixArg: Array<BigInteger>, nArg: Int): Array<BigInteger> {
                var matrix = matrixArg
                var n = nArg
                var result = arrayOf(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE)
                while (n != 0) {
                    if (n % 2 != 0) result = matrixMultiply(result, matrix)
                    n /= 2
                    matrix = matrixMultiply(matrix, matrix)
                }
                return result
            }

            private fun matrixMultiply(x: Array<BigInteger>, y: Array<BigInteger>): Array<BigInteger> {
                return arrayOf(
                    Multiply(x[0], y[0]).add(Multiply(x[1], y[2])),
                    Multiply(x[0], y[1]).add(Multiply(x[1], y[3])),
                    Multiply(x[2], y[0]).add(Multiply(x[3], y[2])),
                    Multiply(x[2], y[1]).add(Multiply(x[3], y[3]))
                )
            }

            private fun Multiply(x: BigInteger, y: BigInteger): BigInteger {
                return x.multiply(y)
            }
        }
    }
}
