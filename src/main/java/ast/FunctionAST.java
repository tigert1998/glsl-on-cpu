package ast;

import ast.stmt.*;

public class FunctionAST extends AST {
    private FunctionSignature functionSignature;
    private CompoundStmt stmt;

    public FunctionAST(FunctionSignature functionSignature, CompoundStmt stmt) {
        this.functionSignature = functionSignature;
        this.stmt = stmt;
    }

    @Override
    public String toString() {
        return functionSignature.toString() + " " + stmt.toString();
    }
}
