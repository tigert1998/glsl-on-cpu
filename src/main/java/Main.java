import codegen.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.*;
import org.antlr.v4.runtime.tree.*;

import org.apache.commons.cli.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    private static Path filePath = null;
    private static boolean dumpAST = false, verbose = false;

    private static void parseArgs(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true,
                "Input file path");
        input.setRequired(false);
        options.addOption(input);

        Option dumpAST = new Option("a", "dump-ast", false,
                "Dump debugging AST");
        dumpAST.setRequired(false);
        options.addOption(dumpAST);

        Option help = new Option("h", "help", false,
                "Helping info");
        help.setRequired(false);
        options.addOption(help);

        Option verbose = new Option("v", "verbose", false,
                "Print verbose scope and function information");
        verbose.setRequired(false);
        options.addOption(verbose);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("glsl-on-cpu", options);
            System.exit(1);
        }

        if (cmd.hasOption(help.getLongOpt())) {
            formatter.printHelp("glsl-on-cpu", options);
            System.exit(0);
        }

        if (!cmd.hasOption(input.getLongOpt())) {
            System.err.println("Missing required option: i");
            System.exit(1);
        }

        filePath = Paths.get(cmd.getOptionValue(input.getLongOpt()));
        if (!filePath.toFile().isFile()) {
            System.err.println("no such file: '" + filePath.toString() + "'");
            System.exit(1);
        }
        Main.dumpAST = cmd.hasOption(dumpAST.getLongOpt());
        Main.verbose = cmd.hasOption(verbose.getLongOpt());
    }

    public static void main(String[] args) throws Exception {
        parseArgs(args);
        LangLexer langLexer = new LangLexer(CharStreams.fromFileName(filePath.toString()));
        LangParser langParser = new LangParser(new CommonTokenStream(langLexer));
        langParser.removeErrorListeners();
        langParser.addErrorListener(new ErrorListener());

        ProgramListener programListener = new ProgramListener();
        ParseTreeWalker.DEFAULT.walk(programListener, langParser.program());

        if (verbose) {
            programListener.getScope().logConstants();
            programListener.getScope().logStructs();
            programListener.getScope().logFunctions();
        }

        String fileName = filePath.getFileName().toString();
        if (fileName.lastIndexOf('.') >= 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        if (dumpAST) {
            var writer = new FileWriter(
                    Paths.get(
                            filePath.getParent().toString(),
                            fileName + ".json").toString()
            );
            programListener.getProgramAST().toJSON().write(writer, 2, 0);
            writer.close();
        }

        for (var exception : programListener.getExceptionList()) {
            System.err.println(exception.getMessage());
        }
        if (!programListener.getExceptionList().isEmpty()) {
            System.exit(1);
        }

        var generator = new CodeGenerator(fileName,
                programListener.getScope(),
                programListener.getProgramAST());
        generator.dump(Paths.get(
                filePath.getParent().toString(), fileName + ".bc").toString());
    }
}

class ErrorListener implements ANTLRErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        System.err.println(line + ":" + charPositionInLine + ": " + msg);
        System.exit(1);
    }

    @Override
    public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
    }

    @Override
    public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                            BitSet conflictingAlts, ATNConfigSet configs) {
    }

    @Override
    public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
                                         int prediction, ATNConfigSet configs) {
    }
}
