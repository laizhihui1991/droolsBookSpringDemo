import com.drools.promote.model.CouponRuleResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Drools整合Spring的测试类
 *
 * @author laizhihui
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring.xml"})
public class DroolsSpringTest {

    //注： 这里的值与配置文件中的值是一样的
    @KSession("couponKsession")
    KieSession ksession;
    //注： 这里的值与配置文件中的值是一样
    @KBase("couponKbase")
    KieBase kBase;

    @Test
    public void testKsession() {
        CouponRuleResult couponRuleResult=new CouponRuleResult();
        couponRuleResult.setMoneySum(300);
        ksession.insert(couponRuleResult);
        int count = ksession.fireAllRules();
        System.out.println("总执行了" + count + "条规则");
        System.out.println("最后的价格="+couponRuleResult.getFinallyMoney());
        ksession.dispose();
    }

    @Test
    public void testKbase() {
        KieSession kieSession = kBase.newKieSession();
        CouponRuleResult couponRuleResult=new CouponRuleResult();
        couponRuleResult.setMoneySum(500);
        kieSession.insert(couponRuleResult);
        int count = kieSession.fireAllRules();
        System.out.println("总执行了" + count + "条规则");
        System.out.println("最后的价格="+couponRuleResult.getFinallyMoney());
        kieSession.dispose();
    }
}
