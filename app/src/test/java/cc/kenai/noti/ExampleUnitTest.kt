package cc.kenai.noti

import cc.kenai.noti.model.RulesFactory
import com.google.gson.Gson
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val test = """
        #{"title":"sunshine","text":"*","pkg_limit":"com.tencent.mm","type":"noti|loop|ring|screennoti|loop|ring|screen"}
        #{"title":"西小北","text":"*","pkg_limit":"com.alibaba.android.rimet","type":"noti|loop|ring|screen"}
        #{"title":"testTitle","text":"testText","pkg_limit":"com.meizu.input.test","type":"noti|loop|ring|screen"}
        """.trimMargin("#")


    @Test
    fun addition_isCorrect() {
        val gson = Gson()

        //RulesFactory().loadRulesFromString(test)

        RulesFactory.loadRulesFromString(test).forEach {
            println(gson.toJson(it))
        }
    }
}
