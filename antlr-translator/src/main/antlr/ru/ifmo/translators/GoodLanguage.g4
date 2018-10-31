grammar GoodLanguage;

@header {
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.stream.Collectors;
}

@members {
    static <T> String stringify(java.util.List<T> list, java.util.function.Function<? super T, String> fn, String delim) {
        return list.stream().map(fn).collect(java.util.stream.Collectors.joining(delim));
    }

    public static String initTuple(String tuple, String type, String assignment) {
        StringBuilder code = new StringBuilder();
        List<String> vars = Arrays.asList(tuple.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
        vars.stream().map(v -> type + " " + v + ";\n").forEach(code::append);
        if (assignment.indexOf("%SCANF%") > -1) {
            code.append(assignment.replace("%SCANF%", vars.stream().map(v -> "&" + v).collect(Collectors.joining(", "))));
        } else if (assignment.indexOf("%PRINTF%") > -1) {
            code.append(assignment.replace("%PRINTF%", vars.stream().collect(Collectors.joining(", "))));
        } else {
            String updateCode = updateTuple(tuple, type, assignment);
            code.append(updateCode.substring(2, updateCode.length() - 3));
        }
        return code.toString();
    }

    public static String updateTuple(String tuple, String type, String assignment) {
        StringBuilder code = new StringBuilder();
        List<String> vars = Arrays.asList(tuple.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
        if (assignment.indexOf("%SCANF%") > -1) {
            code.append(assignment.replace("%SCANF%", vars.stream().map(v -> "&" + v).collect(Collectors.joining(", "))));
        } else if (assignment.indexOf("%PRINTF%") > -1) {
            code.append(assignment.replace("%PRINTF%", vars.stream().collect(Collectors.joining(", "))));
        } else {
            List<String> assignments = Arrays.asList(assignment.substring(1, tuple.length() - 1).split("\\s*,\\s*"));
            if (vars.size() != assignments.size()) {
                throw new AssertionError("Wrong tuple syntax: " + tuple + " " + assignment);
            }
            for (int i = 0; i < vars.size(); i++) {
                code.append(type + " __TEMP" + vars.get(i) + " = " + assignments.get(i) + ";\n");
            }
            for (int i = 0; i < vars.size(); i++) {
                code.append(vars.get(i) + " = __TEMP" + vars.get(i) + ";\n");
            }
        }
        return "{\n" + code.toString() + "\n}\n";
    }
}

program // OK
    returns [String code]
    @after {
        $ctx.code = stringify($ctx.unit(), u -> u.code, "\n\n");
    }
    : Newline* unit*
    ;

unit // OK
    returns [String code]
    @after {
        if ($ctx.declaration() != null) {
            $ctx.code = $ctx.declaration().code;
        } else if ($ctx.function() != null) {
            $ctx.code = $ctx.function().code;
        } else if ($ctx.Directive() != null) {
            $ctx.code = $ctx.Directive().getText();
        }
    }
    : declaration Newline+
    | function Newline*
    | Directive Newline*
    ;

declaration // OK
    returns [String code]
    @after {
        $ctx.code = "";
        if ($ctx.lvalue().tuple() != null && $ctx.initializer().tupleAssignment() != null) {
            $ctx.code += initTuple($ctx.lvalue().tuple().code, $ctx.type().code, $ctx.initializer().tupleAssignment().code);
        } else if ($ctx.lvalue().tuple() != null || $ctx.initializer().tupleAssignment() != null) {
            throw new AssertionError("Wrong tuple usage");
        } else if ($ctx.lvalue().Identifier() != null) {
            if ($ctx.Const() != null) {
                $ctx.code += "const ";
            }
            $ctx.code += $ctx.type().code + " " + $ctx.lvalue().Identifier().getText() + " = " + $ctx.initializer().code + ";\n";
        }
    }
    : 'var' Const? lvalue ':' type '=' initializer
    ;

function // OK
    returns [String code]
    @after {
        $ctx.code = $ctx.type().code + " " + $ctx.Identifier().getText() + "(";
        if ($ctx.arguments() != null) {
            $ctx.code += $ctx.arguments().code;
        }
        $ctx.code += ") " + $ctx.compoundStatement().code;
    }
    : 'function' Identifier '(' arguments? ')' ':' type compoundStatement
    ;

type // OK
    returns [String code]
    @after {
        if ($ctx.Void() != null) {
            $ctx.code = $ctx.Void().getText();
        }
        if ($ctx.Char() != null) {
            $ctx.code = $ctx.Char().getText();
        }
        if ($ctx.Short() != null) {
            $ctx.code = $ctx.Short().getText();
        }
        if ($ctx.Int() != null) {
            $ctx.code = $ctx.Int().getText();
        }
        if ($ctx.Long() != null) {
            $ctx.code = $ctx.Long().getText();
        }
        if ($ctx.Float() != null) {
            $ctx.code = $ctx.Float().getText();
        }
        if ($ctx.Double() != null) {
            $ctx.code = $ctx.Double().getText();
        }
        if ($ctx.Signed() != null) {
            $ctx.code = $ctx.Signed().getText();
        }
        if ($ctx.Unsigned() != null) {
            $ctx.code = $ctx.Unsigned().getText();
        }
        if ($ctx.String() != null) {
            $ctx.code = "char*";
        }
        $ctx.code += stringify($ctx.Star(), s -> s.getText(), "");
    }
    : Void Star*
    | Char Star*
    | Short Star*
    | Int Star*
    | Long Star*
    | Float Star*
    | Double Star*
    | Signed Star*
    | Unsigned Star*
    | String Star*
    ;

initializer // OK
    returns [String code]
    @after {
        if ($ctx.assignmentExpression() != null) {
            $ctx.code = $ctx.assignmentExpression().code;
        } else if ($ctx.initializer() != null) {
            $ctx.code = "{" + $ctx.initializer().code + stringify($ctx.initializerCont(), i -> i.code, "") + "}";
        }
    }
    : tupleAssignment
    | '{' initializer initializerCont* '}'
    | assignmentExpression
    ;

initializerCont // OK
    returns [String code]
    @after {
        $ctx.code = ", " + $ctx.initializer().code;
    }
    : ',' initializer
    ;

arguments // OK
    returns [String code]
    @after {
        $ctx.code = $ctx.argument().code + stringify($ctx.argumentCont(), a -> a.code, "");
    }
    : argument argumentCont*
    ;

argumentCont // OK
    returns [String code]
    @after {
        $ctx.code = ", " + $ctx.argument().code;
    }
    : ',' argument
    ;

argument // OK
    returns [String code]
    @after {
        $ctx.code = $ctx.type().code + " " + $ctx.Identifier().getText();
    }
    : type Identifier
    ;

lvalue // OK
    returns [String code]
    @after {
        if ($ctx.tuple() != null) {
            $ctx.code = $ctx.tuple().code;
        } else {
            $ctx.code = $ctx.Identifier().getText();
        }
    }
    : tuple
    | Identifier
    ;

tuple // OK
    returns [String code]
    @after {
        $ctx.code = "[" + $ctx.assignmentExpression().code + stringify($ctx.tupleCont(), i -> i.code, "") + "]";
    }
    : '[' assignmentExpression tupleCont* ']'
    ;

tupleCont // OK
    returns [String code]
    @after {
        $ctx.code = ", " + $ctx.assignmentExpression().code;
    }
    : ',' assignmentExpression
    ;

compoundStatement // OK
    returns [String code]
    @after {
        $ctx.code = "{\n" + stringify($ctx.item(), i -> i.code, "\n") + "\n}\n";
    }
    : '{' item* '}'
    ;

item // OK
    returns [String code]
    @after {
        if ($ctx.statement() != null) {
            $ctx.code = $ctx.statement().code;
        } else if ($ctx.declaration() != null) {
            $ctx.code = $ctx.declaration().code;
        }
    }
    : declaration Newline+
    | statement Newline*
    ;

statement // OK
    returns [String code]
    @after {
        if ($ctx.labeledStatement() != null) {
            $ctx.code = $ctx.labeledStatement().code;
        } else if ($ctx.compoundStatement() != null) {
            $ctx.code = $ctx.compoundStatement().code;
        } else if ($ctx.expression() != null) {
            $ctx.code = $ctx.expression().code + ";";
        } else if ($ctx.selectionStatement() != null) {
            $ctx.code = $ctx.selectionStatement().code;
        } else if ($ctx.iterationStatement() != null) {
            $ctx.code = $ctx.iterationStatement().code;
        } else if ($ctx.jumpStatement() != null) {
            $ctx.code = $ctx.jumpStatement().code;
        } else if ($ctx.tupleExpression() != null) {
            $ctx.code = $ctx.tupleExpression().code;
        } else {
            $ctx.code = "\n";
        }
    }
    : labeledStatement
    | compoundStatement
    | selectionStatement
    | iterationStatement
    | jumpStatement
    | tupleExpression
    | expression? Newline
    ;

labeledStatement // OK
    returns [String code]
    @after {
        if ($ctx.Default() != null) {
            $ctx.code = "default: " + $ctx.statement().code;
        } else if ($ctx.Case() != null) {
            $ctx.code = "case " + $ctx.conditionalExpression().code + ": " + $ctx.statement().code;
        } else if ($ctx.Identifier() != null) {
            $ctx.code = $ctx.Identifier().getText() + ": " + $ctx.statement().code;
        }
    }
    : Default ':' statement
    | Case conditionalExpression ':' statement
    | Identifier ':' statement
    ;

selectionStatement // OK
    returns [String code]
    @after {
        if ($ctx.If() != null) {
            $ctx.code = "if (" + $ctx.expression().code + ") " + $ctx.statement().code;
            if ($ctx.elseStatement() != null) {
                $ctx.code += " " + $ctx.elseStatement().code;
            }
        } else if ($ctx.Switch() != null) {
            $ctx.code = "switch(" + $ctx.expression().code + ") " + $ctx.statement().code;
        }
    }
    : If '(' expression ')' statement elseStatement?
    | Switch '(' expression ')' statement
    ;

elseStatement
    returns [String code]
    @after {
        $ctx.code = "else " + $ctx.statement().code;
    }
    : Else statement
    ;

iterationStatement // OK
    returns [String code]
    @after {
        if ($ctx.Do() != null) {
            $ctx.code = "do " + $ctx.statement().code + " while (" + $ctx.expression().code + ");\n";
        } else if ($ctx.For() != null) {
            $ctx.code = "for (" + $ctx.forCondition().code + ") " + $ctx.statement().code;
        } else if ($ctx.While() != null) {
            $ctx.code = "while (" + $ctx.expression().code + ") " + $ctx.statement().code;
        }
    }
    : Do statement While '(' expression ')' Newline
    | For '(' forCondition ')' statement
    | While '(' expression ')' statement
    ;

forCondition // OK
    returns [String code]
    @after {
        $ctx.code = "";
        if ($ctx.declaration() != null) {
            $ctx.code += $ctx.declaration().code;
        } else if ($ctx.expression() != null) {
            $ctx.code += $ctx.expression().code;
        }
        $ctx.code += "; ";
        if ($ctx.assignmentExpression() != null) {
            $ctx.code += $ctx.assignmentExpression().code;
        }
        $ctx.code += "; ";
        if ($ctx.assignmentExpression2() != null) {
            $ctx.code += $ctx.assignmentExpression2().code;
        }
    }
	: declaration ';' assignmentExpression? ';' assignmentExpression2?
	| expression? ';' assignmentExpression? ';' assignmentExpression2?
	;

jumpStatement // OK
    returns [String code]
    @after {
        if ($ctx.Goto() != null) {
            $ctx.code = "goto " + $ctx.Identifier().getText() + ";\n";
        } else if ($ctx.Continue() != null) {
            $ctx.code = "continue;\n";
        } else if ($ctx.Break() != null) {
            $ctx.code = "break;\n";
        } else if ($ctx.Return() != null) {
            if ($ctx.expression() != null) {
                $ctx.code = "return " + $ctx.expression().code + ";\n";
            } else {
                $ctx.code = "return;\n";
            }
        }
    }
    : Goto Identifier Newline
    | Continue Newline
    | Break Newline
    | Return expression? Newline
    ;









primaryExpression
    : Identifier
    | Constant
    | StringLiteral
    | '(' expression ')'
    ;

postfixExpression
    :   primaryExpression postfix*
    ;

postfix
    : '[' expression ']'
    | '(' argumentExpressionList? ')'
    | '.' Identifier
    | '->' Identifier
    | '++'
    | '--'
    ;

argumentExpressionList
    : assignmentExpression argumentExpressionListCont*
    ;

argumentExpressionListCont
    : ',' assignmentExpression
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

logicalOrExpression // FIXME: getText()
    returns [String code]
    @after {
        $ctx.code = $ctx.getText();
    }
    : logicalAndExpression
    | logicalOrExpression '||' logicalAndExpression
    ;

conditionalExpression // OK
    returns [String code]
    @after {
        $ctx.code = $ctx.logicalOrExpression().code;
        if ($ctx.conditionalExpressionCont() != null) {
            $ctx.code += conditionalExpressionCont().code;
        }
    }
    : logicalOrExpression conditionalExpressionCont?
    ;

conditionalExpressionCont // OK
    returns [String code]
    @after {
        $ctx.code = " ? " + $ctx.expression().code + " : " + $ctx.conditionalExpression().code;
    }
    : '?' expression ':' conditionalExpression
    ;

assignmentExpression // FIXME: getText()
    returns [String code]
    @after {
        if ($ctx.conditionalExpression() != null) {
            $ctx.code = $ctx.conditionalExpression().code;
        } else if ($ctx.unaryExpression() != null) {
            $ctx.code = $ctx.unaryExpression().getText() + " " + $ctx.assignmentOperator().code + " " + $ctx.assignmentExpression().getText();
        }
    }
    : conditionalExpression
    | unaryExpression assignmentOperator assignmentExpression
    ;

assignmentExpression2 // FIXME: getText()
    returns [String code]
    @after {
        if ($ctx.conditionalExpression() != null) {
            $ctx.code = $ctx.conditionalExpression().code;
        } else if ($ctx.unaryExpression() != null) {
            $ctx.code = $ctx.unaryExpression().getText() + " " + $ctx.assignmentOperator().code + " " + $ctx.assignmentExpression().getText();
        }
    }
    : conditionalExpression
    | unaryExpression assignmentOperator assignmentExpression
    ;

assignmentOperator // OK
    returns [String code]
    @after {
        if ($ctx.Assign() != null) $ctx.code = $ctx.Assign().getText();
        if ($ctx.StarAssign() != null) $ctx.code = $ctx.StarAssign().getText();
        if ($ctx.DivAssign() != null) $ctx.code = $ctx.DivAssign().getText();
        if ($ctx.ModAssign() != null) $ctx.code = $ctx.ModAssign().getText();
        if ($ctx.PlusAssign() != null) $ctx.code = $ctx.PlusAssign().getText();
        if ($ctx.MinusAssign() != null) $ctx.code = $ctx.MinusAssign().getText();
        if ($ctx.LeftShiftAssign() != null) $ctx.code = $ctx.LeftShiftAssign().getText();
        if ($ctx.RightShiftAssign() != null) $ctx.code = $ctx.RightShiftAssign().getText();
        if ($ctx.AndAssign() != null) $ctx.code = $ctx.AndAssign().getText();
        if ($ctx.XorAssign() != null) $ctx.code = $ctx.XorAssign().getText();
        if ($ctx.OrAssign() != null) $ctx.code = $ctx.OrAssign().getText();
    }
    : Assign
    | StarAssign
    | DivAssign
    | ModAssign
    | PlusAssign
    | MinusAssign
    | LeftShiftAssign
    | RightShiftAssign
    | AndAssign
    | XorAssign
    | OrAssign
    ;

expression // OK
    returns [String code]
    @after {
        $ctx.code = $ctx.assignmentExpression().code + stringify($ctx.expressionCont(), c -> c.code, "");
    }
    : assignmentExpression expressionCont*
    ;

expressionCont // OK
    returns [String code]
    @after {
        $ctx.code = ", " + $ctx.assignmentExpression().code;
    }
    : ',' assignmentExpression
    ;

tupleExpression // OK
    returns [String code]
    @after {
        $ctx.code = updateTuple($ctx.tuple().code, $ctx.type().code, $ctx.tupleAssignment().code);
    }
    : tuple ':' type '=' tupleAssignment
    ;

tupleAssignment // OK
    returns [String code]
    @after {
        if ($ctx.Read() != null) {
            $ctx.code = "scanf(" + $ctx.StringLiteral().getText() + ", %SCANF%);";
        } else if ($ctx.Print() != null) {
            $ctx.code = "printf(" + $ctx.StringLiteral().getText() + ", %PRINTF%);";
        } else if ($ctx.tuple() != null) {
            $ctx.code = $ctx.tuple().code;
        }
    }
    : Read '(' StringLiteral ')'
    | Print '(' StringLiteral ')'
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

