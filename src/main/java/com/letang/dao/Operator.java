package com.letang.dao;

public enum Operator {
    EQ("="), NEQ("<>"), GT(">"), LT("<"), GTE(">="), LTE("<=");
    private String value;

    private Operator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
