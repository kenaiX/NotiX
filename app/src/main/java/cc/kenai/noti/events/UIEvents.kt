package cc.kenai.noti.events

import cc.kenai.noti.model.Rule

class RuleCommit(val rule: Rule)
class RulesChanged(val rule: Array<Rule>)