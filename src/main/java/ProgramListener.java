import ast.*;
import ast.types.*;

public class ProgramListener extends LangBaseListener {
    private Scope globalScope = new Scope();

    @Override
    public void enterConstDeclarationStmt(LangParser.ConstDeclarationStmtContext ctx) {
        if (ctx.type().structType() != null) {
            Main.error("struct isn't supported yet!");
        }

        Type type = Utility.typeFromBasicTypeContext(ctx.type().basicType());

        var declarationList = ctx.constDeclarationList();
        int length = declarationList.variableMaybeArray().size();
        for (int i = 0; i < length; i++) {
            var variableMaybeArray = declarationList.variableMaybeArray(i);
            var expr = declarationList.expr(i);
        }
    }
}
