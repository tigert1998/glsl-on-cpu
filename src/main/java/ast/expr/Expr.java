package ast.expr;

import ast.AST;
import ast.types.Type;

public abstract class Expr extends AST {
    protected Type type = null;

    public Type getType() {
        return type;
    }
}
