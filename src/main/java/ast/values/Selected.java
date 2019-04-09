package ast.values;

import ast.exceptions.*;

public interface Selected {
    Value select(String name) throws InvalidSelectionException;
}
