package ast;

import ast.types.*;

import java.util.*;

public class FunctionSignature {
    public enum ParameterQualifier {
        IN("in"), CONST_IN("const in"), OUT("out"), INOUT("inout");

        private final String name;

        ParameterQualifier(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static public class ParameterInfo {
        Type type;
        String id;
        ParameterQualifier qualifier;

        ParameterInfo(ParameterQualifier qualifier, Type type, String id) {
            this.qualifier = qualifier;
            this.type = type;
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ParameterInfo)) return false;
            var info = (ParameterInfo) obj;
            return type.equals(info.type) && id.equals(info.id);
        }

        public boolean equalWithQualifier(ParameterInfo info) {
            return info.qualifier == qualifier && equals(info);
        }

        @Override
        public String toString() {
            return qualifier.toString() + " " + type.toString() + " " + id;
        }
    }

    public Type returnType;
    private List<ParameterInfo> parameters = new ArrayList<>();
    public String id;

    public void addParameter(ParameterQualifier qualifier, Type type, String id) {
        parameters.add(new ParameterInfo(qualifier, type, id));
    }

    public FunctionSignature(Type returnType, String id) {
        this.returnType = returnType;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FunctionSignature)) return false;
        var sig = (FunctionSignature) obj;
        if (!id.equals(sig.id)) return false;
        if (returnType == null) {
            if (sig.returnType != null) return false;
        } else {
            if (!returnType.equals(sig.returnType)) return false;
        }
        if (!(parameters.size() == sig.parameters.size())) return false;
        for (int i = 0; i < parameters.size(); i++)
            if (!parameters.get(i).equals(sig.parameters.get(i)))
                return false;
        return true;
    }

    public boolean equalWithQualifiers(FunctionSignature sig) {
        if (!equals(sig)) return false;
        for (int i = 0; i < parameters.size(); i++)
            if (!parameters.get(i).equalWithQualifier(sig.parameters.get(i)))
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType == null ? "void" : returnType.toString()).append(" ").append(id).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i).toString());
            if (i <= parameters.size() - 2) sb.append(", ");
        }
        sb.append(")");
        return new String(sb);
    }
}
