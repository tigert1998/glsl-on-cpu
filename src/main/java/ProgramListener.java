import ast.*;
import ast.exceptions.*;
import ast.expr.ConstExpr;
import ast.expr.Expr;
import ast.stmt.DeclarationStmt;
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
        Type type;
        try {
            type = Utility.typeFromTypeContext(ctx.type(), scope);
        } catch (SyntaxErrorException exception) {
            exceptionList.add(exception);
            return;
        }

        var declarationList = ctx.constDeclarationList();
        int length = declarationList.variableMaybeArray().size();
        for (int i = 0; i < length; i++) {
            var variableMaybeArray = declarationList.variableMaybeArray(i);
            var constantVisitor = new ConstantVisitor(scope);
            Value value = declarationList.expr(i).accept(constantVisitor);
            String id = variableMaybeArray.IDENTIFIER().getText();
            if (value == null) {
                exceptionList.add(constantVisitor.exception);
                continue;
            }
            try {
                Type actualType = Utility.typeWithArraySuffix(type,
                        variableMaybeArray.specifiedArrayLength(), scope);
                if (!actualType.equals(value.getType())) {
                    exceptionList.add(SyntaxErrorException.cannotConvert(
                            variableMaybeArray.start, value.getType(), actualType));
                    continue;
                }
            } catch (SyntaxErrorException exception) {
                exceptionList.add(exception);
                continue;
            }
            if (!scope.canDefineID(id)) {
                exceptionList.add(SyntaxErrorException.redefinition(variableMaybeArray.start, id));
                continue;
            }
            scope.constants.put(id, value);
        }
    }

    @Override
    public void exitNormalDeclarationStmt(LangParser.NormalDeclarationStmtContext ctx) {
        Type type;
        try {
            type = Utility.typeFromTypeContext(ctx.type(), scope);
        } catch (SyntaxErrorException exception) {
            exceptionList.add(exception);
            return;
        }
        var list = ctx.declarationList();
        list.declarationItem().forEach(item -> {
            Type actualType;
            var variableMaybeArray = item.variableMaybeArray();
            String id = variableMaybeArray.IDENTIFIER().getText();
            if (!scope.canDefineID(id)) {
                exceptionList.add(SyntaxErrorException.redefinition(item.start, id));
                return;
            }
            try {
                actualType = Utility.typeWithArraySuffix(type,
                        variableMaybeArray.specifiedArrayLength(), scope);
            } catch (SyntaxErrorException exception) {
                exceptionList.add(exception);
                return;
            }
            DeclarationStmt declarationStmt;
            if (item.expr() == null) {
                if (actualType instanceof ArrayType && ((ArrayType) actualType).isLengthUnknown()) {
                    this.exceptionList.add(SyntaxErrorException.implicitSizedArray(variableMaybeArray.start));
                    return;
                }
                declarationStmt = new DeclarationStmt(actualType, id, new ConstExpr(actualType.getDefaultValue()));
            } else {
                var visitor = new ASTVisitor(scope);
                var expr = (Expr) item.expr().accept(visitor);
                if (expr == null) {
                    exceptionList.add(visitor.exception);
                    return;
                }
                if (!expr.getType().equals(actualType)) {
                    exceptionList.add(SyntaxErrorException.cannotConvert(item.expr().start, expr.getType(), actualType));
                    return;
                }
                actualType = expr.getType();
                declarationStmt = new DeclarationStmt(actualType, id, expr);
            }
            scope.variables.put(id, declarationStmt);
            programAST.putDeclarationStmt(declarationStmt);
        });
    }

    @Override
    public void exitFunctionSignature(LangParser.FunctionSignatureContext ctx) {
        try {
            System.out.println(Utility.functionSignatureFromCtx(ctx, scope));
        } catch (SyntaxErrorException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
