package ast.types;

import java.util.*;

public class StructType extends Type {
    static public class FieldInfo {
        private Type type;
        private String name;
    };

    List<FieldInfo> fields = null;
}
