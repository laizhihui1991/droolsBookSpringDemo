# droolsBookSpringBootDemo

基于 Spring Boot + Drools 的动态促销规则示例工程。可以在运行时通过页面录入“优惠券”（名称 + 减免金额），后台用 StringTemplate 模板动态生成 DRL 规则并编译成 KieBase；购物车结算时依次执行所有已生成的规则，计算优惠后的金额。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| JDK | 1.8 |
| Spring Boot | 2.0.1.RELEASE（Web、Thymeleaf、Log4j2） |
| Drools | 7.40.0.Final（drools-compiler） |
| StringTemplate (ST4) | 4.0.8 |
| fastjson | RELEASE |
| JUnit | 4.11 |

## 项目结构

```
src/main/java/com/drools/promote
├── Application.java                 # Spring Boot 启动类
├── controller/ShopController.java   # 页面跳转与接口（/promotion/**）
├── service/PromoteEdieService.java  # 生成促销规则、购物车计算
├── entity/
│   ├── PromoteExecute.java          # 促销执行体：规则内容、KieBase、StatelessKieSession
│   └── RuleResult.java              # 规则 Fact：原价、优惠后价格、命中的活动名称
└── util/
    ├── RuleUtil.java                # 组装规则 JSON，并用 ST4 模板渲染为 DRL
    ├── NewKieBase.java              # 使用 KieHelper 将 DRL 字符串编译为 KieBase
    └── DrlExecute.java              # 插入 RuleResult 并批量执行规则
src/main/resources
├── application.properties           # 端口 8090，Thymeleaf 模板路径
└── templates/
    ├── index.html                   # 创建优惠券页面
    └── shopping.html                # 购物车结算页面
```

## 运行

```powershell
mvn spring-boot:run
```

或打包后运行：

```powershell
mvn clean package
java -jar target/droolsBookSpringBootDemo-1.0-SNAPSHOT.jar
```

服务端口为 `8090`。

## 使用流程

1. 访问 `http://localhost:8090/promotion/greeting`，填写“优惠券金额”和“优惠券名称”并提交，生成一条促销规则（可重复提交生成多条）。
2. 访问 `http://localhost:8090/promotion/shop`，填写“购物车总金额”并提交，返回计算结果。

## 接口说明

| 方法 | 路径 | 参数 | 说明 |
| --- | --- | --- | --- |
| GET | `/promotion/greeting` | - | 返回 `index.html` 页面 |
| GET | `/promotion/shop` | - | 返回 `shopping.html` 页面 |
| POST | `/promotion/ediePromote` | `money`（减免金额）、`rulename`（规则名称） | 生成促销规则，返回 `生成促销活动成功` |
| POST | `/promotion/toShopping` | `money`（购物车总金额） | 执行全部规则，返回优惠结果 |

调用示例：

```powershell
curl.exe -X POST -F "money=10" -F "rulename=满10减" http://localhost:8090/promotion/ediePromote
curl.exe -X POST -F "money=100" http://localhost:8090/promotion/toShopping
```

`toShopping` 返回示例（已生成一条减 10 的规则）：

```json
{
  "moneySumYuanJia": "100",
  "youhuiquanjiegou": [
    { "moneySum": 100.0, "finallyMoney": 90.0, "promoteName": ["满10减"] }
  ]
}
```

- `moneySumYuanJia`：优惠前原价
- `youhuiquanjiegou`：每条规则的执行结果；上一条规则的 `finallyMoney` 会作为下一条规则的 `moneySum`，即多张优惠券叠加使用

## 核心实现

1. `RuleUtil.ruleWorkMap` 将规则名称与金额组装为包含 `rule` / `condition` / `action` 的 JSON。
2. `RuleUtil.ruleWordExchangsST` 用 ST4 模板 `wordImport` / `ruleValue` 渲染出 DRL，生成结果类似：

   ```
   package com.drools.promote

   import com.drools.promote.entity.RuleResult;
   rule "满10减"
       no-loop true
       when
           $r:RuleResult(true)
       then
           modify($r){
               setPromoteName(drools.getRule().getName()),
               setFinallyMoney($r.getMoneySum() - 10.0)
           }
   end
   ```

3. `NewKieBase.rulekieBase` 通过 `KieHelper.addContent(drl, ResourceType.DRL)` 编译出 KieBase，`PromoteExecute` 懒加载创建 `StatelessKieSession`。
4. `PromoteEdieService` 以“日期 + 7 位随机数”生成促销编码，把 `PromoteExecute` 缓存到内存 Map。
5. `DrlExecute.rulePromote` 通过 `CommandFactory.newInsert` + `newBatchExecution` 将 `RuleResult` 插入会话执行，得到优惠后价格。

## 注意事项

- 规则仅保存在内存（`HashMap`）中，重启后丢失；`HashMap` 非线程安全，多张优惠券的执行顺序也不固定。
- 规则没有使用条件判断（`RuleResult(true)`），任何金额都会命中，优惠后金额可能为负数。
- 参数未做校验，`money` 非数字时会抛出 `NumberFormatException`。
- 接口没有任何鉴权，仅适用于本地学习演示。
