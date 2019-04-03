package ast.types;

import ast.exceptions.*;
import org.antlr.v4.runtime.Token;

import java.util.*;

public class StructType extends Type {
    static public class FieldInfo {
        public String id = null;
        public Type type = null;
        public FieldInfo(String id, Type type) {
            this.id = id;
            this.type = type;
        }
    };

    public String id = null;
    private List<FieldInfo> fieldInfoList = new ArrayList<>();
    private Map<String, FieldInfo> fieldInfoMap = new TreeMap<>();

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
        fieldInfoMap.put(info.id, info);
    }

    public boolean fieldIDExists(String id) {
        return fieldInfoMap.containsKey(id);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("struct " + id + " {\n");
        fieldInfoList.forEach(info -> {
            String infoTypeStr;
            if (info.type instanceof StructType) {
                infoTypeStr = ((StructType) info.type).id;
            } else {
                infoTypeStr = info.type.toString();
            }
            sb.append('\t').append(infoTypeStr).append(' ').append(info.id).append(";\n");
        });
        sb.append("}");
        return new String(sb);
    }
}
