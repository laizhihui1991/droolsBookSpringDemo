package com.drools.promote.controller;

import com.drools.promote.service.PromoteEdieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
@Controller
@RequestMapping("/promotion")
public class ShopController {

    @Autowired
    private PromoteEdieService promoteEdieService;


    @RequestMapping("/greeting")
    public String greeting() {
        return "index";
    }


    @RequestMapping("/shop")
    public String shop() {
        return "shopping";
    }


    /**
     * 编辑促销活动
     *
     * @return 结果
     */
    @RequestMapping(value = "/ediePromote", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String ediePromote(String money, String rulename) {
        promoteEdieService.ediePromomteMap(money, rulename);
        return "生成促销活动成功";
    }

    /**
     * 购物车
     *
     * @return 返回结果
     */
    @RequestMapping(value = "/toShopping", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Map<String, Object> toShopping(String money) {
        Map<String, Object> data = promoteEdieService.toShopping(money);
        return data;
    }
}