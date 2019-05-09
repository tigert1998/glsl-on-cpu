package ast.expr;

import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.json.JSONObject;
import org.bytedeco.llvm.LLVM.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;


public class SwizzleExpr extends Expr {
    private Expr expr;
    private int[] indices;

    static private Map<String, FunctionSignature> swizzleFuncSigs = new TreeMap<>();

    static {
        String[] typeNames = SwizzledType.typeNames();
        for (var name : typeNames) {
            String id = ".swizzle." + name.toLowerCase().substring(0, name.length() - 4);
            var sig = new FunctionSignature(VoidType.TYPE, id);
            swizzleFuncSigs.put(name, sig);
        }
    }

    private SwizzleExpr(Expr expr, int[] indices) {
        this.isLValue = expr.isLValue && !SwizzleUtility.isDuplicate(indices);
        this.expr = expr;
        this.indices = indices;
        var originType = (SwizzledType) expr.getType();
        if (indices.length == 1) this.type = originType.elementType();
        else this.type = (Type) originType.changeN(indices.length);
    }

    public static Expr factory(Expr expr, int[] indices)
            throws ConstructionFailedException, InvalidSelectionException {
        if (!(expr.getType() instanceof SwizzledType))
            throw InvalidSelectionException.invalidSwizzleType(expr.getType());
        var type = (SwizzledType) expr.getType();

        if (expr instanceof ConstExpr) {
            var values = ((Vectorized) ((ConstExpr) expr).getValue()).retrieve();
            if (indices.length == 1) {
                return new ConstExpr(type.elementType().construct(new Value[]{values[indices[0]]}));
            }
            var newValues = new Value[indices.length];
            for (int i = 0; i < indices.length; i++) newValues[i] = values[indices[i]];
            return new ConstExpr(
                    ((Type) type.changeN(indices.length)).construct(newValues));

        } else if (expr instanceof SwizzleExpr) {
            int[] preIndices = ((SwizzleExpr) expr).indices;
            int[] newIndices = new int[indices.length];
            for (int i = 0; i < indices.length; i++) {
                newIndices[i] = preIndices[indices[i]];
            }
            return new SwizzleExpr(((SwizzleExpr) expr).expr, newIndices);
        } else {
            return new SwizzleExpr(expr, indices);
        }
    }

    @Override
    public LLVMValueRef evaluate(LLVMValueRef function, Scope scope) {
        var value = expr.evaluate(function, scope);
        var builder = LLVMCreateBuilder();
        var block = LLVMGetLastBasicBlock(function);
        LLVMPositionBuilderAtEnd(builder, block);
        if (indices.length == 1) {
            return LLVMBuildLoad(builder, buildGEP(builder, value, new int[]{0, indices[0]}, ""), "");
        } else {
            var result = buildAllocaInFirstBlock(function, type.withInnerPtrInLLVM(), "");
            appendForLoop(function, indices, (bodyBuilder, i) -> {
                var from = LLVMBuildLoad(bodyBuilder,
                        buildGEP(bodyBuilder, value,"", constant(0), constant(indices[i])), "");
                var to = buildGEP(bodyBuilder, result, "", constant(0), constant(i));
                LLVMBuildStore(bodyBuilder, from, to);
                return null;
            });
            return result;
        }
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("expr", expr.toJSON());
        json.put("swizzle", SwizzleUtility.indicesToString(indices));
        return json;
    }
}
