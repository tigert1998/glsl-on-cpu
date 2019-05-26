package ast;

import ast.types.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import static org.bytedeco.llvm.global.LLVM.*;

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
        public Type type;
        public String id;
        public ParameterQualifier qualifier;
        public int index;

        private ParameterInfo(ParameterQualifier qualifier, Type type, String id, int index) {
            this.qualifier = qualifier;
            this.type = type;
            this.id = id;
            this.index = index;
        }

        public boolean match(ParameterInfo info) {
            return info.type == this.type;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ParameterInfo)) return false;
            var info = (ParameterInfo) obj;
            return info.qualifier == qualifier && match(info);
        }

        @Override
        public String toString() {
            return qualifier.toString() + " " + type.toString() + " " + id;
        }
    }

    public Type returnType;
    public List<ParameterInfo> parameters = new ArrayList<>();
    public Map<String, ParameterInfo> parametersMap = new TreeMap<>();
    public String id;
    public boolean cLinkage = false;

    public void addParameter(ParameterQualifier qualifier, Type type, String id) {
        var info = new ParameterInfo(qualifier, type, id, parameters.size());
        parameters.add(info);
        parametersMap.put(id, info);
    }

    public FunctionSignature(Type returnType, String id) {
        this.returnType = returnType;
        this.id = id;
    }

    public void setCLinkage() {
        cLinkage = true;
    }

    public boolean match(FunctionSignature sig) {
        if (!id.equals(sig.id)) return false;
        if (!(parameters.size() == sig.parameters.size())) return false;
        for (int i = 0; i < parameters.size(); i++)
            if (!parameters.get(i).match(sig.parameters.get(i)))
                return false;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FunctionSignature)) return false;
        var sig = (FunctionSignature) obj;
        if (!match(sig)) return false;
        if (!returnType.equals(sig.returnType)) return false;
        for (int i = 0; i < parameters.size(); i++)
            if (!parameters.get(i).equals(sig.parameters.get(i)))
                return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType.toString()).append(" ").append(id).append("(");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i).toString());
            if (i <= parameters.size() - 2) sb.append(", ");
        }
        sb.append(")");
        return new String(sb);
    }

    public LLVMTypeRef inLLVM() {
        var types = new LLVMTypeRef[parameters.size() + (returnType instanceof VoidType ? 0 : 1)];
        for (int i = 0; i < parameters.size(); i++) {
            types[i] = LLVMPointerType(parameters.get(i).type.withInnerPtrInLLVM(), 0);
        }
        if (!(returnType instanceof VoidType)) {
            types[types.length - 1] = LLVMPointerType(returnType.withInnerPtrInLLVM(), 0);
        }
        return LLVMFunctionType(LLVMVoidType(), new PointerPointer<>(types), types.length, 0);
    }

    public String getLLVMID() {
        if (cLinkage) {
            return id;
        } else {
            StringBuilder sb = new StringBuilder("$" + id);
            parameters.forEach(parameter ->
                    sb.append('.').append(parameter.type.toString())
            );
            return new String(sb);
        }
    }
}
