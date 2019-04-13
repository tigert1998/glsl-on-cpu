package ast;

import ast.types.*;
import java.util.*;

public class FunctionSignature {
    public enum ParameterQualifier {
        IN, CONST_IN, OUT, INOUT
    }

    static public class ParameterInfo {
        Type type;
        String id;
        ParameterQualifier qualifiers;
    }

    private Type returnType = null;
    private List<ParameterInfo> parameters = null;
    private String functionName = null;
}
