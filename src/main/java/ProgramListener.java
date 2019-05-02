import ast.*;
import ast.exceptions.*;
import ast.stmt.*;

import java.util.*;

public class ProgramListener extends LangBaseListener {
    private Scope scope = new Scope();
    private List<SyntaxErrorException> exceptionList = new ArrayList<>();
    private ProgramAST programAST = new ProgramAST();
    private int depth = 0;

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
    public void enterDeclarationStmt(LangParser.DeclarationStmtContext ctx) {
        depth++;
        if (depth >= 2) return;

        try {
            var wrapper = Utility.declarationStmtFromCtx(ctx, scope);
            wrapper.stmts.forEach(stmt -> {
                programAST.putDeclarationStmt((DeclarationStmt) stmt);
            });
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
        }
    }

    @Override
    public void exitDeclarationStmt(LangParser.DeclarationStmtContext ctx) {
        depth--;
    }

    @Override
    public void enterFunctionForwardDeclarationStmt(LangParser.FunctionForwardDeclarationStmtContext ctx) {
        depth++;
        if (depth >= 2) return;

        try {
            var sig = Utility.functionSignatureFromCtx(ctx.functionSignature(), scope);
            scope.declareFunction(sig);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
        } catch (ScopeException exception) {
            this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
        }
    }

    @Override
    public void exitFunctionForwardDeclarationStmt(LangParser.FunctionForwardDeclarationStmtContext ctx) {
        depth--;
    }

    @Override
    public void enterFunctionDefinition(LangParser.FunctionDefinitionContext ctx) {
        depth++;
        if (depth >= 2) return;

        try {
            var signature = Utility.functionSignatureFromCtx(ctx.functionSignature(), scope);
            var visitor = new ASTVisitor(scope);
            scope.screwIn();
            scope.defineParameters(signature);
            var result = (StmtsWrapper) ctx.compoundStmt().accept(visitor);
            scope.screwOut();
            if (result == null) {
                this.exceptionList.addAll(visitor.exceptionList);
                return;
            }

            scope.defineFunction(signature);
            var functionAST = new FunctionAST(signature, new CompoundStmt(result));
            programAST.putFunctionAST(functionAST);
        } catch (SyntaxErrorException exception) {
            this.exceptionList.add(exception);
        } catch (ScopeException exception) {
            this.exceptionList.add(new SyntaxErrorException(ctx.start, exception));
        }
    }

    @Override
    public void exitFunctionDefinition(LangParser.FunctionDefinitionContext ctx) {
        depth--;
    }
}
