import ast.types.*;

public class StructDefinitionVisitor extends LangBaseVisitor<StructType> {
    @Override
    public StructType visitStructDefinition(LangParser.StructDefinitionContext ctx) {
        StructType result = new StructType(ctx.structName.getText());
        ctx.structFieldDeclarationStmt().forEach(stmtCtx -> {

        });
        return result;
    }
}
