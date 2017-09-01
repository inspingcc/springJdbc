package com.letang.dao;
/**
 * 条件单元
 * @author houshanping
 *
 */
public class ConditionUnit {
    private String key;
    private Operator op = Operator.EQ;
    private Object value;

    public ConditionUnit(String key, Operator op, Object value) {
        this.key = key;
        this.op = op;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Operator getOp() {
        return op;
    }

    public void setOp(Operator op) {
        this.op = op;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
