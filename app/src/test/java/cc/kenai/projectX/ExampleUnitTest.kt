package cc.kenai.projectX

import org.junit.Test

import org.junit.Assert.*
import rx.Observable
import rx.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        printArray(factory(4, 1))
        printArray(factory(4, 2))
        printArray(factory(4, 3))
        printArray(factory(4, 4))
        printArray(factory(4, 5))


        assertEquals(4, 2 + 2)
    }

    fun factory(nubOfColumns: Int, numOfLine: Int): Array<Array<Int>> = Array(numOfLine) { j ->
        Array<Int>(nubOfColumns) { i ->
            if (numOfLine <= 3) {
                i * numOfLine + j
            } else {
                if (j < 3) {
                    i * 3 + j
                } else {
                    nubOfColumns * j + i
                }
            }
        }
    }

    fun printArray(array: Array<Array<Int>>) {
        val sb = StringBuilder()
        for (line in array) {
            sb.append("[")
            for (p in line) {
                sb.append(p).append(" ")
            }
            sb.append("]").append("\n")
        }
        System.out.println(sb.trimEnd())
    }

    @Test
    fun testRx() {
        val start = System.currentTimeMillis()
        var subject = PublishSubject.create<String>()
        subject
                .delay(5,TimeUnit.SECONDS)
                .map {
                    println(it)
                    println(System.currentTimeMillis()-start)
                }
                .subscribe({ print("subscribe") })
        subject.onNext("test1")
        subject.onNext("test2")
        subject.onNext("test3")

        Thread.sleep(10000)
    }
}
