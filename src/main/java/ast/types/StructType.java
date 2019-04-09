package ast.types;

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
    public Type collapse() {
        return null;
    }
}
