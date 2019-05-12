package ast.stmt;

import ast.*;
import ast.expr.*;
import ast.types.*;
import codegen.LLVMUtility;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

public class DeclarationStmt extends Stmt {
    public Type type;
    public String id;
    public Expr expr;
    private LLVMValueRef llvmValue;
    // i32*, float*, i8*
    // [n x i32]*, [n x i8]*, [n x float]*

    public DeclarationStmt(Type type, String id, Expr expr) {
        this.type = type;
        this.id = id;
        this.expr = expr;
    }

    public void setLLVMValue(LLVMValueRef llvmValue) {
        this.llvmValue = llvmValue;
    }

    // store e* to e*
    // store [n x e*]* to [n x e]*
    public void storeLLVMValue(LLVMValueRef function, LLVMValueRef llvmValue) {
        LLVMUtility.storePtr(this.type, function, llvmValue, this.llvmValue);
    }

    // load e* from e*
    // load [n x e*]* from [n x e]*
    public LLVMValueRef loadLLVMValue(LLVMValueRef function) {
        return LLVMUtility.loadPtr(this.type, function, this.llvmValue);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("type", type.toString());
        json.put("id", id);
        json.put("expr", ((AST) expr).toJSON());
        return json;
    }
}
