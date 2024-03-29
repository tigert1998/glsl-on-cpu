grammar Lang;

program: (
    declarationStmt
    | functionDefinition
    | functionForwardDeclarationStmt
    | emptyStmt
    | externStmt )*;

functionDefinition: functionSignature compoundStmt;

functionSignature: (type | VOID) functionName=IDENTIFIER '(' functionParameterList ')';

functionParameterList: (functionParameter (',' functionParameter)*)?;

functionParameter: (CONST? IN? | OUT | INOUT) type variableMaybeArray;

// statements

functionForwardDeclarationStmt: functionSignature ';';

externStmt: EXTERN '"C"' '{' (functionForwardDeclarationStmt | emptyStmt)* '}';

// forward function declaration and extern stmt doesn't belong to normal stmt

stmt:
    declarationStmt
    | switchStmt
    | selectionStmt
    | compoundStmt
    | loopStmt
    | exprStmt
    | returnStmt
    | breakStmt
    | continueStmt
    | emptyStmt
    ;

returnStmt: RETURN expr? ';';

breakStmt: BREAK ';';

continueStmt: CONTINUE ';';

emptyStmt: ';';

switchStmt: SWITCH '(' expr ')' '{'
    caseItem*
'}';

caseItem: (CASE expr | DEFAULT) ':' stmt*;

compoundStmt: '{' stmt* '}';

loopStmt:
    WHILE '(' expr ')' stmt                                                        # whileLoopStmt
    | DO stmt WHILE '(' expr ')' ';'                                               # doWhileLoopStmt
    | FOR '(' forLoopInitialization forLoopCondition ';' forLoopStep ')' body=stmt # forLoopStmt
    ;

forLoopInitialization: exprStmt | declarationStmt | ';';

forLoopCondition: expr?;

forLoopStep: expr?;

selectionStmt: IF '(' expr ')' (
    stmt (ELSE stmt)?
);

declarationStmt:
    constDeclarationStmt
    | normalDeclarationStmt;

constDeclarationStmt: CONST type constDeclarationList ';';

normalDeclarationStmt: type declarationList ';';

declarationItem: variableMaybeArray ('=' expr)?;

constDeclarationList: (variableMaybeArray '=' expr (',' variableMaybeArray '=' expr)*)?;
declarationList: (declarationItem (',' declarationItem)*)?;

exprStmt: expr ';';

variableMaybeArray: IDENTIFIER specifiedArrayLength?;

// encapsulate types

structDefinition: STRUCT structName=IDENTIFIER? '{' structFieldDeclarationStmt+ '}';

structFieldDeclarationStmt: type variableMaybeArray (',' variableMaybeArray)* ';';

type: basicType | structType;

basicType:
    (BOOL | INT | UINT | FLOAT | BVECN | IVECN | UVECN | VECN | MATNXM | MATN) specifiedArrayLength?;

structType:
    (IDENTIFIER | structDefinition) specifiedArrayLength?;

specifiedArrayLength: '[' expr? ']';

// expression

expr:
    (boolLiteral | INT_LITERAL | UINT_LITERAL | REAL_LITERAL) # literalExpr
    | IDENTIFIER                                              # referenceExpr
    | basicTypeConstructorInvocation                          # basicTypeConstructorInvocationExpr
    | functionOrStructConstructorInvocation                   # functionOrStructConstructorInvocationExpr
    | expr ('[' idx=expr ']')                                 # arraySubscriptingExpr
    | expr '.' method=functionInvocation                      # memberFunctionInvocationExpr
    | expr '.' selection=IDENTIFIER                           # elementSelectionExpr
//    | expr op=(INCREMENT | DECREMENT)                         # postfixUnaryExpr
//    do not support postfix increment/decrement temporarily
    | op=(
        INCREMENT
        | DECREMENT
        | PLUS
        | MINUS
        | LOGICAL_NOT
        | BITWISE_NOT
    ) expr                                             # prefixUnaryExpr
    | expr op=(MULT | DIV | MOD) expr                  # multDivModBinaryExpr
    | expr op=(PLUS | MINUS) expr                      # plusMinusBinaryExpr
    | expr op=(SHL | SHR) expr                         # shlShrBinaryExpr
    | expr op=(
        LESS
        | LESS_EQUAL
        | GREATER
        | GREATER_EQUAL
    ) expr                                             # lessGreaterBinaryExpr
    | expr op=(EQUAL | NOT_EQUAL) expr                 # eqNeqBinaryExpr
    | expr op=BITWISE_AND expr                         # bitwiseAndBinaryExpr
    | expr op=BITWISE_XOR expr                         # bitwiseXorBinaryExpr
    | expr op=BITWISE_OR expr                          # bitwiseOrBinaryExpr
    | expr op=LOGICAL_AND expr                         # logicalAndBinaryExpr
    | expr op=LOGICAL_XOR expr                         # logicalXorBinaryExpr
    | expr op=LOGICAL_OR expr                          # logicalOrBinaryExpr
    | expr '?' expr ':' expr                           # ternaryConditionalExpr
    | expr op=(
        ASSIGN
        | MULT_ASSIGN
        | DIV_ASSIGN
        | MOD_ASSIGN
        | PLUS_ASSIGN
        | MINUS_ASSIGN
        | SHL_ASSIGN
        | SHR_ASSIGN
        | AND_ASSIGN
        | XOR_ASSIGN
        | OR_ASSIGN
    ) expr                                             # assignExpr
//    | expr (',' expr)+
//    do not support comma expresion temporarily
    | '(' expr ')'                                     # parameteredExpr
    ;

basicTypeConstructorInvocation:
    basicType '(' ')'
    | basicType '(' expr (',' expr)* ')';

functionOrStructConstructorInvocation:
    structType '(' ')'
    | structType '(' expr (',' expr)* ')';

functionInvocation:
    IDENTIFIER '(' ')'
    | IDENTIFIER '(' expr (',' expr)* ')';

// keywords
// compiler-support for extern "C" {}
EXTERN: 'extern';

IN: 'in';
OUT: 'out';
INOUT: 'inout';
CONST: 'const';
RETURN: 'return';
IF: 'if';
ELSE: 'else';
BREAK: 'break';
CONTINUE: 'continue';
STRUCT: 'struct';
WHILE: 'while';
DO: 'do';
FOR: 'for';
TRUE: 'true';
FALSE: 'false';
SWITCH: 'switch';
CASE: 'case';
DEFAULT: 'default';

// basic types, also keywords
VOID: 'void';
BOOL: 'bool';
INT: 'int';
UINT: 'uint';
FLOAT: 'float';
DOUBLE: 'double';

BVECN: 'bvec'[2-4];
IVECN: 'ivec'[2-4];
UVECN: 'uvec'[2-4];
VECN: 'vec'[2-4];
DVECN: 'dvec'[2-4];

MATNXM: 'mat'[2-4]'x'[2-4];
MATN: 'mat'[2-4];

// others

IDENTIFIER: [_a-zA-Z][_a-zA-Z0-9]*;
boolLiteral: TRUE | FALSE;
INT_LITERAL: [1-9][0-9]* | '0' | '0'+[1-7][0-7]* | '0'[xX][0-9a-fA-F]+;
UINT_LITERAL: INT_LITERAL 'u';
REAL_LITERAL: [0-9]*'.'[0-9]* ([eE] [+-]? [0-9]+)?;

// operators

INCREMENT: '++';
DECREMENT: '--';
LOGICAL_NOT: '!';
BITWISE_NOT: '~';

MULT: '*';
DIV: '/';
MOD: '%';

PLUS: '+';
MINUS: '-';

SHL: '<<';
SHR: '>>';

LESS: '<';
LESS_EQUAL: '<=';
GREATER: '>';
GREATER_EQUAL: '>=';

EQUAL: '==';
NOT_EQUAL: '!=';

BITWISE_AND: '&';

BITWISE_XOR: '^';

BITWISE_OR: '|';

LOGICAL_AND: '&&';

LOGICAL_XOR: '^^';

LOGICAL_OR: '||';

ASSIGN: '=';
MULT_ASSIGN: '*=';
DIV_ASSIGN: '/=';
MOD_ASSIGN: '%=';
PLUS_ASSIGN: '+=';
MINUS_ASSIGN: '-=';
SHL_ASSIGN: '<<=';
SHR_ASSIGN: '>>=';
AND_ASSIGN: '&=';
XOR_ASSIGN: '^=';
OR_ASSIGN: '|=';

// others to skip

WHITESPACE: [ \t\r\n] -> skip;

BLOCK_COMMENT:
    '/*' .*? '*/' -> skip;

LINE_COMMENT:
    '//' ~[\r\n]* -> skip;
