grammar MyGrammar;

grammarFile
    : grammarUnit+
    ;

grammarUnit
    : myParserRule
    | myLexerRule
    ;

myParserRule
    : ParserRuleName parserRuleModifier* ':' parserAlternativeList ';';

parserRuleModifier
    : 'returns' '[' ']'
    | '@' 'init' '{' '}'
    | '@' 'after' '{' '}'
    ;

parserAlternativeList
    : parserAlternative ('|' parserAlternative)*
    ;

parserAlternative
    : parserRulePart*
    ;

parserRulePart
    : (ParserRuleName '=')? ParserRuleName RuleOperator?
    | (ParserRuleName '=')? LexerRuleName RuleOperator?
    ;


myLexerRule
    : LexerRuleName lexerRuleModifier* ':' lexerAlternativeList ';';

lexerRuleModifier
    : '@' 'after' '{' '}'
    ;

lexerAlternativeList
    : lexerAlternative ('|' lexerAlternative)*
    ;

lexerAlternative
    : lexerRulePart*
    ;

lexerRulePart
    : LexerRuleName RuleOperator?
    | StringLiteral RuleOperator?
    | RegExp RuleOperator?
    ;





Colon: ':';
Semicolon: ';';

RegExp
    : 'RegExp(' StringLiteral ')'
    ;

StringLiteral
    : '\'' (~['] | '\\\'')*  '\''
    ;

RuleOperator
    : [?+*]
    ;

LexerRuleName
    : UppercaseLetter IdentifierChar*
    ;

ParserRuleName
    : LowercaseLetter IdentifierChar*
    ;

fragment
IdentifierChar
    : Digit
    | NonDigit
    ;

fragment
NonDigit
    : LowercaseLetter
    | UppercaseLetter
    | Underscore
    ;

fragment
Letter
    : UppercaseLetter
    | LowercaseLetter
    ;

fragment
UppercaseLetter
    : [A-Z]
    ;

fragment
LowercaseLetter
    : [a-z]
    ;

fragment
Digit
    : [0-9]
    ;

fragment
Underscore
    : [_]
    ;




Whitespace
    : [ \t]+ -> skip
    ;

Newline
    : ('\r\n' | '\r' | '\n') -> skip
    ;

BlockComment
    : '/*' .*? '*/' -> skip
    ;

LineComment
    : '//' ~[\r\n]* -> skip
    ;