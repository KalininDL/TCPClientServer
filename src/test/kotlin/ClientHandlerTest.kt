import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.net.Socket
import kotlin.test.assertEquals

internal class ClientHandlerTest {

    fun isValidNumber(s: String?): Boolean {
        return when (s?.toIntOrNull()){
            null -> false
            else -> s.toInt() > 0
        }
    }
    @Test
    fun isValidNumberTest() {
        assertAll({
            assertEquals(true, isValidNumber("1"))
            assertEquals(true, isValidNumber("999"))
            assertEquals(true, isValidNumber("123"))
            assertEquals(false, isValidNumber("-1"))
            assertEquals(false, isValidNumber("1 0"))
            assertEquals(false, isValidNumber("-100"))
        })
    }
}