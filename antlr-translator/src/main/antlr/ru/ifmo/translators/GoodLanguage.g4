grammar GoodLanguage;

@members {
    static <T> String stringify(java.util.List<T> list, java.util.function.Function<? super T, String> fn, String delim) {
        return list.stream().map(fn).collect(java.util.stream.Collectors.joining(delim));
    }
}

program // OK
    : Newline* unit*
    ;

unit // OK
    : declaration Newline*
    | function Newline*
    | Directive Newline*
    ;

declaration // OK
    : 'var' 'const'? lvalue ':' type '=' initializer Newline
    ;

function // OK
    : 'function' Identifier '(' arguments? ')' ':' type compoundStatement
    ;

type // OK
    : 'void' Star*
    | 'char' Star*
    | 'short' Star*
    | 'int' Star*
    | 'long' Star*
    | 'float' Star*
    | 'double' Star*
    | 'signed' Star*
    | 'unsigned' Star*
    | 'string' Star*
    ;

initializer
    : assignmentExpression
    | '{' initializer initializerCont* '}'
    | tupleAssignment
    ;

initializerCont
    : ',' initializer
    ;

arguments
    : argument argumentCont*
    ;

argumentCont
    : ',' argument
    ;

argument // OK
    : type Identifier
    ;

lvalue // OK
    : tuple
    | Identifier
    ;

tuple // OK
    : '[' Identifier+ ']'
    ;

compoundStatement // OK
    : '{' item* '}'
    ;

item // OK
    : statement Newline*
    | declaration Newline*
    ;

statement
    : labeledStatement
    | compoundStatement
    | expression? Newline
    | selectionStatement
    | iterationStatement
    | jumpStatement
    ;

labeledStatement // OK
    : Identifier ':' statement
    | 'case' constantExpression ':' statement
    | 'default' ':' statement
    ;

selectionStatement // OK
    : 'if' '(' expression ')' statement elseStatement?
    | 'switch' '(' expression ')' statement
    ;

elseStatement
    : 'else' statement
    ;

iterationStatement // OK
    : 'while' '(' expression ')' statement
    | 'do' statement While '(' expression ')' Newline
    | 'for' '(' forCondition ')' statement
    ;

forCondition // OK
	: declaration ';' assignmentExpression? ';' assignmentExpression?
	| expression? ';' assignmentExpression? ';' assignmentExpression?
	;

jumpStatement // OK
    : 'goto' Identifier Newline
    | 'continue' Newline
    | 'break' Newline
    | 'return' expression? Newline
    ;









primaryExpression // OK
    : Identifier
    | Constant
    | StringLiteral
    | '(' expression ')'
    ;

postfixExpression // TODO Left recursive
    :   primaryExpression
    |   postfixExpression '[' expression ']'
    |   postfixExpression '(' argumentExpressionList? ')'
    |   postfixExpression '.' Identifier
    |   postfixExpression '->' Identifier
    |   postfixExpression '++'
    |   postfixExpression '--'
    ;

argumentExpressionList
    : assignmentExpression
    | argumentExpressionList ',' assignmentExpression
    ;

unaryExpression
    :   postfixExpression
    |   '++' unaryExpression
    |   '--' unaryExpression
    |   '&' castExpression
    |   '*' castExpression
    |   '+' castExpression
    |   '-' castExpression
    |   '~' castExpression
    |   '!' castExpression
    |   'sizeof' unaryExpression
    |   'sizeof' '(' type ')'
    ;

castExpression
    : '(' type ')' castExpression
    | unaryExpression
    ;

multiplicativeExpression
    : castExpression
    | multiplicativeExpression '*' castExpression
    | multiplicativeExpression '/' castExpression
    | multiplicativeExpression '%' castExpression
    ;

additiveExpression
    : multiplicativeExpression
    | additiveExpression '+' multiplicativeExpression
    | additiveExpression '-' multiplicativeExpression
    ;

shiftExpression
    : additiveExpression
    | shiftExpression '<<' additiveExpression
    | shiftExpression '>>' additiveExpression
    ;

relationalExpression
    : shiftExpression
    | relationalExpression '<' shiftExpression
    | relationalExpression '>' shiftExpression
    | relationalExpression '<=' shiftExpression
    | relationalExpression '>=' shiftExpression
    ;

equalityExpression
    : relationalExpression
    | equalityExpression '==' relationalExpression
    | equalityExpression '!=' relationalExpression
    ;

andExpression
    : equalityExpression
    | andExpression '&' equalityExpression
    ;

exclusiveOrExpression
    : andExpression
    | exclusiveOrExpression '^' andExpression
    ;

inclusiveOrExpression
    : exclusiveOrExpression
    | inclusiveOrExpression '|' exclusiveOrExpression
    ;

logicalAndExpression
    : inclusiveOrExpression
    | logicalAndExpression '&&' inclusiveOrExpression
    ;

logicalOrExpression
    : logicalAndExpression
    | logicalOrExpression '||' logicalAndExpression
    ;

conditionalExpression
    : logicalOrExpression conditionalExpressionCont?
    ;

conditionalExpressionCont
    : '?' expression ':' conditionalExpression
    ;

assignmentExpression
    : conditionalExpression
    | unaryExpression assignmentOperator assignmentExpression
    ;

assignmentOperator
    :   '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
    ;

expression
    : assignmentExpression
    | expression ',' assignmentExpression
    | tupleExpression
    ;

constantExpression
    :   conditionalExpression
    ;

tupleExpression
    : tuple '=' tupleAssignment
    ;

tupleAssignment
    : 'read' '(' argumentExpressionList? ')'
    | 'print' '(' argumentExpressionList? ')'
    | tuple
    ;






Newline
    : '\r\n'
    | '\r'
    | '\n'
    ;

BlockComment
    : '/*' (~[*] | [*] ~[/])* '*/' -> skip
    ;

LineComment
    : '//' ~[\r\n]* -> skip
    ;

StringLiteral
    : '"' SCharSequence? '"'
    ;

Break: 'break';
Case: 'case';
Char: 'char';
Const: 'const';
Continue: 'continue';
Default: 'default';
Do: 'do';
Double: 'double';
Else: 'else';
Float: 'float';
For: 'for';
Function: 'function';
Goto: 'goto';
If: 'if';
Int: 'int';
Long: 'long';
Print: 'print';
Read: 'read';
Return: 'return';
Short: 'short';
Signed: 'signed';
Sizeof: 'sizeof';
Static: 'static';
String: 'string';
Struct: 'struct';
Switch: 'switch';
Typedef: 'typedef';
Union: 'union';
Unsigned: 'unsigned';
Var: 'var';
Void: 'void';
Volatile: 'volatile';
While: 'while';

LeftParen: '(';
RightParen: ')';
LeftBracket: '[';
RightBracket: ']';
LeftBrace: '{';
RightBrace: '}';


LeftShift: '<<';
LessEqual: '<=';
Less: '<';

RightShift: '>>';
GreaterEqual: '>=';
Greater: '>';

Arrow: '->';


Equal: '==';
Assign: '=';
StarAssign: '*=';
DivAssign: '/=';
ModAssign: '%=';
PlusAssign: '+=';
MinusAssign: '-=';
LeftShiftAssign: '<<=';
RightShiftAssign: '>>=';
AndAssign: '&=';
XorAssign: '^=';
OrAssign: '|=';
NotEqual: '!=';

PlusPlus: '++';
Plus: '+';
MinusMinus: '--';
Minus: '-';
Star: '*';
Div: '/';
Mod: '%';


AndAnd: '&&';
And: '&';
OrOr: '||';
Or: '|';
Caret: '^';
Not: '!';
Tilde: '~';

Question: '?';
Colon: ':';
Semicolon: ';';
Comma: ',';

Ellipsis: '...';
Dot: '.';

Directive
    : '#' (~[\\\r\n] | [\\] [\r\n])* Newline
    ;






















Identifier
    : IdentifierNondigit (IdentifierNondigit | Digit)*
    ;

fragment
IdentifierNondigit
    :   Nondigit
    |   UniversalCharacterName
    ;

fragment
Nondigit
    :   [a-zA-Z_]
    ;

fragment
Digit
    :   [0-9]
    ;

fragment
UniversalCharacterName
    :   '\\u' HexQuad
    |   '\\U' HexQuad HexQuad
    ;

fragment
HexQuad
    :   HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit
    ;

Constant
    :   FloatingConstant
    |   IntegerConstant
    |   CharacterConstant
    ;

fragment
IntegerConstant
    :   DecimalConstant IntegerSuffix?
    |   OctalConstant IntegerSuffix?
    |   HexadecimalConstant IntegerSuffix?
    |	BinaryConstant
    ;

fragment
BinaryConstant
	:	'0' [bB] [0-1]+
	;

fragment
DecimalConstant
    :   NonzeroDigit Digit*
    ;

fragment
OctalConstant
    :   '0' OctalDigit*
    ;

fragment
HexadecimalConstant
    :   HexadecimalPrefix HexadecimalDigit+
    ;

fragment
HexadecimalPrefix
    :   '0' [xX]
    ;

fragment
NonzeroDigit
    :   [1-9]
    ;

fragment
OctalDigit
    :   [0-7]
    ;

fragment
HexadecimalDigit
    :   [0-9a-fA-F]
    ;

fragment
IntegerSuffix
    :   UnsignedSuffix LongSuffix?
    |   UnsignedSuffix LongLongSuffix
    |   LongSuffix UnsignedSuffix?
    |   LongLongSuffix UnsignedSuffix?
    ;

fragment
UnsignedSuffix
    :   [uU]
    ;

fragment
LongSuffix
    :   [lL]
    ;

fragment
LongLongSuffix
    :   'll' | 'LL'
    ;

fragment
FloatingConstant
    :   DecimalFloatingConstant
    |   HexadecimalFloatingConstant
    ;

fragment
DecimalFloatingConstant
    :   FractionalConstant ExponentPart? FloatingSuffix?
    |   DigitSequence ExponentPart FloatingSuffix?
    ;

fragment
HexadecimalFloatingConstant
    :   HexadecimalPrefix HexadecimalFractionalConstant BinaryExponentPart FloatingSuffix?
    |   HexadecimalPrefix HexadecimalDigitSequence BinaryExponentPart FloatingSuffix?
    ;

fragment
FractionalConstant
    :   DigitSequence? '.' DigitSequence
    |   DigitSequence '.'
    ;

fragment
ExponentPart
    :   'e' Sign? DigitSequence
    |   'E' Sign? DigitSequence
    ;

fragment
Sign
    :   '+' | '-'
    ;

DigitSequence
    :   Digit+
    ;

fragment
HexadecimalFractionalConstant
    :   HexadecimalDigitSequence? '.' HexadecimalDigitSequence
    |   HexadecimalDigitSequence '.'
    ;

fragment
BinaryExponentPart
    :   'p' Sign? DigitSequence
    |   'P' Sign? DigitSequence
    ;

fragment
HexadecimalDigitSequence
    :   HexadecimalDigit+
    ;

fragment
FloatingSuffix
    :   'f' | 'l' | 'F' | 'L'
    ;

fragment
CharacterConstant
    :   '\'' CCharSequence '\''
    |   'L\'' CCharSequence '\''
    |   'u\'' CCharSequence '\''
    |   'U\'' CCharSequence '\''
    ;

fragment
CCharSequence
    :   CChar+
    ;

fragment
CChar
    :   ~['\\\r\n]
    |   EscapeSequence
    ;

fragment
EscapeSequence
    :   SimpleEscapeSequence
    |   OctalEscapeSequence
    |   HexadecimalEscapeSequence
    |   UniversalCharacterName
    ;

fragment
SimpleEscapeSequence
    :   '\\' ['"?abf0nrtv\\]
    ;

fragment
OctalEscapeSequence
    :   '\\' OctalDigit
    |   '\\' OctalDigit OctalDigit
    |   '\\' OctalDigit OctalDigit OctalDigit
    ;

fragment
HexadecimalEscapeSequence
    :   '\\x' HexadecimalDigit+
    ;

fragment
SCharSequence
    :   SChar+
    ;

fragment
SChar
    :   ~["\\\r\n]
    |   EscapeSequence
    |   '\\\n'   // Added line
    |   '\\\r\n' // Added line
    ;

Whitespace
    : [ \t]+ -> skip
    ;

