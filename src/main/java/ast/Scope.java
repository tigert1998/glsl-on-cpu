package ast;

import ast.exceptions.ScopeException;
import ast.stmt.*;
import ast.types.*;
import ast.values.*;

import java.util.*;

public class Scope {
    static public class LookupResult {
        public DeclarationStmt stmt;
        public Value value;
    }

    static public class InnerScope {
        public Map<String, Value> constants = new TreeMap<>();
        public Map<String, DeclarationStmt> variables = new TreeMap<>();
        public Map<String, StructType> structs = new TreeMap<>();
    }

    static public class FunctionInfo {
        private FunctionSignature functionSignature;
        private boolean defined;

        public FunctionInfo(FunctionSignature functionSignature, boolean defined) {
            this.functionSignature = functionSignature;
            this.defined = defined;
        }
    }

    public Map<String, List<FunctionInfo>> functions = new TreeMap<>();

    public Stack<InnerScope> innerScopes;

    public Scope() {
        innerScopes = new Stack<>();
        innerScopes.push(new InnerScope());
    }

    public void screwIn() {
        innerScopes.push(new InnerScope());
    }

    public void screwOut() {
        innerScopes.pop();
    }

    public LookupResult lookupConstantOrVariable(String id) {
        LookupResult result = new LookupResult();
        for (int i = innerScopes.size() - 1; i >= 0; i--) {
            var scope = innerScopes.get(i);
            if (scope.constants.containsKey(id)) {
                result.value = scope.constants.get(id);
                return result;
            } else if (scope.variables.containsKey(id)) {
                result.stmt = scope.variables.get(id);
                return result;
            }
        }
        return null;
    }

    public void defineConstant(String id, Value value) {
        innerScopes.peek().constants.put(id, value);
    }

    public void defineVariable(DeclarationStmt stmt) {
        innerScopes.peek().variables.put(stmt.id, stmt);
    }

    public void defineStructType(StructType type) {
        innerScopes.peek().structs.put(type.id, type);
    }

    public void declareFunction(FunctionSignature sig) throws ScopeException {
        checkDeclareFunction(sig);
        var list = functions.computeIfAbsent(sig.id, k -> new ArrayList<>());
        for (var info : list) {
            if (info.functionSignature.equals(sig)) return;
        }
        list.add(new FunctionInfo(sig, false));
    }

    public void defineFunction(FunctionSignature sig) throws ScopeException {
        checkDefineFunction(sig);
        var list = functions.computeIfAbsent(sig.id, k -> new ArrayList<>());
        for (var info : list) {
            if (info.functionSignature.equals(sig)) {
                info.defined = true;
                return;
            }
        }
        list.add(new FunctionInfo(sig, true));
    }

    public boolean canDefineID(String id) {
        var scope = innerScopes.peek();
        boolean redefinition = scope.constants.containsKey(id) ||
                scope.variables.containsKey(id) || scope.structs.containsKey(id);
        if (innerScopes.size() == 1) {
            redefinition |= functions.containsKey(id);
        }
        return !redefinition;
    }

    private void checkSkewedSignature(FunctionSignature sig) throws ScopeException {
        if (!functions.containsKey(sig.id)) return;
        for (var info : functions.get(sig.id)) {
            if (sig.equals(info.functionSignature) && !sig.equalWithQualifiers(info.functionSignature))
                throw ScopeException.sameQualifier();
        }
    }

    private void checkFunctionIDRedefinition(FunctionSignature sig) throws ScopeException {
        if (!functions.containsKey(sig.id)) return;
        var scope = innerScopes.get(0);
        if (scope.constants.containsKey(sig.id) ||
                scope.variables.containsKey(sig.id) || scope.structs.containsKey(sig.id)) {
            throw ScopeException.functionRedefinition(sig.id);
        }
    }

    private void checkDeclareFunction(FunctionSignature sig) throws ScopeException {
        if (!functions.containsKey(sig.id)) return;
        checkSkewedSignature(sig);
        checkFunctionIDRedefinition(sig);
    }

    private void checkDefineFunction(FunctionSignature sig) throws ScopeException {
        if (!functions.containsKey(sig.id)) return;
        checkSkewedSignature(sig);
        checkFunctionIDRedefinition(sig);
        for (var info : functions.get(sig.id)) {
            if (info.defined && sig.equals(info.functionSignature)) {
                throw ScopeException.alreadyBody(sig.id);
            }
        }
    }

    public void logFunctions() {
        for (var kv : functions.entrySet()) {
            var list = kv.getValue();
            for (var info : list) {
                System.out.println(info.functionSignature + ";");
            }
        }
    }

    public void logConstants() {
        for (var kv : innerScopes.get(0).constants.entrySet()) {
            System.out.println("[" + kv.getKey() + "] " + kv.getValue());
        }
    }

    public void logStructs() {
        for (var kv : innerScopes.get(0).structs.entrySet()) {
            System.out.println(kv.getValue().toDetailedString());
        }
    }
}