package ast;

import ast.stmt.*;
import java.util.*;

public class FunctionAST extends AST {
    private FunctionSignature functionSignature;
    private List<AST> components = new ArrayList<>();

    public void putDeclarationStmt(DeclarationStmt stmt) {
        components.add(stmt);
    }

    public FunctionAST(FunctionSignature functionSignature) {
        this.functionSignature = functionSignature;
    }
}
