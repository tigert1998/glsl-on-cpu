import ast.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.*;

public class Main {
    public static void error(String message) {
        System.err.println(message);
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        LangLexer langLexer = new LangLexer(CharStreams.fromFileName(filePath));
        LangParser langParser = new LangParser(new CommonTokenStream(langLexer));

        ParseTree parseTree = langParser.program();
        ProgramListener programListener = new ProgramListener();
        ParseTreeWalker.DEFAULT.walk(programListener, parseTree);
        programListener.getGlobalScope().LogConstants();
    }
}
