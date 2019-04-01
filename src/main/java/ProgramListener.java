import ast.*;
import ast.types.*;
import ast.values.*;

public class ProgramListener extends LangBaseListener {
    private Scope globalScope = new Scope();

    public Scope getGlobalScope() {
        return globalScope;
    }

    @Override
    public void exitConstDeclarationStmt(LangParser.ConstDeclarationStmtContext ctx) {
        if (ctx.type().structType() != null) {
            Main.error("struct isn't supported yet!");
        }

        Type type = Utility.typeFromBasicTypeContext(ctx.type().basicType());

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
}
