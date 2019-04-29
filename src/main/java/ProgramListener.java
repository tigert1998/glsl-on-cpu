import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

import java.util.*;

public class ProgramListener extends LangBaseListener {
    private Scope scope = new Scope();
    private List<SyntaxErrorException> exceptionList = new ArrayList<>();
    private ProgramAST programAST = new ProgramAST();

    public Scope getScope() {
        return scope;
    }

    public List<SyntaxErrorException> getExceptionList() {
        return exceptionList;
    }

    public ProgramAST getProgramAST() {
        return programAST;
    }

    @Override
    public void exitConstDeclarationStmt(LangParser.ConstDeclarationStmtContext ctx) {
        try {
            Utility.constantsFromCtx(ctx, scope);
        } catch (SyntaxErrorException exception) {
            exceptionList.add(exception);
        }
    }

    @Override
    public void exitNormalDeclarationStmt(LangParser.NormalDeclarationStmtContext ctx) {
        try {
            var stmts = Utility.normalDeclarationStmtsFromCtx(ctx, scope);
            stmts.forEach(stmt -> programAST.putDeclarationStmt(stmt));
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
        }
    }

    @Override
    public void exitFunctionForwardDeclarationStmt(LangParser.FunctionForwardDeclarationStmtContext ctx) {
        try {
            var sig = Utility.functionSignatureFromCtx(ctx.functionSignature(), scope);
            if (!scope.canDeclareFunction(sig)) {
                this.exceptionList.add(SyntaxErrorException.functionRedefinition(ctx.start, sig.id));
                return;
            }
            scope.declareFunction(sig);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
        }
    }

    @Override
    public void exitFunctionDefinition(LangParser.FunctionDefinitionContext ctx) {
        try {
            var sig = Utility.functionSignatureFromCtx(ctx.functionSignature(), scope);
            if (!scope.canDefineFunction(sig)) {
                this.exceptionList.add(SyntaxErrorException.functionRedefinition(ctx.start, sig.id));
                return;
            }

            var functionAST = new FunctionAST(sig);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
        }
    }
}
