package com.drools.promote.util;

import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupString;

/**
 * @author laizhihui
 */
public class RuleUtil{

    public static String rule(String ruleName,double v) {
        String ruleJson = ruleWorkMap(ruleName, v);
        return ruleWordExchangsST(ruleJson);
    }

    /**
     * 规则业务生成
     */
    private static String ruleWordExchangsST(String json) {
        STGroup group = new STGroupString(workMoneyST);
        ST stFile = group.getInstanceOf("wordImport");
        ST stRule = group.getInstanceOf("ruleValue");
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONObject condition = jsonObject.getJSONObject("condition");
        JSONObject action = jsonObject.getJSONObject("action");
        JSONObject rule = jsonObject.getJSONObject("rule");
        stRule.add("condition", condition);
        stRule.add("action", action);
        stRule.add("rule", rule);
        stFile.add("rules", stRule);
        String result = stFile.render();
        return result;
    }

    /**
     * 组合业务规则Json方法
     *
     * @return 结果
     */
    private static String ruleWorkMap(String name, Double money) {
        Map<String, Object> map = new HashMap<>();
        //组合Rule部分
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        map.put("rule", rule);
        //组合 规则When部分
        Map<String, Object> when = new HashMap<>();
        map.put("condition", when);
        //组合 规则Then部分
        Map<String, Object> then = new HashMap<>();
        then.put("money", money);
        map.put("action", then);
        //组合规则When And Then 部分
        return JSONObject.toJSONString(map);
    }

    private static final String workMoneyST = "wordImport(rules) ::=<<\n" +
        "package com.drools.promote\n" +
        "\n" +
        "import\tcom.drools.promote.entity.RuleResult;\n" +
        "<rules; separator=\"\\n\\n\">\n" +
        ">>\n" +
        "\n" +
        "ruleValue(condition,action,rule) ::=<<\n" +
        "rule \"<rule.name>\"\n" +
        "\tno-loop true\n" +
        "\t\twhen\n" +
        "\t\t    $r:RuleResult(true)\n" +
        " \t\tthen\n" +
        "           modify($r){\n" +
        "                setPromoteName(drools.getRule().getName())<if(action)>,\n" +
        "                setFinallyMoney($r.getMoneySum() - <action.money><endif>)\n" +
        "           }\n" +
        "end\n" +
        ">>\n";
}
