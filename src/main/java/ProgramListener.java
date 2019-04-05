import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;

public class ProgramListener extends LangBaseListener {
    private Scope globalScope = new Scope();

    public Scope getGlobalScope() {
        return globalScope;
    }

    @Override
    public void exitConstDeclarationStmt(LangParser.ConstDeclarationStmtContext ctx) {
        Type type;
        try {
            type = Utility.typeFromTypeContext(ctx.type(), globalScope);
        } catch (SyntaxErrorException exception) {
            System.out.println(exception.getMessage());
            return;
        }

        var declarationList = ctx.constDeclarationList();
        int length = declarationList.variableMaybeArray().size();
        for (int i = 0; i < length; i++) {
            var variableMaybeArray = declarationList.variableMaybeArray(i);
            ConstantVisitor constantVisitor = new ConstantVisitor(globalScope);
            Value value = declarationList.expr(i).accept(constantVisitor);
            String name = variableMaybeArray.IDENTIFIER().getText();
            if (value == null) {
                System.out.println(constantVisitor.exception.getMessage());
                continue;
            }
            globalScope.constants.put(name, value);
        }
    }

    @Override
    public void exitNormalDeclarationStmt(LangParser.NormalDeclarationStmtContext ctx) {
        try {
            var type = Utility.typeFromTypeContext(ctx.type(), globalScope);
        } catch (SyntaxErrorException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
