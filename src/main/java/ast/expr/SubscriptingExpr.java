package ast.expr;

import ast.Scope;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;
import org.json.JSONObject;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class SubscriptingExpr extends Expr {
    private Expr x;
    private Expr index;

    private SubscriptingExpr(Expr x, Expr index) {
        isLValue = x.isLValue;
        this.type = ((IndexedType) x.getType()).elementType();
        this.x = x;
        this.index = index;
    }

    static public Expr factory(Expr x, Expr index) throws UnlocatedSyntaxErrorException {
        if (!(x.getType() instanceof IndexedType))
            throw InvalidIndexException.invalidSubscriptingType(x.getType());

        var type = (IndexedType) x.getType();

        if (index instanceof ConstExpr) {
            int i = Value.evalAsIntegral(((ConstExpr) index).getValue());
            if (i >= type.getN()) throw InvalidIndexException.outOfRange();
            if (x instanceof ConstExpr) {
                return new ConstExpr(((Indexed) ((ConstExpr) x).getValue()).valueAt(i));
            }
        }
        return new SubscriptingExpr(x, index);
    }

    @Override
    public LLVMValueRef evaluate(LLVMModuleRef module, LLVMValueRef function, Scope scope) {
        var xptr = x.evaluate(module, function, scope);

        var originalType = x.getType();
        var builder = LLVMCreateBuilder();
        LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
        var indexValue = LLVMBuildLoad(builder, index.evaluate(module, function, scope), ""); // i32

        if (originalType instanceof SwizzledType) {
            return LLVMBuildLoad(builder, buildGEP(builder, xptr, "", constant(0), indexValue), "");
        } else if (originalType instanceof MatnxmType) {
            int m = ((MatnxmType) originalType).getM();
            var l = LLVMBuildMul(builder, indexValue, constant(m), "");
            var result = buildAllocaInFirstBlock(function, this.type.withInnerPtrInLLVM(), "");
            appendForLoop(function, constant(0), constant(m), "", (bodyBuilder, i) -> {
                var from = LLVMBuildLoad(bodyBuilder,
                        buildGEP(bodyBuilder, xptr, "", constant(0), LLVMBuildAdd(bodyBuilder, l, i, "")),
                        "");
                var to = buildGEP(bodyBuilder, result, "", constant(0), i);
                LLVMBuildStore(bodyBuilder, from, to);
                return null;
            });
            return result;
        } else {
            var result = buildGEP(builder, xptr, "", constant(0), indexValue);
            return loadPtr(this.type, function, result);
        }
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("x", x.toJSON());
        json.put("index", index.toJSON());
        return json;
    }
}
