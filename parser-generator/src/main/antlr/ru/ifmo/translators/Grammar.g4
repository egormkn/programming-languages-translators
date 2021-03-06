grammar Grammar;

grammarFile
    : 'grammar' name=LexerRuleName ';'
      ('@' 'header' header=BracesBlock)?
      ('@' 'members' members=BracesBlock)?
      ('@' 'footer' footer=BracesBlock)?
      (parserRules+=grammarParserRule | lexerRules+=grammarLexerRule)+
      EOF
    ;

grammarParserRule
    : name=ParserRuleName
      (args=BracketsBlock)?
      (Returns ret=BracketsBlock)?
      ('@' 'init' init=BracesBlock)?
      ('@' 'after' after=BracesBlock)? ':'
      alternative=parserAlternative ';';

parserAlternative
    : sequences+=parserSequence ('|' sequences+=parserSequence)*
    ;

parserSequence
    : (wrappers+=parserTokenWrapper)*
    ;

parserTokenWrapper
    : (customName=ParserRuleName customOp=AssignOperator)?
      token=parserToken (repeat=RuleOperator)?
    | code=BracesBlock
    ;

parserToken
    : lexerRuleName=LexerRuleName
    | parserRuleName=ParserRuleName (args=BracketsBlock)?
    | string=SingleQuoteString
    | '(' alternative=parserAlternative ')'
    ;

/* LEXER RULES */

grammarLexerRule
    : (isFragment=Fragment)?
      name=LexerRuleName ':'
      alternative=lexerAlternative
      (Arrow isSkip=Skip)? ';';

lexerAlternative
    : sequences+=lexerSequence ('|' sequences+=lexerSequence)*
    ;

lexerSequence
    : (wrappers+=lexerTokenWrapper)*
    ;

lexerTokenWrapper
    : token=lexerToken (repeat=RuleOperator)?
    | code=BracesBlock
    ;

lexerToken
    : lexerRuleName=LexerRuleName
    | string=SingleQuoteString
    | (inverse='~')? charset=BracketsBlock
    | '(' alternative=lexerAlternative ')'
    ;


BracesBlock: '{' (~['"{}] | SingleQuoteString | DoubleQuoteString | BracesBlock)* '}' {
    String text = getText();
    setText(text.substring(1, text.length() - 1));
};

BracketsBlock: '[' (~[\\\]] | [\\] . )* ']' {
    String text = getText();
    setText(text.substring(1, text.length() - 1));
};

Colon: ':';
Semicolon: ';';
Returns: 'returns';
At: '@';
Fragment: 'fragment';
Arrow: '->';
Skip: 'skip';
Not: '~';

AssignOperator
    : Assign
    | AddAssign
    ;

Assign: '=';
AddAssign: '+=';

SingleQuoteString: '\'' (~[\\'] | [\\] . )+ '\'' {
    String text = getText();
    setText(text.substring(1, text.length() - 1));
};

DoubleQuoteString: '"' (~[\\"] | [\\] . )* '"' {
    String text = getText();
    setText(text.substring(1, text.length() - 1));
};

RuleOperator: [?+*];
LexerRuleName: UppercaseLetter Char*;
ParserRuleName: LowercaseLetter Char*;

fragment
Char
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