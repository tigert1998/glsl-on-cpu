grammar Lang;

functionDefinition: functionSignature compoundStmt;

functionSignature: (type | VOID) IDENTIFIER '(' functionParameter (',' functionParameter)* ')';

functionParameter: (CONST? IN? | OUT | INOUT) type variableMaybeArray;

// statements

stmt:
    declarationStmt
    | selectionStmt
    | compoundStmt
    | RETURN expr ';'
    | BREAK ';'
    | CONTINUE ';'
    | expr ';'
    ;

compoundStmt: '{' stmt* '}';

selectionStmt: IF '(' expr ')' (
    (stmt | compoundStmt) (
        ELSE (stmt | compoundStmt)
    )?
);

declarationStmt:
    CONST type variableMaybeArray '=' expr (',' variableMaybeArray '=' expr)* ';'
    | type variableMaybeArray ('=' expr)? (',' variableMaybeArray ('=' expr)?)* ';';

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
    UNSIGNED_INT
    | UNSIGNED_REAL
    | IDENTIFIER
    | functionOrStructConstructorInvocation
    | basicTypeConstructorInvocation
    | expr '[' expr ']' // array subscripting
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
UNSIGNED_INT: [0-9]+;
UNSIGNED_REAL: [0-9]+'.'[0-9]*;

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
