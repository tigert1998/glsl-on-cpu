import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

import java.util.*;

public class ProgramListener extends LangBaseListener {
    private Scope globalScope = new Scope();
    private List<SyntaxErrorException> exceptionList = new ArrayList<>();

    public Scope getGlobalScope() {
        return globalScope;
    }

    public List<SyntaxErrorException> getExceptionList() {
        return exceptionList;
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
            if (globalScope.constants.containsKey(name) || globalScope.variables.containsKey(name)) {
                exceptionList.add(SyntaxErrorException.redefinition(variableMaybeArray.start, name));
                continue;
            }
            globalScope.constants.put(name, value);
        }
    }

    @Override
    public void exitNormalDeclarationStmt(LangParser.NormalDeclarationStmtContext ctx) {
        // only add struct declarations
        try {
            var type = Utility.typeFromTypeContext(ctx.type(), globalScope);
        } catch (SyntaxErrorException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
