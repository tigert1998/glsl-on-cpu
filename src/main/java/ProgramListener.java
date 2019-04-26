import ast.*;
import ast.exceptions.*;
import ast.expr.ConstExpr;
import ast.expr.Expr;
import ast.stmt.DeclarationStmt;
import ast.types.*;
import ast.values.*;

import java.util.*;

public class ProgramListener extends LangBaseListener {
    private Scope globalScope = new Scope();
    private List<SyntaxErrorException> exceptionList = new ArrayList<>();
    private ProgramAST programAST = new ProgramAST();

    public Scope getGlobalScope() {
        return globalScope;
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
            type = Utility.typeFromTypeContext(ctx.type(), globalScope);
        } catch (SyntaxErrorException exception) {
            exceptionList.add(exception);
            return;
        }

        var declarationList = ctx.constDeclarationList();
        int length = declarationList.variableMaybeArray().size();
        for (int i = 0; i < length; i++) {
            var variableMaybeArray = declarationList.variableMaybeArray(i);
            var constantVisitor = new ConstantVisitor(globalScope);
            Value value = declarationList.expr(i).accept(constantVisitor);
            String name = variableMaybeArray.IDENTIFIER().getText();
            if (value == null) {
                exceptionList.add(constantVisitor.exception);
                continue;
            }
            try {
                Type shouldType = Utility.typeWithArraySuffix(type,
                        variableMaybeArray.specifiedArrayLength(), globalScope);
                if (!shouldType.equals(value.getType())) {
                    exceptionList.add(SyntaxErrorException.cannotConvert(
                            variableMaybeArray.start, value.getType(), shouldType));
                    continue;
                }
            } catch (SyntaxErrorException exception) {
                exceptionList.add(exception);
                continue;
            }
            if (Utility.idDefinedBefore(name, globalScope)) {
                exceptionList.add(SyntaxErrorException.redefinition(variableMaybeArray.start, name));
                continue;
            }
            globalScope.constants.put(name, value);
        }
    }

    @Override
    public void exitNormalDeclarationStmt(LangParser.NormalDeclarationStmtContext ctx) {
        Type type;
        try {
            type = Utility.typeFromTypeContext(ctx.type(), globalScope);
        } catch (SyntaxErrorException exception) {
            exceptionList.add(exception);
            return;
        }
        var list = ctx.declarationList();
        list.declarationItem().forEach(item -> {
            Type actualType;
            var variableMaybeArray = item.variableMaybeArray();
            String id = variableMaybeArray.IDENTIFIER().getText();
            if (Utility.idDefinedBefore(id, globalScope)) {
                exceptionList.add(SyntaxErrorException.redefinition(item.start, id));
                return;
            }
            try {
                actualType = Utility.typeWithArraySuffix(type,
                        variableMaybeArray.specifiedArrayLength(), globalScope);
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
                var visitor = new ASTVisitor(globalScope);
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
            globalScope.variables.put(id, declarationStmt);
            programAST.putDeclarationStmt(declarationStmt);
        });
    }
}
