package ast.types;

import ast.*;
import ast.exceptions.*;
import ast.values.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.llvm.LLVM.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;
import static codegen.LLVMUtility.*;

public class StructType extends Type {
    static public class FieldInfo {
        public String id;
        public Type type;

        public FieldInfo(String id, Type type) {
            this.id = id;
            this.type = type;
        }
    }

    public String id;
    private List<FieldInfo> fieldInfoList = new ArrayList<>();
    private Map<String, Integer> fieldInfoMap = new TreeMap<>();

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StructType)) return false;
        return id.equals(((StructType) obj).id);
    }

    public StructType(String id) {
        this.id = id;
    }

    public void addFieldInfo(FieldInfo info) {
        fieldInfoList.add(info);
        fieldInfoMap.put(info.id, fieldInfoList.size() - 1);
    }

    public boolean fieldIDExists(String id) {
        return fieldInfoMap.containsKey(id);
    }

    public FieldInfo getFieldInfo(int i) {
        return fieldInfoList.get(i);
    }

    public FieldInfo getFieldInfo(String name) {
        var idx = fieldInfoMap.get(name);
        if (idx == null) return null;
        return getFieldInfo(idx);
    }

    public Integer getFieldInfoIndex(String name) {
        return fieldInfoMap.get(name);
    }

    public int totalFields() {
        return fieldInfoList.size();
    }

    @Override
    public String toString() {
        return id;
    }

    public String toDetailedString() {
        var sb = new StringBuilder("struct " + id + " {\n");
        fieldInfoList.forEach(info -> {
            sb.append('\t').append(info.type.toString()).append(' ').append(info.id).append(";\n");
        });
        sb.append("}");
        return new String(sb);
    }

    @Override
    public StructValue zero() {
        Value[] values = new Value[totalFields()];
        for (int i = 0; i < values.length; i++)
            values[i] = fieldInfoList.get(i).type.zero();
        return new StructValue(this, values);
    }

    @Override
    public Value construct(Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length != this.totalFields()) throw ConstructionFailedException.fieldNumberNotMatch();
        for (int i = 0; i < values.length; i++) {
            var value = values[i];
            var field = this.getFieldInfo(i);
            if (!value.getType().equals(field.type))
                throw ConstructionFailedException.fieldTypeNotMatch();
        }
        return new StructValue(this, values);
    }

    @Override
    public LLVMValueRef construct(Type[] types, LLVMValueRef[] values, LLVMValueRef function, Scope scope) {
        var builder = LLVMCreateBuilder();
        var result = buildAllocaInFirstBlock(function, this.inLLVM(), "");
        for (int i = 0; i < values.length; i++) {
            LLVMPositionBuilderAtEnd(builder, LLVMGetLastBasicBlock(function));
            storePtr(types[i], function, values[i], buildGEP(builder, result, "", 0, i));
        }
        return result;
    }

    @Override
    public LLVMTypeRef inLLVM() {
        var types = new LLVMTypeRef[totalFields()];
        for (int i = 0; i < types.length; i++)
            types[i] = fieldInfoList.get(i).type.inLLVM();
        return LLVMStructType(new PointerPointer<>(types), types.length, 0);
    }
}
