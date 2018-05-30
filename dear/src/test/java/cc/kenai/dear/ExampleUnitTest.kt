package cc.kenai.dear

import cc.kenai.dear.model.Record
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val test = """
        #1|0|20180305|问题|答案
        #2|0|20180306|问题2|答案2
        """.trimMargin("#")


    @Test
    fun addition_isCorrect() {
        val split = test.split("\n")
        for (s in split) {
            val record = Record.fromString(s)
            println(record.toString())
        }
    }
}
