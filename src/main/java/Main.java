import codegen.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        LangLexer langLexer = new LangLexer(CharStreams.fromFileName(filePath));
        LangParser langParser = new LangParser(new CommonTokenStream(langLexer));

        ProgramListener programListener = new ProgramListener();
        ParseTreeWalker.DEFAULT.walk(programListener, langParser.program());
        programListener.getScope().logConstants();
        programListener.getScope().logStructs();
        programListener.getScope().logFunctions();

        var writer = new FileWriter("/Users/tigertang/CodeSandBox/ast.json");
        programListener.getProgramAST().toJSON().write(writer, 2, 0);
        writer.close();

        for (var exception : programListener.getExceptionList()) {
            System.out.println(exception.getMessage());
        }
        if (!programListener.getExceptionList().isEmpty()) {
            System.exit(0);
        }

        var generator = new CodeGenerator("fuck", programListener.getScope(), programListener.getProgramAST());
        generator.dump("/Users/tigertang/CodeSandBox/fuck.bc");
    }
}
