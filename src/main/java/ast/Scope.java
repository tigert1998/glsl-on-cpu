package ast;

import ast.types.*;
import ast.values.*;

import java.util.*;

public class Scope {
    static public class FunctionInfo {
        private FunctionSignature functionSignature;
        private boolean defined, referenced;
    }

    public Map<String, FunctionInfo> functions = new TreeMap<>();
    public Map<String, Value> constants = new TreeMap<>();
    public Map<String, Value> variables = new TreeMap<>();
}
