package ast;

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

    public void LogConstants() {
        for (var kv : constants.entrySet()) {
            System.out.println("[" + kv.getKey() + "] " + kv.getValue());
        }
    }
}
