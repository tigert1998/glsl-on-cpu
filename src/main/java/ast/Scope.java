package ast;

import java.util.*;

public class Scope {
    static class FunctionInfo {
        private FunctionDeclaration functionDeclaration;
        private boolean defined, referenced;
    }

    private Map<String, FunctionInfo> functions = new TreeMap<>();
}
