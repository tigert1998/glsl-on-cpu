import ast.*;
import ast.exceptions.*;
import ast.types.*;
import ast.values.*;
import org.antlr.v4.runtime.Token;

public class Utility {
    private static int parseIntLiteralText(String str) {
        if (str.startsWith("0x") || str.startsWith("0X"))
            return Integer.parseInt(str.substring(2), 16);
        if (str.charAt(0) == '0')
            return Integer.parseInt(str, 8);
        return Integer.parseInt(str, 10);
    }

    public static Value valueFromLiteralExprContext(LangParser.LiteralExprContext ctx) {
        if (ctx.boolLiteral() != null)
            return new BoolValue(ctx.boolLiteral().FALSE() == null);
        if (ctx.INT_LITERAL() != null)
            return new IntValue(parseIntLiteralText(ctx.INT_LITERAL().getText()));
        if (ctx.UINT_LITERAL() != null) {
            String str = ctx.UINT_LITERAL().getText();
            return new UintValue(parseIntLiteralText(str.substring(0, str.length() - 1)));
        }
        return new FloatValue(Float.parseFloat(ctx.REAL_LITERAL().getText()));
    }

    public static int evalValueAsIntegral(Value value, Token token) throws SyntaxErrorException {
        Integer res = Value.evalAsIntegral(value);
        if (res == null) throw SyntaxErrorException.notIntegerExpression(token);
        return res;
    }

    public static int evalExprAsIntegral(LangParser.ExprContext exprCtx, Scope scope) throws SyntaxErrorException {
        var visitor = new ConstantVisitor(scope);
        Value value = exprCtx.accept(visitor);
        if (visitor.exception != null) throw visitor.exception;
        return evalValueAsIntegral(value, exprCtx.start);
    }

    public static int evalExprAsArraySize(LangParser.ExprContext exprCtx, Scope scope) throws SyntaxErrorException {
        int res = evalExprAsIntegral(exprCtx, scope);
        if (res <= 0)
            throw SyntaxErrorException.arraySizeNotPositive(exprCtx.start);
        return res;
    }

    public static StructType typeFromStructDefinitionContext(LangParser.StructDefinitionContext ctx, Scope scope)
            throws SyntaxErrorException {
        String id = ctx.structName.getText();
        if (!scope.canDefineID(id))
            throw SyntaxErrorException.redefinition(ctx.structName, id);

        StructType result = new StructType(id);
        for (int i = 0; i < ctx.structFieldDeclarationStmt().size(); i++) {
            var stmtCtx = ctx.structFieldDeclarationStmt(i);

            if (stmtCtx.type().structType() != null && stmtCtx.type().structType().structDefinition() != null)
                throw SyntaxErrorException.embeddedStructDefinition(stmtCtx.type().start);

            Type type = typeFromTypeContext(stmtCtx.type(), scope);
            for (int j = 0; j < stmtCtx.variableMaybeArray().size(); j++) {
                var varCtx = stmtCtx.variableMaybeArray(j);
                String varID = varCtx.IDENTIFIER().getText();

                Type actualType = typeWithArraySuffix(type, varCtx.specifiedArrayLength(), scope);
                if (actualType instanceof ArrayType && ((ArrayType) actualType).isLengthUnknown())
                    throw SyntaxErrorException.structArrayMemberUnknownSize(varCtx.start, varID);

                if (result.fieldIDExists(varID))
                    throw SyntaxErrorException.duplicateFieldName(varCtx.start, varID);
                result.addFieldInfo(new StructType.FieldInfo(varID, actualType));
            }
        }

        scope.structs.put(id, result);
        return result;
    }

    private static Type typeWithoutArrayFromBasicTypeContext(LangParser.BasicTypeContext ctx) {
        if (ctx.BOOL() != null) return BoolType.TYPE;
        else if (ctx.INT() != null) return IntType.TYPE;
        else if (ctx.UINT() != null) return UintType.TYPE;
        else if (ctx.FLOAT() != null) return FloatType.TYPE;
        else if (ctx.BVECN() != null) return BvecnType.fromText(ctx.BVECN().getText());
        else if (ctx.IVECN() != null) return IvecnType.fromText(ctx.IVECN().getText());
        else if (ctx.UVECN() != null) return UvecnType.fromText(ctx.UVECN().getText());
        else if (ctx.VECN() != null) return VecnType.fromText(ctx.VECN().getText());
        else if (ctx.MATNXM() != null) return MatnxmType.fromText(ctx.MATNXM().getText());
        else return MatnxmType.fromText(ctx.MATN().getText());
    }

    public static Type typeWithArraySuffix(Type type, LangParser.SpecifiedArrayLengthContext ctx, Scope scope)
            throws SyntaxErrorException {
        if (ctx == null) return type;

        if (type instanceof ArrayType) throw SyntaxErrorException.arrayOfArrays(ctx.start);

        if (ctx.expr() == null) return new ArrayType(type);
        int len = evalExprAsArraySize(ctx.expr(), scope);
        return new ArrayType(type, len);
    }

    public static Type typeFromBasicTypeContext(LangParser.BasicTypeContext ctx, Scope scope)
            throws SyntaxErrorException {
        Type type = typeWithoutArrayFromBasicTypeContext(ctx);
        return typeWithArraySuffix(type, ctx.specifiedArrayLength(), scope);
    }

    public static Type typeFromStructTypeContext(LangParser.StructTypeContext ctx, Scope scope)
            throws SyntaxErrorException {
        Type type;
        if (ctx.IDENTIFIER() != null) {
            String id = ctx.IDENTIFIER().getText();
            if (!scope.structs.containsKey(id))
                throw SyntaxErrorException.undeclaredID(ctx.start, id);
            type = scope.structs.get(id);
        } else {
            type = typeFromStructDefinitionContext(ctx.structDefinition(), scope);
        }
        return typeWithArraySuffix(type, ctx.specifiedArrayLength(), scope);
    }

    public static Type typeFromTypeContext(LangParser.TypeContext ctx, Scope scope) throws SyntaxErrorException {
        if (ctx.basicType() != null) {
            return typeFromBasicTypeContext(ctx.basicType(), scope);
        } else {
            return typeFromStructTypeContext(ctx.structType(), scope);
        }
    }

    public static FunctionSignature functionSignatureFromCtx(LangParser.FunctionSignatureContext ctx, Scope scope)
            throws SyntaxErrorException {
        String id = ctx.IDENTIFIER().getText();
        var returnType = ctx.VOID() != null ? null : typeFromTypeContext(ctx.type(), scope);
        var functionSignature = new FunctionSignature(returnType, id);
        var listCtx = ctx.functionParameterList();
        if (listCtx.functionParameter() != null) {
            for (int i = 0; i < listCtx.functionParameter().size(); i++) {
                var parameterCtx = listCtx.functionParameter(i);

                FunctionSignature.ParameterQualifier qualifier;
                if (parameterCtx.CONST() != null) qualifier = FunctionSignature.ParameterQualifier.CONST_IN;
                else if (parameterCtx.OUT() != null) qualifier = FunctionSignature.ParameterQualifier.OUT;
                else if (parameterCtx.INOUT() != null) qualifier = FunctionSignature.ParameterQualifier.INOUT;
                else qualifier = FunctionSignature.ParameterQualifier.IN;

                var type = typeFromTypeContext(parameterCtx.type(), scope);
                type = typeWithArraySuffix(type, parameterCtx.variableMaybeArray().specifiedArrayLength(), scope);

                String varID = parameterCtx.variableMaybeArray().IDENTIFIER().getText();

                functionSignature.addParameter(qualifier, type, varID);
            }
        }

        return functionSignature;
    }
}
