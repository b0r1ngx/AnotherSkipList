
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals


class Tests {
    @Test
    fun smoothTestCreatePrintAddSearchRemove() {
        val skipList = SkipList<Int>()
        println("Print list")
        skipList.print()
        val eList = mutableListOf<Int>()
        for (i in 0 .. 20) {
            val e = Random.Default.nextInt(1, 100)
            eList.add(e)
            println("=== List after adding $e ===")
            skipList.add(e)
            skipList.print()
            println("=====================")
        }
        for (i in 0 .. 10) {
            val e = eList[i]
            skipList.remove(e)
            println("=== List after remove $e ===")
            skipList.print()
            println("=====================")
        }
        for (i in 11 .. 20) {
            val e = eList[i]
            assertEquals(true, skipList.contains(e), )
        }
        for (i in 0 .. 10) {
            val e = eList[i]
            assertEquals(false, skipList.contains(e))
        }
    }

    @RepeatedTest(5)
    fun repeatedTest() {
        println("Этот тест будет запущен пять раз. ")
    }
}