package ast.stmt;

import java.util.*;

public class CompoundStmt extends Stmt {
    public List<Stmt> stmts = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\n");
        stmts.forEach(stmt -> sb.append(stmt).append("\n"));
        return new String(sb.append("}\n"));
    }
}
