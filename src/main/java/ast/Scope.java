package ast;

import ast.exceptions.*;
import ast.stmt.*;
import ast.types.*;
import ast.values.*;
import org.bytedeco.llvm.LLVM.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class Scope {
    static public class LookupResult {
        public DeclarationStmt stmt;
        public Value value;
        public FunctionSignature.ParameterInfo parameter;
    }

    static public class InnerScope {
        public Map<String, Value> constants = new TreeMap<>();
        public Map<String, DeclarationStmt> variables = new TreeMap<>();
        public Map<String, StructType> structs = new TreeMap<>();
        public FunctionSignature functionSignature = null;
    }

    static public class FunctionInfo {
        private FunctionSignature functionSignature;
        private boolean defined;
        private LLVMValueRef value = null;

        public FunctionInfo(FunctionSignature functionSignature, boolean defined) {
            this.functionSignature = functionSignature;
            this.defined = defined;
        }

        public LLVMValueRef getValue(LLVMModuleRef module) {
            if (value == null) {
                value = LLVMAddFunction(module, functionSignature.getLLVMID(), functionSignature.inLLVM());
                if (!defined) LLVMSetLinkage(value, LLVMExternalLinkage);
            }
            return value;
        }
    }

    public Map<String, List<FunctionInfo>> functions = new TreeMap<>();

    public Map<String, LLVMValueRef> builtInFunctions = new TreeMap<>();

    public Set<String> cLinkageFunctionIDs = new TreeSet<>();

    public Stack<InnerScope> innerScopes;

    private Stack<ControlFlowManager> controlFlowManagers = new Stack<>();

    public void pushControlFlowManager(ControlFlowManager manager) {
        controlFlowManagers.push(manager);
    }

    public void popControlFlowManager() {
        controlFlowManagers.pop();
    }

    public ControlFlowManager loopupBreak() {
        for (int i = controlFlowManagers.size() - 1; i >= 0; i--) {
            if (controlFlowManagers.get(i).allowBreak) return controlFlowManagers.get(i);
        }
        return null;
    }

    public ControlFlowManager loopupContinue() {
        for (int i = controlFlowManagers.size() - 1; i >= 0; i--) {
            if (controlFlowManagers.get(i).allowContinue) return controlFlowManagers.get(i);
        }
        return null;
    }

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

    public LLVMValueRef lookupLLVMFunction(FunctionSignature sig, LLVMModuleRef module) {
        for (var info : functions.get(sig.id)) {
            if (info.functionSignature.match(sig))
                return info.getValue(module);
        }
        return null;
    }

    public FunctionSignature lookupFunction(String id, Type[] types) {
        if (!functions.containsKey(id)) return null;
        outer:
        for (var fun : functions.get(id)) {
            var sig = fun.functionSignature;
            if (types.length != sig.parameters.size()) continue;
            for (int i = 0; i < types.length; i++)
                if (!types[i].equals(sig.parameters.get(i).type)) continue outer;
            return sig;
        }
        return null;
    }

    public StructType lookupStructure(String id) {
        for (int i = innerScopes.size() - 1; i >= 0; i--) {
            var scope = innerScopes.get(i);
            if (scope.structs.containsKey(id)) {
                return scope.structs.get(id);
            }
        }
        return null;
    }

    public LookupResult lookupValue(String id) {
        LookupResult result = new LookupResult();
        for (int i = innerScopes.size() - 1; i >= 0; i--) {
            var scope = innerScopes.get(i);
            if (scope.constants.containsKey(id)) {
                result.value = scope.constants.get(id);
                return result;
            } else if (scope.variables.containsKey(id)) {
                result.stmt = scope.variables.get(id);
                return result;
            } else if (scope.functionSignature != null && scope.functionSignature.parametersMap.containsKey(id)) {
                result.parameter = scope.functionSignature.parametersMap.get(id);
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

    public void removeFunction(FunctionSignature sig) {
        var list = functions.computeIfAbsent(sig.id, k -> new ArrayList<>());
        for (var info : list) {
            if (info.functionSignature.match(sig)) {
                list.remove(info);
                return;
            }
        }
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

    public void declareCLinkageFunction(FunctionSignature sig) throws ScopeException {
        checkDeclareCLinkageFunctions(sig);
        sig.setCLinkage();
        declareFunction(sig);
        cLinkageFunctionIDs.add(sig.id);
    }

    public boolean canDefineID(String id) {
        var scope = innerScopes.peek();
        boolean redefinition = scope.constants.containsKey(id) ||
                scope.variables.containsKey(id) ||
                scope.structs.containsKey(id) ||
                (scope.functionSignature != null && scope.functionSignature.parametersMap.containsKey(id));
        if (innerScopes.size() == 1) {
            redefinition |= functions.containsKey(id);
        }
        return !redefinition;
    }

    private void checkSkewedSignature(FunctionSignature sig) throws ScopeException {
        if (!functions.containsKey(sig.id)) return;
        for (var info : functions.get(sig.id)) {
            if (sig.match(info.functionSignature) && !sig.equals(info.functionSignature))
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

    private void checkDeclareCLinkageFunctions(FunctionSignature sig) throws ScopeException {
        if (cLinkageFunctionIDs.contains(sig.id))
            throw ScopeException.declarationCLinkage(sig);
        if (!functions.containsKey(sig.id)) return;
        for (var info : functions.get(sig.id)) {
            if (info.functionSignature.match(sig))
                throw ScopeException.declarationCLinkage(sig);
        }
    }

    public void logFunctions() {
        for (var kv : functions.entrySet()) {
            var list = kv.getValue();
            for (var info : list) {
                if (info.functionSignature.cLinkage) System.out.print("(C) ");
                System.out.println(info.functionSignature + ";");
            }
        }
    }

    public void logConstants() {
        for (var kv : innerScopes.get(0).constants.entrySet()) {
            var valuePtr = LLVMPrintValueToString(kv.getValue().inLLVM());
            System.out.println(kv.getKey() + " = " + valuePtr.getString());
        }
    }

    public void logStructs() {
        for (var kv : innerScopes.get(0).structs.entrySet()) {
            System.out.println(kv.getValue().toDetailedString());
        }
    }
}