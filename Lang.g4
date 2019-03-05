grammar Lang;

addSubExpr: multDivExpr ((ADD|SUB) multDivExpr)*;
    
multDivExpr: atomExpr ((MULT|DIV) atomExpr)*;

atomExpr:
    '(' addSubExpr ')'
    | (ADD|SUB) atomExpr
    | UNSIGNED_INT
    | UNSIGNED_REAL
    | STRUCT_MEMBER
    | IDENTIFIER
    | functionInvoke
    ;

functionInvoke: 
    IDENTIFIER '()'
    | IDENTIFIER '(' addSubExpr (',' addSubExpr)* ')'
    ;

WHITESPACE: [ \t\r\n] -> skip;

IDENTIFIER : [_a-zA-Z][_a-zA-Z0-9]*;
STRUCT_MEMBER : IDENTIFIER '.' IDENTIFIER;
UNSIGNED_INT : [0-9]+;
UNSIGNED_REAL : [0-9]+'.'[0-9]*;

ADD: '+';
SUB: '-';
MULT: '*';
DIV: '/';