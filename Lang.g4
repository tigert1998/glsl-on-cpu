grammar Lang;

selectionStmt:
    IF '(' expr ')' stmt;

stmt:
    CONST declareIdentifier '=' expr ';'
    | RETURN expr ';'
    | declareIdentifier ('=' expr)? ';'
    | expr ';'
    ;

declareIdentifier: IDENTIFIER IDENTIFIER;

expr:
    | UNSIGNED_INT
    | UNSIGNED_REAL
    | IDENTIFIER
    | IDENTIFIER '()' // function invoke
    | IDENTIFIER '(' expr (',' expr)* ')'
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
    | IDENTIFIER (
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
    | expr (',' expr)+
    | '(' expr ')'
    ;

CONST: 'const';
RETURN: 'return';
IF: 'if';

IDENTIFIER: [_a-zA-Z][_a-zA-Z0-9]*;
UNSIGNED_INT: [0-9]+;
UNSIGNED_REAL: [0-9]+'.'[0-9]*;

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

WHITESPACE: [ \t\r\n] -> skip;

BLOCK_COMMENT:
    '/*' .*? '*/' -> skip;

LINE_COMMENT:
    '//' ~[\r\n]* -> skip;
