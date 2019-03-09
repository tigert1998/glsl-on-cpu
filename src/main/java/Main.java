import ast.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

class ExprListener extends LangBaseListener {
    Stack<Expr> stack;

    @Override
    public void enterExpr(LangParser.ExprContext ctx) {
        
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        LangLexer langLexer = new LangLexer(CharStreams.fromFileName(filePath));
        LangParser langParser = new LangParser(new CommonTokenStream(langLexer));

        ParseTree parseTree = langParser.program();
        ParseTreeWalker.DEFAULT.walk(new ExprListener(), parseTree);
    }
}
