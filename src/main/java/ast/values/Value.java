package ast.values;

import ast.types.*;

public class Value {
    protected Type type = null;
    private String id = null;

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
