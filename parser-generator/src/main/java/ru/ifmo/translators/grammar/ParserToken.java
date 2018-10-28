package ru.ifmo.translators.grammar;

public interface ParserToken extends Token {

    enum Operator {
        SET("="),
        ADD("+="),
        NONE("");

        private String operator;

        Operator(String operator) {
            this.operator = operator;
        }

        public static Operator get(String operator) {
            for (Operator op : Operator.values()) {
                if (operator.equals(op.operator)) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Wrong assign operator: " + operator);
        }

        @Override
        public String toString() {
            return operator;
        }
    }
}
