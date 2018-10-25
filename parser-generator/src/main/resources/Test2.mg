// https://en.wikipedia.org/wiki/Statement_(computer_science)

// https://github.com/antlr/antlr4/blob/master/doc/parser-rules.md#rule-attribute-definitions
// https://github.com/Shemplo/Study-courses/blob/master/Translation%20methods/3.%20Translator/grammar/pascal.g4

@header {
}

@members {
    static <T> String stringify(java.util.List<T> list, java.util.function.Function<? super T, String> fn, String delim) {
        return list.stream().map(fn).collect(java.util.stream.Collectors.joining(delim));
    }

    static String spaces(int length) {
        return new String(new char[length]).replace('\0', ' ');
    }

    static StringBuilder builder() {
        return new StringBuilder();
    }

    static FilteredStringJoiner joiner(String delim) {
        return new FilteredStringJoiner(delim);
    }

    static String nullable(String given, String def) {
        return given != null ? given : def;
    }

    static String nullable(String pref, String given, String suff, String def) {
        return given != null ? (pref + given + suff) : def;
    }
}

program // OK
    returns [String code]
    : (units+=unit)* EOF
        {
            $code = stringify($units, u -> u.code, "\n\n");
            System.out.println($code);
        }
    ;

unit // OK
    returns [String code]
    : function
        {$code = $function.code;}
    | declaration ';'
        {$code = $declaration.code + ";";}
    | directive
        {$code = $directive.code;}
    | ';' // stray ';' character
    ;

function // OK
    returns [String code]
    : (specifiers+=declarationSpecifier)* declarator (declarations+=declaration ';')* compoundStatement[true]
        {
            $code = joiner(" ")
                    .add(stringify($specifiers, s -> s.code, " "))
                    .add($declarator.code)
                    .add(stringify($declarations, d -> d.code + ";", " "))
                    .add($compoundStatement.code)
                    .toString();
        }
    ;

declaration
    returns [String code]
    : (specifiers+=declarationSpecifier)+ initDeclaratorList?
        {
            $code = stringify($specifiers, s -> s.code, " ")
                    + " " + $initDeclaratorList.code;
        }
    ;

directive
    returns [String code]
    : DirectiveToken
        {$code = $DirectiveToken.text.substring(0, $DirectiveToken.text.length() - 1);}
    ;

declarationSpecifier
    returns [String code]
    : StorageClassSpecifier {$code = $StorageClassSpecifier.text;}
    | typeSpecifier {$code = $typeSpecifier.code;}
    | TypeQualifier {$code = $TypeQualifier.text;}
    ;

typeSpecifier
    returns [String code]
    : BasicTypeSpecifier {$code = $BasicTypeSpecifier.text;}
    | structOrUnionSpecifier {$code = $structOrUnionSpecifier.text;}
    | enumSpecifier {$code = $enumSpecifier.text;}
    | Identifier {$code = $Identifier.text;} // typedef
    | pointer_type=typeSpecifier pointer {$code = $pointer_type.code + $pointer.text;}
    ;

initDeclaratorList
    returns [String code]
    : declarators+=initDeclarator (',' declarators+=initDeclarator)*
        {$code = stringify($declarators, d -> d.code, ", ");}
    ;

initDeclarator
    returns [String code]
    : declarator '=' initializer
        {$code = $declarator.code + " = " + $initializer.text;}
    | declarator
        {$code = $declarator.code;}
    ;

declarator
    returns [String code]
    : pointer directDeclarator
        {$code = $pointer.text + " " + $directDeclarator.text;}
    | directDeclarator
        {$code = $directDeclarator.text;}
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

compoundStatement[boolean outer] // OK
    returns [String code]
    @init {
        $level = outer ? 0 : ($item::level + 1);
    }
    : '{' (items+=item)* '}'
        {
            $code = joiner("\n")
                    .add("{")
                    .add(stringify($items, u -> spaces($level * 4 + 4) + u.code, "\n"))
                    .add(spaces($level * 4) + "}")
                    .toString();
        }
    ;

item // OK
    returns [String code]
    @init {
        $level = $compoundStatement::level;
    }
    : statement
        {$code = $statement.code;}
    | declaration ';'
        {$code = $declaration.code + ";";}
    ;

















primaryExpression
    returns [String code]
    @after {
        $code = $text;
    }
    : Identifier
    | Constant
    | StringLiteral+
    | '(' expression ')'
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
    :   assignmentExpression (',' assignmentExpression)*
    ;

unaryExpression
    returns [String code]
    @after {
        $code = $text;
    }
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
    returns [String code]
    @after {$code = $text;}
    :   logicalOrExpression ('?' expression ':' conditionalExpression)?
    ;

assignmentExpression
    returns [String code]
    : conditionalExpression
        {$code = $conditionalExpression.text;}
    | unaryExpression assignmentOperator assignmentExpression
        {$code = $unaryExpression.text + $assignmentOperator.text + $assignmentExpression.text;}
    | DigitSequence // for
        {$code = $DigitSequence.text;}
    ;

assignmentOperator
    :   '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
    ;

expression
    returns [String code]
    : expressions+=assignmentExpression (',' expressions+=assignmentExpression)*
        {$code = stringify($expressions, e -> e.code, ", ");}
    ;

constantExpression // OK
    returns [String code]
    : conditionalExpression
        {$code = $conditionalExpression.code;}
    ;







structOrUnionSpecifier
    :   StructOrUnion Identifier? '{' structDeclaration+ '}'
    |   StructOrUnion Identifier
    ;


structDeclaration
    :   specifierQualifierList structDeclaratorList? ';'
    ;

specifierQualifierList
    :   (TypeQualifier | typeSpecifier)+
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
    : Identifier
    | Identifier '=' constantExpression
    ;







pointer
    : ('*' | '^') TypeQualifier* pointer?
    ;

typeQualifierList
    : TypeQualifier+
    ;

parameterTypeList
    :   parameterList (',' '...')?
    ;

parameterList
    :   parameterDeclaration
    |   parameterList ',' parameterDeclaration
    ;

parameterDeclaration
    :   declarationSpecifier+ (declarator | abstractDeclarator?)
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

statement // OK
    returns [String code]
    : labeledStatement
        {$code = $labeledStatement.code;}
    | compoundStatement[false]
        {$code = $compoundStatement.code;}
    | expressionStatement
        {$code = $expressionStatement.code;}
    | selectionStatement
        {$code = $selectionStatement.code;}
    | iterationStatement
        {$code = $iterationStatement.code;}
    | jumpStatement
        {$code = $jumpStatement.code;}
    ;

labeledStatement
    returns [String code]
    : 'case' constantExpression ':' statement
        {$code = "case " + $constantExpression.code + ": " + $statement.code;}
    | 'default' ':' statement
        {$code = "default: " + $statement.code;}
    | Identifier ':' statement
        {$code = $Identifier.text + ": " + $statement.code;}
    ;

expressionStatement // OK
    returns [String code]
    : expression? ';'
        {$code = nullable($expression.code, "") + ";";}
    ;

selectionStatement // OK
    returns [String code]
    : 'if' '(' expression ')' iftrue=statement ('else' iffalse=statement)?
        {$code = "if (" + $expression.code + ") " + $iftrue.code + nullable(" else ", $iffalse.code, "", "");}
    | 'switch' '(' expression ')' statement
        {$code = "switch (" + $expression.code + ") " + $statement.code;}
    ;

iterationStatement // OK
    returns [String code]
    : While '(' expression ')' statement
        {$code = "while (" + $expression.code + ") " + $statement.code;}
    | Do statement While '(' expression ')' ';'
        {$code = "do " + $statement.code + " while (" + $expression.code + ");";}
    | For '(' forCondition ')' statement
        {$code = "for (" + $forCondition.code + ") " + $statement.code;}
    ;

forCondition // OK
    returns [String code]
	: for1d=declaration ';' for2=expression? ';' for3=expression?
	    {
	        $code = joiner("; ")
                    .add($for1d.code)
                    .add(nullable($for2.code, ""))
                    .add(nullable($for3.code, ""))
                    .toString();
	    }
	| for1e=expression? ';' for2=expression? ';' for3=expression?
	    {
	        $code = joiner("; ")
                    .add(nullable($for1e.code, ""))
                    .add(nullable($for2.code, ""))
                    .add(nullable($for3.code, ""))
                    .toString();
	    }
	;

jumpStatement // OK
    returns [String code]
    : Goto Identifier Semicolon
        {
            $code = joiner("")
                    .add($Goto.text)
                    .add(" ")
                    .add($Identifier.text)
                    .add($Semicolon.text)
                    .toString();
        }
    | Continue Semicolon
        {
            $code = joiner("")
                    .add($Continue.text)
                    .add($Semicolon.text)
                    .toString();
        }
    | Break Semicolon
        {
            $code = joiner("")
                    .add($Break.text)
                    .add($Semicolon.text)
                    .toString();
        }
    | Return expression? Semicolon
        {
            $code = joiner("")
                    .add($Return.text)
                    .add(nullable(" ", $expression.code, $Semicolon.text, $Semicolon.text))
                    .toString();
        }
    | Goto unaryExpression Semicolon // GCC extension
        {
            $code = joiner("")
                    .add($Goto.text)
                    .add(" ")
                    .add($unaryExpression.code)
                    .add($Semicolon.text)
                    .toString();
        }
    ;








StructOrUnion
    : Struct
    | Union
    ;

DirectiveToken
    : '#' Whitespace* RegExp("[a-z]+") Whitespace RegExp(".*?") RegExp("~[ \\]") Whitespace* Newline
    ;

StorageClassSpecifier
    : Typedef
    | Extern
    | Static
    | Auto
    | Register
    ;

BasicTypeSpecifier
    : Void
    | Char
    | Short
    | Int
    | Long
    | Float
    | Double
    | Signed
    | Unsigned
    ;

TypeQualifier
    : Const
    | Volatile
    ;

Auto: 'auto';
Break: 'break';
Case: 'case';
Char: 'char';
Const: 'const';
Continue: 'continue';
Default: 'default';
Do: 'do';
Double: 'double';
Else: 'else';
Enum: 'enum';
Extern: 'extern';
Float: 'float';
For: 'for';
Goto: 'goto';
If: 'if';
Inline: 'inline';
Int: 'int';
Long: 'long';
Register: 'register';
Restrict: 'restrict';
Return: 'return';
Short: 'short';
Signed: 'signed';
Sizeof: 'sizeof';
Static: 'static';
Struct: 'struct';
Switch: 'switch';
Typedef: 'typedef';
Union: 'union';
Unsigned: 'unsigned';
Void: 'void';
Volatile: 'volatile';
While: 'while';

LeftParen: '(';
RightParen: ')';
LeftBracket: '[';
RightBracket: ']';
LeftBrace: '{';
RightBrace: '}';

Less: '<';
LessEqual: '<=';
Greater: '>';
GreaterEqual: '>=';
LeftShift: '<<';
RightShift: '>>';

Plus: '+';
PlusPlus: '++';
Minus: '-';
MinusMinus: '--';
Star: '*';
Div: '/';
Mod: '%';

And: '&';
Or: '|';
AndAnd: '&&';
OrOr: '||';
Caret: '^';
Not: '!';
Tilde: '~';

Question: '?';
Colon: ':';
Semicolon: ';';
Comma: ',';
SingleQuote: '\'';
DoubleQuote: '"';

Assign: '=';
// '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
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

Equal: '==';
NotEqual: '!=';

Arrow: '->';
Dot: '.';
Ellipsis: '...';

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
    : [a-zA-Z_]
    ;

fragment
Digit
    : [0-9]
    ;

fragment
UniversalCharacterName
    : '\\u' HexQuad
    | '\\U' HexQuad HexQuad
    ;

fragment
HexQuad
    : HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit
    ;

Constant
    : IntegerConstant
    | FloatingConstant
    //| EnumerationConstant
    | CharacterConstant
    ;

fragment
IntegerConstant
    : DecimalConstant IntegerSuffix?
    | OctalConstant IntegerSuffix?
    | HexadecimalConstant IntegerSuffix?
    | BinaryConstant
    ;

fragment
BinaryConstant
	: '0' [bB] [0-1]+
	;

fragment
DecimalConstant
    : NonzeroDigit Digit*
    ;

fragment
OctalConstant
    : '0' OctalDigit*
    ;

fragment
HexadecimalConstant
    : HexadecimalPrefix HexadecimalDigit+
    ;

fragment
HexadecimalPrefix
    : '0' [xX]
    ;

fragment
NonzeroDigit
    : [1-9]
    ;

fragment
OctalDigit
    : [0-7]
    ;

fragment
HexadecimalDigit
    : [0-9a-fA-F]
    ;

fragment
IntegerSuffix
    : UnsignedSuffix LongSuffix?
    | UnsignedSuffix LongLongSuffix
    | LongSuffix UnsignedSuffix?
    | LongLongSuffix UnsignedSuffix?
    ;

fragment
UnsignedSuffix
    : [uU]
    ;

fragment
LongSuffix
    : [lL]
    ;

fragment
LongLongSuffix
    : 'll' | 'LL'
    ;

fragment
FloatingConstant
    : DecimalFloatingConstant
    | HexadecimalFloatingConstant
    ;

fragment
DecimalFloatingConstant
    : FractionalConstant ExponentPart? FloatingSuffix?
    | DigitSequence ExponentPart FloatingSuffix?
    ;

fragment
HexadecimalFloatingConstant
    : HexadecimalPrefix HexadecimalFractionalConstant BinaryExponentPart FloatingSuffix?
    | HexadecimalPrefix HexadecimalDigit+ BinaryExponentPart FloatingSuffix?
    ;

fragment
FractionalConstant
    : DigitSequence? '.' DigitSequence
    | DigitSequence '.'
    ;

fragment
ExponentPart
    : Sign? DigitSequence
    ;

fragment
Sign
    : [-+]
    ;

DigitSequence
    : Digit+
    ;

fragment
HexadecimalFractionalConstant
    : HexadecimalDigit* '.' HexadecimalDigit+
    | HexadecimalDigit+ '.'
    ;

fragment
BinaryExponentPart
    : [pP] Sign? DigitSequence
    ;

fragment
FloatingSuffix
    : [fFlL]
    ;

fragment
CharacterConstant
    : [LuU] '\'' CChar+ '\''
    ;

fragment
CChar
    : ~['\\\r\n]
    | EscapeSequence
    ;

fragment
EscapeSequence
    : SimpleEscapeSequence
    | OctalEscapeSequence
    | HexadecimalEscapeSequence
    | UniversalCharacterName
    ;

fragment
SimpleEscapeSequence
    : '\\' ['"?abfnrtv\\]
    ;

fragment
OctalEscapeSequence
    : '\\' OctalDigit
    | '\\' OctalDigit OctalDigit
    | '\\' OctalDigit OctalDigit OctalDigit
    ;

fragment
HexadecimalEscapeSequence
    : '\\x' HexadecimalDigit+
    ;

StringLiteral
    : EncodingPrefix? '"' SChar* '"'
    ;

fragment
EncodingPrefix
    : 'u8'
    | 'u'
    | 'U'
    | 'L'
    ;

fragment
SChar
    : ~["\\\r\n]
    | EscapeSequence
    | '\\' Newline // Added line
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
