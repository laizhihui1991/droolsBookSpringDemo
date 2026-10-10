# droolsBookSpringDemo

Drools 7 与 Spring 4 整合的示例工程。通过 Spring XML（`kie-spring` 命名空间）声明 KieBase / KieSession，并在测试类中用 `@KBase`、`@KSession` 注解注入，演示一个简单的“满减优惠券”规则计算。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| JDK | 1.8 |
| Drools（drools-compiler / kie-spring） | 7.40.0.Final |
| Spring（core / beans / context / test / tx 等） | 4.2.6.RELEASE |
| JUnit | 4.11 |
| 构建工具 | Maven |

## 目录结构

```
droolsBookSpringDemo
├── pom.xml
└── src
    ├── main
    │   ├── java/com/drools/promote
    │   │   ├── del                         # 占位文件
    │   │   └── model
    │   │       ├── CouponRuleResult.java   # 规则计算的输入/输出对象（Fact）
    │   │       └── Shopping.java           # 购物车商品模型（当前规则未使用）
    │   └── resources
    │       ├── spring.xml                  # Spring + KIE 配置
    │       └── rules
    │           ├── del                     # 占位文件
    │           └── coupon/coupon.drl       # 优惠券规则
    └── test/java
        └── DroolsSpringTest.java           # 整合测试
```

## 核心说明

### 1. Spring 配置（`spring.xml`）

```xml
<kie:kmodule id="springKieKmodule">
    <kie:kbase name="couponKbase" packages="rules.coupon">
        <kie:ksession name="couponKsession"/>
    </kie:kbase>
</kie:kmodule>
<bean id="kiePostProcessor" class="org.kie.spring.annotations.KModuleAnnotationPostProcessor"/>
```

- `kie:kmodule` 的作用与 `META-INF/kmodule.xml` 相同，因此工程中不需要 `kmodule.xml`。
- `couponKbase` 加载 `rules/coupon` 目录下的规则文件，`packages` 的值对应 classpath 下的资源路径。
- `couponKsession` 是该 KieBase 下的有状态会话。
- `KModuleAnnotationPostProcessor` 负责处理 `@KBase`、`@KSession` 注解的注入。

### 2. 数据模型

- `CouponRuleResult`
  - `moneySum`：原始价格（未使用优惠券）
  - `finallyMoney`：优惠后的最终价格
  - `useCouponCodes`：使用的优惠券码集合。注意 `setUseCouponCodes(String)` 实际是向集合中追加一个元素，而不是整体替换。
- `Shopping`：商品名称 `commodityName`、价格 `price`、数量 `number`，目前没有规则引用它。

### 3. 规则（`coupon.drl`）

| 规则名 | salience | 条件 | 结果 |
| --- | --- | --- | --- |
| 优惠券满足500减50 | 50 | `moneySum > 500` | `finallyMoney = moneySum - 50` |
| 优惠券满足300减30 | 30 | `moneySum > 300` | `finallyMoney = moneySum - 30` |
| 优惠券满足100减10 | 10 | `moneySum > 100` | `finallyMoney = moneySum - 10` |

- 通过 `salience` 控制优先级，金额满足多条规则时优先执行减免最多的那一条。
- 每条规则执行后调用 `drools.halt()` 立即停止引擎，保证只会命中一条规则（不叠加优惠）。
- 条件使用的是严格大于 `>`，所以刚好等于 500 / 300 / 100 时不会命中对应档位。

### 4. 测试（`DroolsSpringTest`）

使用 `SpringJUnit4ClassRunner` 加载 `classpath:spring.xml`，注入方式：

```java
@KSession("couponKsession")
KieSession ksession;

@KBase("couponKbase")
KieBase kBase;
```

注解中的名称必须与 `spring.xml` 中配置的名称一致。

- `testKsession`：直接使用注入的 `KieSession`，`moneySum = 300`。
- `testKbase`：通过注入的 `KieBase` 新建 `KieSession`，`moneySum = 500`。

根据规则逻辑，预期结果如下：

| 测试 | moneySum | 命中规则 | finallyMoney |
| --- | --- | --- | --- |
| testKsession | 300 | 优惠券满足100减10 | 290.0 |
| testKbase | 500 | 优惠券满足300减30 | 470.0 |

## 运行

```bash
# 运行全部测试
mvn test

# 只运行某一个测试方法
mvn test -Dtest=DroolsSpringTest#testKsession
```

也可以在 IDE 中直接运行 `DroolsSpringTest`，控制台会输出执行的规则数量和最终价格。
