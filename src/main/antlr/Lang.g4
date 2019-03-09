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
    CONST type constDeclarationList ';'
    | type declarationList ';';

constDeclarationList: variableMaybeArray '=' expr (',' variableMaybeArray '=' expr)*;
declarationList: variableMaybeArray ('=' expr)? (',' variableMaybeArray ('=' expr)?)*;

exprStmt: expr ';';

variableMaybeArray: IDENTIFIER ('[' expr? ']')?;

// encapsulate types

structDefinition: STRUCT IDENTIFIER? '{' (type variableMaybeArray (',' variableMaybeArray)* ';')+ '}';

type: basicType | structType;

basicType:
    (BOOL | INT | UINT | FLOAT | BVECN | IVECN | UVECN | VECN | MATNXM | MATN) ('[' expr? ']')?;

structType:
    (IDENTIFIER | structDefinition) ('[' expr? ']')?;

// expression

expr:
    (TRUE | FALSE)
    | INT_LITERAL
    | UINT_LITERAL
    | REAL_LITERAL
    | IDENTIFIER
    | functionOrStructConstructorInvocation
    | basicTypeConstructorInvocation
    | expr '[' expr ']' // array subscripting
    | expr '.' functionOrStructConstructorInvocation // member function
    | expr '.' IDENTIFIER // struct member
    | expr (INCREMENT | DECREMENT)
    | (
        INCREMENT
        | DECREMENT
        | PLUS
        | MINUS
        | LOGICAL_NOT
        | BITWISE_NOT
    ) expr
    | expr (MULT | DIV | MOD) expr
    | expr (PLUS | MINUS) expr
    | expr (SHL | SHR) expr
    | expr (
        LESS
        | LESS_EQUAL
        | GREATER
        | GREATER_EQUAL
    ) expr
    | expr (EQUAL | NOT_EQUAL) expr
    | expr BITWISE_AND expr
    | expr BITWISE_XOR expr
    | expr BITWISE_OR expr
    | expr LOGICAL_AND expr
    | expr LOGICAL_OR expr
    | expr '?' expr ':' expr
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
    ) expr
    // | expr (',' expr)+ // do not support comma expresion temporarily
    | '(' expr ')'
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
INT_LITERAL: [0-9]+;
UINT_LITERAL: [0-9]+'u';
REAL_LITERAL: [0-9]+'.'[0-9]*;

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
