package ast.exceptions;

import ast.types.*;

public class ConstructionFailedException extends UnlocatedSyntaxErrorException {
    private ConstructionFailedException(String message) {
        super(message);
    }

    public static ConstructionFailedException arrayIncorrectType() {
        return new ConstructionFailedException("array constructor argument has an incorrect type");
    }

    public static ConstructionFailedException arraySizeUnmatched() {
        return new ConstructionFailedException("array constructor needs one argument per array element");
    }

    public static ConstructionFailedException arraySizeNotPositive() {
        return new ConstructionFailedException("array getN must be greater than zero");
    }

    public static ConstructionFailedException noArgument() {
        return new ConstructionFailedException("constructor does not have any arguments");
    }

    public static ConstructionFailedException tooManyArguments() {
        return new ConstructionFailedException("too many arguments");
    }

    public static ConstructionFailedException invalidConversion(Type from, Type to) {
        return new ConstructionFailedException("invalid conversion from " + from + " to " + to);
    }

    public static ConstructionFailedException notEnoughData() {
        return new ConstructionFailedException("not enough data provided for construction");
    }

    public static ConstructionFailedException matrixFromMatrix() {
        return new ConstructionFailedException("constructing matrix from matrix can only take one argument");
    }

    public static ConstructionFailedException fieldNumberNotMatch() {
        return new ConstructionFailedException(
                "number of constructor parameters does not match the number of structure fields");
    }

    public static ConstructionFailedException fieldTypeNotMatch() {
        return new ConstructionFailedException(
                "structure constructor arguments do not match structure fields");
    }
}
