import ast.AST;
import ast.Scope;
import ast.exceptions.SyntaxErrorException;
import ast.expr.*;

public class ASTVisitor extends LangBaseVisitor<AST> {
    Scope scope;
    SyntaxErrorException exception;

    public ASTVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public AST visitLiteralExpr(LangParser.LiteralExprContext ctx) {
        var value = Utility.valueFromLiteralExprContext(ctx);
        return new ConstExpr(value);
    }

    @Override
    public AST visitReferenceExpr(LangParser.ReferenceExprContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        if (scope.constants.containsKey(name))
            return new ConstExpr(scope.constants.get(name));
        else if (scope.variables.containsKey(name))
            return null;
        return null;
    }
}
