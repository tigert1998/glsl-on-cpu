package ast.values;

import ast.exceptions.*;

public interface Indexed {
    Value valueAt(int i) throws InvalidIndexException;
}
