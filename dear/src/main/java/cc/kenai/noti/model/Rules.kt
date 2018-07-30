package cc.kenai.noti.model

import android.content.Context
import com.google.gson.Gson
import java.io.*

data class Rule(var title: String, var text: String, var pkg_limit: String, var type: String) {

    fun update(rule: Rule) {
        this.title = rule.title
        this.text = rule.text
        this.pkg_limit = rule.pkg_limit
        this.type = rule.type
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}

data class RuleRex(var title: Regex, var text: Regex, var pkg_limit: Regex, var type: String) {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}


object RulesFactory {

    private val mGson: Gson

    init {
        mGson = Gson()
    }

    fun rule2json(rule: Rule): String {
        return mGson.toJson(rule)
    }

    fun json2rule(json: String): Rule {
        return mGson.fromJson<Rule>(json, Rule::class.java)
    }

    fun loadRules(context: Context): Array<Rule> {
        val rulesConfig = context.getSharedPreferences("config", 0).getString("rules", null)
        if (rulesConfig != null) {
            try {
                val rules = RulesFactory.loadRulesFromString(rulesConfig)
                return rules
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val inputStream = context.getAssets().open("default_rules")
        try {
            val rules = RulesFactory.loadRulesFromInputStream(inputStream)
            return rules
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream.close()
        }
        return emptyArray()
    }

    private fun loadRulesFromString(s: String): Array<Rule> {
        val rules = ArrayList<Rule>()
        val lines = s.split("\n")
        for (everyLine in lines) {
            val rule = line2rule(everyLine)
            if (rule != null) {
                rules.add(rule)
            }
        }
        return rules.toTypedArray()
    }

    private fun loadRulesFromInputStream(input: InputStream): Array<Rule> {
        val rules = ArrayList<Rule>()
        val bufferReader = BufferedReader(InputStreamReader(input))
        try {
            var everyLine = bufferReader.readLine()
            while (everyLine != null) {
                val rule = line2rule(everyLine)
                if (rule != null) {
                    rules.add(rule)
                }
                everyLine = bufferReader.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bufferReader.close()
        }
        return rules.toTypedArray()
    }

    fun saveRulesToFile(rules: Array<Rule>, file: File) {
        val fileWriter = FileWriter(file)
        val bufferedWriter = BufferedWriter(fileWriter)
        rules.forEach {
            bufferedWriter.write(rule2json(it))
        }
    }

    private inline fun line2rule(everyLine: String): Rule? {
        if (everyLine.isNotEmpty()) {
            return mGson.fromJson<Rule>(everyLine, Rule::class.java)
        }
        return null
    }
}