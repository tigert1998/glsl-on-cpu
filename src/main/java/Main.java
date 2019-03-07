import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

class TestListener extends LangBaseListener {
    public void enterFunctionDefinition(LangParser.FunctionDefinitionContext ctx) {
        System.out.println(ctx.functionSignature().children.get(1).toString());
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        LangLexer langLexer = new LangLexer(CharStreams.fromFileName(filePath));
        LangParser langParser = new LangParser(new CommonTokenStream(langLexer));
        ParseTree parseTree = langParser.prog();
        ParseTreeWalker.DEFAULT.walk(new TestListener(), parseTree);
    }
}
