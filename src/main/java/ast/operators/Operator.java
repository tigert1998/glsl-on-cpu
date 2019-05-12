package ast.operators;

import org.reflections.Reflections;

public abstract class Operator {
    public static Operator fromText(String text) {
        Reflections reflections = new Reflections("ast.operators");
        var subTypes = reflections.getSubTypesOf(Operator.class);
        for (var opClass : subTypes) {
            try {
                var op = (Operator) opClass.getDeclaredField("OP").get(null);
                if (op.toString().equals(text)) return op;
            } catch (Exception ignore) { }
        }
        return null;
    }

    public String getLLVMID() {
        return getClass().getSimpleName().toLowerCase();
    }
}
