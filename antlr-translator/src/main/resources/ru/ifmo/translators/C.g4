grammar C;

// https://en.wikipedia.org/wiki/Statement_(computer_science)

program
    returns [String code]
    //@init {$builder = new StringBuilder();}
    : (units+=unit)* EOF
    ;

unit
    : function
    | declaration
    | directive
    | ';' // stray ';'
    ;

function
    :   declarationSpecifiers? declarator declarationList? compoundStatement
    ;

declaration
    :   declarationSpecifiers initDeclaratorList? ';'
    ;

directive
    : DirectiveToken
    ;

DirectiveToken
    : '#' Whitespace? [a-z]+ Whitespace ~[\r\n]*
    ;

declarationList
    :   declaration
    |   declarationList declaration
    ;

declarationSpecifiers
    :   declarationSpecifier+
    ;

declarationSpecifier
    :   storageClassSpecifier
    |   typeSpecifier
    |   typeQualifier
    ;

storageClassSpecifier
    : 'typedef'
    | 'extern'
    | 'static'
    | 'auto'
    | 'register'
    ;

typeSpecifier
    : ( 'void'
      | 'char'
      | 'short'
      | 'int'
      | 'long'
      | 'float'
      | 'double'
      | 'signed'
      | 'unsigned')
    | structOrUnionSpecifier
    | enumSpecifier
    | typedefName
    | typeSpecifier pointer
    ;

typeQualifier
    : 'const'
    | 'volatile'
    ;





















primaryExpression
    :   Identifier
    |   Constant
    |   StringLiteral+
    |   '(' expression ')'
    ;

genericAssocList
    :   genericAssociation
    |   genericAssocList ',' genericAssociation
    ;

genericAssociation
    :   typeName ':' assignmentExpression
    |   'default' ':' assignmentExpression
    ;

postfixExpression
    :   primaryExpression
    |   postfixExpression '[' expression ']'
    |   postfixExpression '(' argumentExpressionList? ')'
    |   postfixExpression '.' Identifier
    |   postfixExpression '->' Identifier
    |   postfixExpression '++'
    |   postfixExpression '--'
    |   '(' typeName ')' '{' initializerList '}'
    |   '(' typeName ')' '{' initializerList ',' '}'
    ;

argumentExpressionList
    :   assignmentExpression
    |   argumentExpressionList ',' assignmentExpression
    ;

unaryExpression
    :   postfixExpression
    |   '++' unaryExpression
    |   '--' unaryExpression
    |   unaryOperator castExpression
    |   'sizeof' unaryExpression
    |   'sizeof' '(' typeName ')'
    |   '&&' Identifier // GCC extension address of label
    ;

unaryOperator
    :   '&' | '*' | '+' | '-' | '~' | '!'
    ;

castExpression
    :   '(' typeName ')' castExpression
    |   '__extension__' '(' typeName ')' castExpression
    |   unaryExpression
    |   DigitSequence // for
    ;

multiplicativeExpression
    :   castExpression
    |   multiplicativeExpression '*' castExpression
    |   multiplicativeExpression '/' castExpression
    |   multiplicativeExpression '%' castExpression
    ;

additiveExpression
    :   multiplicativeExpression
    |   additiveExpression '+' multiplicativeExpression
    |   additiveExpression '-' multiplicativeExpression
    ;

shiftExpression
    :   additiveExpression
    |   shiftExpression '<<' additiveExpression
    |   shiftExpression '>>' additiveExpression
    ;

relationalExpression
    :   shiftExpression
    |   relationalExpression '<' shiftExpression
    |   relationalExpression '>' shiftExpression
    |   relationalExpression '<=' shiftExpression
    |   relationalExpression '>=' shiftExpression
    ;

equalityExpression
    :   relationalExpression
    |   equalityExpression '==' relationalExpression
    |   equalityExpression '!=' relationalExpression
    ;

andExpression
    :   equalityExpression
    |   andExpression '&' equalityExpression
    ;

exclusiveOrExpression
    :   andExpression
    |   exclusiveOrExpression '^' andExpression
    ;

inclusiveOrExpression
    :   exclusiveOrExpression
    |   inclusiveOrExpression '|' exclusiveOrExpression
    ;

logicalAndExpression
    :   inclusiveOrExpression
    |   logicalAndExpression '&&' inclusiveOrExpression
    ;

logicalOrExpression
    :   logicalAndExpression
    |   logicalOrExpression '||' logicalAndExpression
    ;

conditionalExpression
    :   logicalOrExpression ('?' expression ':' conditionalExpression)?
    ;

assignmentExpression
    :   conditionalExpression
    |   unaryExpression assignmentOperator assignmentExpression
    |   DigitSequence // for
    ;

assignmentOperator
    :   '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
    ;

expression
    :   assignmentExpression
    |   expression ',' assignmentExpression
    ;

constantExpression
    :   conditionalExpression
    ;



initDeclaratorList
    :   initDeclarator
    |   initDeclaratorList ',' initDeclarator
    ;

initDeclarator
    :   declarator
    |   declarator '=' initializer
    ;



structOrUnionSpecifier
    :   structOrUnion Identifier? '{' structDeclaration+ '}'
    |   structOrUnion Identifier
    ;

structOrUnion
    :   'struct'
    |   'union'
    ;

structDeclaration
    :   specifierQualifierList structDeclaratorList? ';'
    ;

specifierQualifierList
    :   typeSpecifier specifierQualifierList?
    |   typeQualifier specifierQualifierList?
    ;

structDeclaratorList
    :   structDeclarator
    |   structDeclaratorList ',' structDeclarator
    ;

structDeclarator
    :   declarator
    |   declarator? ':' constantExpression
    ;

enumSpecifier
    :   'enum' Identifier? '{' enumeratorList '}'
    |   'enum' Identifier? '{' enumeratorList ',' '}'
    |   'enum' Identifier
    ;

enumeratorList
    :   enumerator
    |   enumeratorList ',' enumerator
    ;

enumerator
    :   enumerationConstant
    |   enumerationConstant '=' constantExpression
    ;

enumerationConstant
    :   Identifier
    ;


declarator
    :   pointer? directDeclarator
    ;

directDeclarator
    :   Identifier
    |   '(' declarator ')'
    |   directDeclarator '[' typeQualifierList? assignmentExpression? ']'
    |   directDeclarator '[' 'static' typeQualifierList? assignmentExpression ']'
    |   directDeclarator '[' typeQualifierList 'static' assignmentExpression ']'
    |   directDeclarator '[' typeQualifierList? '*' ']'
    |   directDeclarator '(' parameterTypeList ')'
    |   directDeclarator '(' identifierList? ')'
    |   Identifier ':' DigitSequence  // bit field
    |   '(' typeSpecifier? pointer directDeclarator ')' // function pointer like: (__cdecl *f)
    ;

nestedParenthesesBlock
    :   (   ~('(' | ')')
        |   '(' nestedParenthesesBlock ')'
        )*
    ;

pointer
    :   '*' typeQualifierList?
    |   '*' typeQualifierList? pointer
    |   '^' typeQualifierList? // Blocks language extension
    |   '^' typeQualifierList? pointer // Blocks language extension
    ;

typeQualifierList
    :   typeQualifier+
    ;

parameterTypeList
    :   parameterList (',' '...')?
    ;

parameterList
    :   parameterDeclaration
    |   parameterList ',' parameterDeclaration
    ;

parameterDeclaration
    :   declarationSpecifiers (declarator | abstractDeclarator?)
    ;

identifierList
    :   Identifier
    |   identifierList ',' Identifier
    ;

typeName
    :   specifierQualifierList abstractDeclarator?
    ;

abstractDeclarator
    :   pointer
    |   pointer? directAbstractDeclarator
    ;

directAbstractDeclarator
    :   '(' abstractDeclarator ')'
    |   '[' typeQualifierList? assignmentExpression? ']'
    |   '[' 'static' typeQualifierList? assignmentExpression ']'
    |   '[' typeQualifierList 'static' assignmentExpression ']'
    |   '[' '*' ']'
    |   '(' parameterTypeList? ')'
    |   directAbstractDeclarator '[' typeQualifierList? assignmentExpression? ']'
    |   directAbstractDeclarator '[' 'static' typeQualifierList? assignmentExpression ']'
    |   directAbstractDeclarator '[' typeQualifierList 'static' assignmentExpression ']'
    |   directAbstractDeclarator '[' '*' ']'
    |   directAbstractDeclarator '(' parameterTypeList? ')'
    ;

typedefName
    :   Identifier
    ;

initializer
    :   assignmentExpression
    |   '{' initializerList '}'
    |   '{' initializerList ',' '}'
    ;

initializerList
    :   designation? initializer
    |   initializerList ',' designation? initializer
    ;

designation
    :   designatorList '='
    ;

designatorList
    :   designator
    |   designatorList designator
    ;

designator
    :   '[' constantExpression ']'
    |   '.' Identifier
    ;

statement
    :   labeledStatement
    |   compoundStatement
    |   expressionStatement
    |   selectionStatement
    |   iterationStatement
    |   jumpStatement
    ;

labeledStatement
    :   Identifier ':' statement
    |   'case' constantExpression ':' statement
    |   'default' ':' statement
    ;

compoundStatement
    :   '{' blockItemList? '}'
    ;

blockItemList
    :   blockItem+
    ;

blockItem
    :   statement
    |   declaration
    ;

expressionStatement
    :   expression? ';'
    ;

selectionStatement
    :   'if' '(' expression ')' statement ('else' statement)?
    |   'switch' '(' expression ')' statement
    ;

iterationStatement
    :   While '(' expression ')' statement
    |   Do statement While '(' expression ')' ';'
    |   For '(' forCondition ')' statement
    ;

//    |   'for' '(' expression? ';' expression?  ';' forUpdate? ')' statement
//    |   For '(' declaration  expression? ';' expression? ')' statement

forCondition
	:   forDeclaration ';' forExpression? ';' forExpression?
	|   expression? ';' forExpression? ';' forExpression?
	;

forDeclaration
    :   declarationSpecifiers initDeclaratorList
	| 	declarationSpecifiers
    ;

forExpression
    :   assignmentExpression
    |   forExpression ',' assignmentExpression
    ;

jumpStatement
    :   'goto' Identifier ';'
    |   'continue' ';'
    |   'break' ';'
    |   'return' expression? ';'
    |   'goto' unaryExpression ';' // GCC extension
    ;






Auto : 'auto';
Break : 'break';
Case : 'case';
Char : 'char';
Const : 'const';
Continue : 'continue';
Default : 'default';
Do : 'do';
Double : 'double';
Else : 'else';
Enum : 'enum';
Extern : 'extern';
Float : 'float';
For : 'for';
Goto : 'goto';
If : 'if';
Inline : 'inline';
Int : 'int';
Long : 'long';
Register : 'register';
Restrict : 'restrict';
Return : 'return';
Short : 'short';
Signed : 'signed';
Sizeof : 'sizeof';
Static : 'static';
Struct : 'struct';
Switch : 'switch';
Typedef : 'typedef';
Union : 'union';
Unsigned : 'unsigned';
Void : 'void';
Volatile : 'volatile';
While : 'while';

Alignas : '_Alignas';
Alignof : '_Alignof';
Atomic : '_Atomic';
Bool : '_Bool';
Complex : '_Complex';
Generic : '_Generic';
Imaginary : '_Imaginary';
Noreturn : '_Noreturn';
StaticAssert : '_Static_assert';
ThreadLocal : '_Thread_local';

LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';
LeftShift : '<<';
RightShift : '>>';

Plus : '+';
PlusPlus : '++';
Minus : '-';
MinusMinus : '--';
Star : '*';
Div : '/';
Mod : '%';

And : '&';
Or : '|';
AndAnd : '&&';
OrOr : '||';
Caret : '^';
Not : '!';
Tilde : '~';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

Assign : '=';
// '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
StarAssign : '*=';
DivAssign : '/=';
ModAssign : '%=';
PlusAssign : '+=';
MinusAssign : '-=';
LeftShiftAssign : '<<=';
RightShiftAssign : '>>=';
AndAssign : '&=';
XorAssign : '^=';
OrAssign : '|=';

Equal : '==';
NotEqual : '!=';

Arrow : '->';
Dot : '.';
Ellipsis : '...';

Identifier
    : IdentifierNondigit (IdentifierNondigit | Digit)*
    ;

fragment
IdentifierNondigit
    : Nondigit
    | UniversalCharacterName
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
    :   IntegerConstant
    |   FloatingConstant
    //|   EnumerationConstant
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
    :   '\\' ['"?abfnrtv\\]
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

StringLiteral
    :   EncodingPrefix? '"' SCharSequence? '"'
    ;

fragment
EncodingPrefix
    :   'u8'
    |   'u'
    |   'U'
    |   'L'
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

Newline
    : ('\r' '\n'? | '\n') -> skip
    ;

BlockComment
    : '/*' .*? '*/' -> skip
    ;

LineComment
    : '//' ~[\r\n]* -> skip
    ;
