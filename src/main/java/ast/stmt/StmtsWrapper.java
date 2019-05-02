package ast.stmt;

import ast.AST;

import java.util.*;

public class StmtsWrapper extends AST {
    public List<Stmt> stmts = new ArrayList<>();

    public static StmtsWrapper singleton(Stmt stmt) {
        var result = new StmtsWrapper();
        result.stmts.add(stmt);
        return result;
    }
}
