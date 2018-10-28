package ru.ifmo.translators.grammar;

public interface Token {

    enum Repeat {
        MAYBE("?"),
        ONCE(""),
        SOME("+"),
        ANY("*");

        private String operator;

        Repeat(String operator) {
            this.operator = operator;
        }

        public static Repeat get(String operator) {
            for (Repeat r : Repeat.values()) {
                if (operator.equals(r.operator)) {
                    return r;
                }
            }
            throw new IllegalArgumentException("Wrong repeat operator: " + operator);
        }

        @Override
        public String toString() {
            return operator;
        }
    }
}
