package ast;

import java.util.*;

public class Scope {
    static class FunctionInfo {
        private FunctionSignature functionSignature;
        private boolean defined, referenced;
    }

    private Map<String, FunctionInfo> functions = new TreeMap<>();
}
