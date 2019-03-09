import ast.*;

public class ProgramListener extends LangBaseListener {
    private Scope globalScope = new Scope();

    @Override
    public void enterFunctionForwardDeclarationStmt(LangParser.FunctionForwardDeclarationStmtContext ctx) {
        var fctx = ctx.functionSignature();

    }
}
