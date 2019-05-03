package ast.values;

import ast.exceptions.*;
import ast.types.*;

public class Value {
    protected Type type = null;

    public Type getType() {
        return type;
    }

    static public int evalAsIntegral(Value value) throws UnlocatedSyntaxErrorException {
        if (!(value.getType() instanceof IntType || value.getType() instanceof UintType))
            throw UnlocatedSyntaxErrorException.notIntegerExpression();
        int res;
        if (value instanceof IntValue) res = ((IntValue) value).value;
        else res = (int) (long) ((UintValue) value).value;
        return res;
    }
}
