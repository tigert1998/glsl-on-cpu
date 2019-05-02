package ast.expr;

import ast.FunctionSignature;
import org.json.JSONObject;

public class ParameterReferenceExpr extends Expr {
    public FunctionSignature.ParameterInfo parameterInfo;

    public ParameterReferenceExpr(FunctionSignature.ParameterInfo parameterInfo) {
        this.parameterInfo = parameterInfo;
        this.type = parameterInfo.type;
        this.isLValue = (parameterInfo.qualifier != FunctionSignature.ParameterQualifier.CONST_IN);
    }

    @Override
    public JSONObject toJSON() {
        var json = super.toJSON();
        json.put("id", parameterInfo.id);
        return json;
    }
}
