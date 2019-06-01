package ast.stmt;

import ast.AST;
import ast.Scope;
import org.bytedeco.llvm.LLVM.*;

import java.util.*;

public class StmtsWrapper extends AST {
    public List<Stmt> stmts = new ArrayList<>();

    public StmtsWrapper() {}

    public StmtsWrapper(List<StmtsWrapper> wrappers) {
        for (var wrapper : wrappers) stmts.addAll(wrapper.stmts);
    }

    public static StmtsWrapper singleton(Stmt stmt) {
        var result = new StmtsWrapper();
        result.stmts.add(stmt);
        return result;
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        return null;
    }
}
