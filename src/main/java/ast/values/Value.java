package ast.values;

import ast.types.*;

public abstract class Value {
    protected Type type = null;
    protected String id = null;

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
