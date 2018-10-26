package ru.ifmo.translators.grammar;

public abstract class Token {

    private Repeat repeat;
    private String customName, customOp, code;

    Repeat getRepeat() {
        return repeat;
    }

    Token setRepeat(Repeat repeat) {
        this.repeat = repeat;
        return this;
    }

    public String getCustomName() {
        return customName;
    }

    public Token setCustomName(String customName) {
        this.customName = customName;
        return this;
    }

    public String getCustomOp() {
        return customOp;
    }

    public Token setCustomOp(String customOp) {
        this.customOp = customOp;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Token setCode(String code) {
        this.code = code;
        return this;
    }

    enum Repeat {
        MAYBE("?"),
        ONCE(""),
        SOME("+"),
        ANY("*");

        String operator;

        Repeat(String operator) {
            this.operator = operator;
        }

        public static Repeat get(String operator) {
            for (Repeat r : Repeat.values()) {
                if (operator.equals(r.operator)) {
                    return r;
                }
            }
            throw new AssertionError("Wrong repeat operator");
        }
    }
}
