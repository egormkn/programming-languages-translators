package ru.ifmo.translators.grammar;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Token {

    private Repeat repeat;
    private String customName, customOp, code;

    public Repeat getRepeat() {
        return repeat;
    }

    public String getRepeatOp() {
        return repeat.operator;
    }

    Token setRepeat(Repeat repeat) {
        this.repeat = repeat;
        return this;
    }

    public String getCustomName() {
        return customName;
    }

    Token setCustomName(String customName) {
        this.customName = customName;
        return this;
    }

    public String getCustomOp() {
        return customOp;
    }

    Token setCustomOp(String customOp) {
        this.customOp = customOp;
        return this;
    }

    public String getCode() {
        return code;
    }

    Token setCode(String code) {
        this.code = code;
        return this;
    }

    public enum Repeat {
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
            throw new IllegalArgumentException("Wrong repeat operator");
        }
    }
}
