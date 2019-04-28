package ast;

import ast.stmt.DeclarationStmt;
import ast.types.*;
import ast.values.*;

import java.util.*;

public class Scope {
    static public class InnerScope {
        public Map<String, Value> constants = new TreeMap<>();
        public Map<String, DeclarationStmt> variables = new TreeMap<>();
    }

    static public class FunctionInfo {
        private FunctionSignature functionSignature;
        private boolean defined;
    }

    public Map<String, List<FunctionInfo>> functions = new TreeMap<>();
    public Map<String, Value> constants = new TreeMap<>();
    public Map<String, DeclarationStmt> variables = new TreeMap<>();
    public Map<String, StructType> structs = new TreeMap<>();

    public Stack<InnerScope> innerScopes = new Stack<>();

    public boolean canDefineID(String id) {
        boolean redefinition;
        if (innerScopes.empty()) {
            redefinition = constants.containsKey(id) ||
                    variables.containsKey(id) ||
                    structs.containsKey(id) ||
                    functions.containsKey(id);
        } else {
            var scope = innerScopes.peek();
            redefinition = scope.constants.containsKey(id) || scope.variables.containsKey(id);
        }
        return !redefinition;
    }

    public boolean canDefineFunction(FunctionSignature sig) {
        if (!functions.containsKey(sig.id)) return true;
        for (var info : functions.get(sig.id)) {
            if (!info.defined) continue;
            if (sig.equals(info.functionSignature)) return false;
        }
        return !(constants.containsKey(sig.id) || variables.containsKey(sig.id) || structs.containsKey(sig.id));
    }

    public void LogConstants() {
        for (var kv : constants.entrySet()) {
            System.out.println("[" + kv.getKey() + "] " + kv.getValue());
        }
    }

    public void LogStructs() {
        for (var kv : structs.entrySet()) {
            System.out.println(kv.getValue().toDetailedString());
        }
    }
}