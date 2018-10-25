grammar MyGrammar;

grammarFile
    : grammarHeader? grammarMembers? grammarRule+
    ;

grammarHeader
    : '@' 'header' BracesBlock
    ;

grammarMembers
    : '@' 'members' BracesBlock
    ;

grammarRule
    : grammarParserRule
    | grammarLexerRule
    ;

/* PARSER RULES */

grammarParserRule
    : name=ParserRuleName (args=BracketsBlock)? (Returns ret=BracketsBlock)? (modifiers+=ruleModifier)* ':' parserAlternativeList ';';

parserAlternativeList
    : rules+=parserAlternative ('|' rules+=parserAlternative)*
    ;

parserAlternative
    : parserRulePart*
    ;

parserRulePart
    : (ParserRuleName AssignOperator)? parserRuleToken RuleOperator? BracesBlock?
    ;

parserRuleToken
    : LexerRuleName
    | ParserRuleName BracketsBlock?
    | SingleQuoteString
    | '(' parserAlternativeList ')'
    ;

/* LEXER RULES */

grammarLexerRule
    : 'fragment'? name=LexerRuleName ':' lexerAlternativeList (modifiers+=ruleModifier)* ';';

lexerAlternativeList
    : rules+=lexerAlternative ('|' rules+=lexerAlternative)*
    ;

lexerAlternative
    : lexerRulePart* (Arrow Skip)?
    ;

lexerRulePart
    : lexerRuleToken RuleOperator?
    ;

lexerRuleToken
    : LexerRuleName
    | SingleQuoteString
    | RegExp
    | '(' lexerAlternativeList ')'
    ;

ruleModifier
    : '@' 'init' BracesBlock
    | '@' 'after' BracesBlock
    ;


RegExp: 'RegExp(' DoubleQuoteString ')';

// nestedParenthesesBlock: (~('(' | ')') | '(' nestedParenthesesBlock ')')*;
// ParenthesesBlock: '(' (~['"()] | SingleQuoteString | DoubleQuoteString | ParenthesesBlock)* ')';
BracesBlock: '{' (~['"{}] | SingleQuoteString | DoubleQuoteString | BracesBlock)* '}';
BracketsBlock: '[' (~['"[\]] | SingleQuoteString | DoubleQuoteString | BracketsBlock)* ']';

Colon: ':';
Semicolon: ';';
Returns: 'returns';
At: '@';
Fragment: 'fragment';
Arrow: '->';
Skip: 'skip';

AssignOperator
    : Assign
    | AddAssign
    ;

Assign: '=';
AddAssign: '+=';

SingleQuoteString: '\'' (~['] | '\\\'')* '\'';
DoubleQuoteString: '"' (~["] | '\\"')* '"';
RuleOperator: [?+*];
LexerRuleName: UppercaseLetter IdentifierChar*;
ParserRuleName: LowercaseLetter IdentifierChar*;
Identifier: IdentifierChar+;

fragment
IdentifierChar
    : Digit
    | Letter
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