package com.drools.promote.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.drools.promote.entity.PromoteExecute;
import com.drools.promote.entity.RuleResult;
import com.drools.promote.util.DrlExecute;
import com.drools.promote.util.RuleUtil;
import org.springframework.stereotype.Service;


/**
 * @author laizhihui
 */
@Service
public class PromoteEdieService {

    private Map<String, PromoteExecute> promoteExecuteMap;
    private final static SimpleDateFormat sdfThree = new SimpleDateFormat("yyyyMMdd");

    /**
     * 生成优惠券
     */
    public void ediePromomteMap(String money, String rulename) {
        if (this.promoteExecuteMap == null) {
            promoteExecuteMap = new HashMap<>(8);
        }
        PromoteExecute promoteExecute;
        promoteExecute = new PromoteExecute();
        double v = Double.parseDouble(money);
        String rule = RuleUtil.rule(rulename,v);
        String promoteCode = typeJoinTime();
        promoteExecute.setPromoteCode(promoteCode);
        promoteExecute.setWorkContent(rule);
        promoteExecute.setPromoteName(rulename);
        //生成优惠券
        PromoteExecute execute =editRule(rule);
        this.promoteExecuteMap.put(promoteCode, execute);
    }

    /**
     * 编辑规则
     *
     * @param rule 促销实体数据
     * @return 返回值
     */
    public PromoteExecute editRule(String rule) throws RuntimeException {
        PromoteExecute promoteExecute = new PromoteExecute();
        //促销业务规则
        promoteExecute.setWorkContent(rule);
        //规则库 初始化
        promoteExecute.getWorkSession();
        return promoteExecute;
    }

    /**
     * 生成不重复促销编码
     *
     * @return
     */
    public static String typeJoinTime() {
        String dateNowStr = sdfThree.format(new Date());
        Integer math = (int) ((Math.random() * 9 + 1) * 1000000);
        String code = dateNowStr.concat(math.toString());
        return code;
    }

    /**
     * 购物车计算
     *
     * @return
     */
    public Map<String, Object> toShopping(String moneySum) {
        //购物车请求信息
        Map<String, Object> map = new HashMap<>();
        double v = Double.parseDouble(moneySum);
        List<Object> pn = new ArrayList<>();
        if (this.promoteExecuteMap != null) {
            //证明有优惠券
            for (Map.Entry<String, PromoteExecute> codes : this.promoteExecuteMap.entrySet()) {
                RuleResult ruleResult = DrlExecute.rulePromote(codes.getValue(), v);
                v = ruleResult.getFinallyMoney();
                pn.add(ruleResult);
            }
        }
        map.put("moneySumYuanJia", moneySum);
        map.put("youhuiquanjiegou", pn);
        return map;
    }
}