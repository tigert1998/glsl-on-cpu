package ast;

import ast.stmt.DeclarationStmt;

import java.util.*;

public class ProgramAST extends AST {
    private List<AST> components = new ArrayList<>();

    public void putDeclarationStmt(DeclarationStmt stmt) {
        components.add(stmt);
    }

    public void putFunctionAST(FunctionAST ast) { components.add(ast); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        components.forEach(ast -> {
            sb.append(ast).append('\n');
        });
        return new String(sb);
    }
}
