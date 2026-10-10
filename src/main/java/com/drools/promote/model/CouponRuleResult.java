package com.drools.promote.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回值
 *
 * @author laizhihui
 */
public class CouponRuleResult {
    /**
     * 优惠券计算后最终价格
     */
    private double finallyMoney;
    /**
     * 未使用优惠券时的价格，原始价格
     */
    private double moneySum;

    /**
     * 使用优惠券码集合，有可能可以使用多种多张优惠券
     */
    private List<String> useCouponCodes=new ArrayList<String>();

    public double getFinallyMoney() {
        return finallyMoney;
    }

    public void setFinallyMoney(double finallyMoney) {
        this.finallyMoney = finallyMoney;
    }

    public double getMoneySum() {
        return moneySum;
    }

    public void setMoneySum(double moneySum) {
        this.moneySum = moneySum;
    }

    public List<String> getUseCouponCodes() {
        return useCouponCodes;
    }

    public void setUseCouponCodes(String useCouponCode) {
        this.useCouponCodes.add(useCouponCode);
    }
}
