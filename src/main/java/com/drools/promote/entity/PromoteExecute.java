package com.drools.promote.entity;

import static com.drools.promote.util.NewKieBase.rulekieBase;
import org.kie.api.KieBase;
import org.kie.api.runtime.StatelessKieSession;

import java.util.List;
/**
 * @author laizhihui
 */
public class PromoteExecute {
    //促销编号：
    private String promoteCode;
    //业务Kbase
    private KieBase workKbase;
    //业务session
    private StatelessKieSession workSession;
    //规则内容
    private String workContent;
    //促销规则名称集合：
    private List<String> ruleName;
    //促销规则名称
    private String promoteName;

    public String getPromoteName() {
        return promoteName;
    }

    public void setPromoteName(String promoteName) {
        this.promoteName = promoteName;
    }

    public String getWorkContent() {
        return workContent;
    }

    public void setWorkContent(String workContent) {
        this.workContent = workContent;
    }

    public List<String> getRuleName() {
        return ruleName;
    }

    public void setRuleName(List<String> ruleName) {
        this.ruleName = ruleName;
    }


    public String getPromoteCode() {
        return promoteCode;
    }

    public void setPromoteCode(String promoteCode) {
        this.promoteCode = promoteCode;
    }

    public KieBase getWorkKbase() {
        if (this.workKbase == null) {
            this.setWorkKbase();
        }
        return workKbase;
    }

    public void setWorkKbase() {
        this.workKbase = rulekieBase(this.getWorkContent());
    }

    public StatelessKieSession getWorkSession() {
        if (this.workSession == null) {
            this.setWorkSession();
        }
        return workSession;
    }

    public void setWorkSession() {
        if (null != this.getWorkKbase()) {
            this.workSession = this.getWorkKbase().newStatelessKieSession();
        }
    }
}
