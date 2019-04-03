grammar Lang;

program: (declarationStmt | functionDefinition | functionForwardDeclarationStmt)+;

functionDefinition: functionSignature compoundStmt;

functionSignature: (type | VOID) functionName=IDENTIFIER '(' functionParameterList ')';

functionParameterList: (functionParameter (',' functionParameter)*)?;

functionParameter: (CONST? IN? | OUT | INOUT) type variableMaybeArray;

// statements

functionForwardDeclarationStmt: functionSignature ';'; // forward function declaration don't belong to normal stmt

stmt:
    declarationStmt
    | selectionStmt
    | compoundStmt
    | loopStmt
    | exprStmt
    | RETURN expr ';'
    | BREAK ';'
    | CONTINUE ';'
    | ';' // permit empty statement
    ;

compoundStmt: '{' stmt* '}';

loopStmt:
    WHILE '(' expr ')' (compoundStmt | stmt)
    | DO compoundStmt WHILE '(' expr ')' ';'
    | FOR '(' (exprStmt | declarationStmt) exprStmt expr ')' compoundStmt;

selectionStmt: IF '(' expr ')' (
    (stmt | compoundStmt) (
        ELSE (stmt | compoundStmt)
    )?
);

declarationStmt:
    constDeclarationStmt
    | normalDeclarationStmt;

constDeclarationStmt: CONST type constDeclarationList ';';

normalDeclarationStmt: type declarationList ';';

constDeclarationList: variableMaybeArray '=' expr (',' variableMaybeArray '=' expr)*;
declarationList: variableMaybeArray ('=' expr)? (',' variableMaybeArray ('=' expr)?)*;

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
    | IDENTIFIER                                       # referenceExpr
    | basicTypeConstructorInvocation                   # basicTypeConstructorInvocationExpr
    | functionOrStructConstructorInvocation            # functionOrStructConstructorInvocationExpr
    | expr ('[' expr ']')+                             # arraySubscriptingExpr
    | expr '.' functionOrStructConstructorInvocation   # memberFunctionInvocationExpr
    | expr '.' IDENTIFIER                              # elementSelectionExpr
    | expr (INCREMENT | DECREMENT)                     # postfixUnaryExpr
    | (
        INCREMENT
        | DECREMENT
        | PLUS
        | MINUS
        | LOGICAL_NOT
        | BITWISE_NOT
    ) expr                                             # prefixUnaryExpr
    | expr (MULT | DIV | MOD) expr                     # multDivModBinaryExpr
    | expr (PLUS | MINUS) expr                         # plusMinusBinaryExpr
    | expr (SHL | SHR) expr                            # shlShrBinaryExpr
    | expr (
        LESS
        | LESS_EQUAL
        | GREATER
        | GREATER_EQUAL
    ) expr                                             # lessGreaterBinaryExpr
    | expr (EQUAL | NOT_EQUAL) expr                    # eqNeqBinaryExpr
    | expr BITWISE_AND expr                            # bitwiseAndBinaryExpr
    | expr BITWISE_XOR expr                            # bitwiseXorBinaryExpr
    | expr BITWISE_OR expr                             # bitwiseOrBinaryExpr
    | expr LOGICAL_AND expr                            # logicalAndBinaryExpr
    | expr LOGICAL_OR expr                             # logicalOrBinaryExpr
    | expr '?' expr ':' expr                           # ternaryConditionalExpr
    | expr (
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
    // | expr (',' expr)+ // do not support comma expresion temporarily
    | '(' expr ')'                                     # parameteredExpr
    ;

basicTypeConstructorInvocation:
    basicType '(' ')'
    | basicType '(' expr (',' expr)* ')';

functionOrStructConstructorInvocation:
    structType '(' ')'
    | structType '(' expr (',' expr)* ')';

// keywords
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
REAL_LITERAL: [0-9]+'.'[0-9]* ([eE] [+-]? [0-9]+)?;

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
