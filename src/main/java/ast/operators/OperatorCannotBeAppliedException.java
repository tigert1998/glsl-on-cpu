package ast.operators;

public class OperatorCannotBeAppliedException extends Exception {
    public OperatorCannotBeAppliedException(String operatorName, String typeName) {
        super(operatorName + " cannot be applied on " + typeName);
    }
}
