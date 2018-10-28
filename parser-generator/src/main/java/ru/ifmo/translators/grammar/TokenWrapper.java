package ru.ifmo.translators.grammar;

public class TokenWrapper {

    private final Token token;
    private final Token.Repeat repeat;
    private final String customName, customOp, code;

    public TokenWrapper(ParserToken token, Token.Repeat repeat, String customName, String customOp, String code) {
        this.token = token;
        this.repeat = repeat;
        this.customName = customName;
        this.customOp = customOp;
        this.code = code;
    }

    public TokenWrapper(LexerToken token, Token.Repeat repeat, String code) {
        this.token = token;
        this.repeat = repeat;
        this.code = code;
        this.customName = null;
        this.customOp = null;
    }

    public Token getToken() {
        return token;
    }

    public Token.Repeat getRepeat() {
        return repeat;
    }

    public String getCustomName() {
        return customName;
    }

    public String getCustomOp() {
        return customOp;
    }

    public String getCode() {
        return code;
    }
}
