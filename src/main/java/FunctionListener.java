import ast.*;
import ast.exceptions.*;
import ast.expr.*;
import ast.stmt.*;
import ast.types.*;
import java.util.*;

public class FunctionListener extends LangBaseListener {
    private Scope scope;
    private FunctionAST functionAST;
    private List<SyntaxErrorException> exceptionList;

    public FunctionListener(Scope scope, FunctionAST functionAST) {
        this.scope = scope;
        this.functionAST = functionAST;
    }

    @Override
    public void exitNormalDeclarationStmt(LangParser.NormalDeclarationStmtContext ctx) {


    }
}
