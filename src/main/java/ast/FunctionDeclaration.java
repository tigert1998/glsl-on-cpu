package ast;

import ast.types.*;

import java.util.*;

public class FunctionDeclaration {
    public enum ParameterQualifier {
        IN, CONST_IN, OUT, INOUT
    }

    static public class ParameterInfo {
        Type type;
        String id;
        ParameterQualifier qualifiers;
    }

    private Type returnType;
    private List<ParameterInfo> parameters;
}
