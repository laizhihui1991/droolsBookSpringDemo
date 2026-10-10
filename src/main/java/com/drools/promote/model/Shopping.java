package com.drools.promote.model;

/**
 * 购物车
 *
 * @author laizhihui
 */
public class Shopping {

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 价格
     */
    private double price;

    /**
     * 数量
     */
    private int number;

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
