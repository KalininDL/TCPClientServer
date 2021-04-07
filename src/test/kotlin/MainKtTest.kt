import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

import java.lang.IllegalArgumentException
import kotlin.test.assertFailsWith
import kotlin.text.split

class MainKtTest {

    private fun stringToCMDArray(s : String) : Array<String>{
        return s.split(" ").toTypedArray()
    }

    @Test
    fun cmdArgumentsParserTest() {
        assertFailsWith<IllegalArgumentException> {
            cmdArgumentsParser(stringToCMDArray("--server -h localhost -p"))
        }
        assertFailsWith<IllegalArgumentException> {
            cmdArgumentsParser(stringToCMDArray("--server localhost -p"))
        }
        assertFailsWith<IllegalArgumentException> {
            cmdArgumentsParser(stringToCMDArray("--server"))
        }
        assertFailsWith<IllegalArgumentException> {
            cmdArgumentsParser(stringToCMDArray("--client"))
        }
        assertFailsWith<IllegalArgumentException> {
            cmdArgumentsParser(stringToCMDArray("--serve -h localhost -p"))
        }
        assertFailsWith<IllegalArgumentException> {
            cmdArgumentsParser(stringToCMDArray("--server -h 192.168.88.1 --p 2222"))
        }
        assertFailsWith<IllegalArgumentException> {
            cmdArgumentsParser(stringToCMDArray("-s -h localhost -p 99999"))
        }
    }

    @Test
    fun cmdArgumentsParserPostitiveTest() {
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("--server -h localhost -p 4444"))
        }
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("--server --host localhost --port 4444"))
        }
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("-s -h localhost -p 4444"))
        }
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("--server -h 192.168.1.1 -p 234"))
        }
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("--server --host localhost --port 90"))
        }
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("-s -h 10.10.10.10 -p 8080"))
        }
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("-h 10.10.10.10 -p 8080 -s"))
        }
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("-h 10.10.10.10 -p 8080 -c"))
        }
        assertDoesNotThrow {
            cmdArgumentsParser(stringToCMDArray("-h 10.10.10.10 -p 8080 --client"))
        }
    }
}