package ast.values;

import ast.types.*;

public abstract class Value {
    protected Type type;

    public Type getType() {
        return type;
    }

    private static int parseIntLiteralText(String str) {
        if (str.startsWith("0x") || str.startsWith("0X"))
            return Integer.parseInt(str.substring(2), 16);
        if (str.charAt(0) == '0')
            return Integer.parseInt(str, 8);
        return Integer.parseInt(str, 10);
    }

    public static Value fromLiteralExprContext(LangParser.LiteralExprContext ctx) {
        if (ctx.boolLiteral() != null)
            return new BoolValue(ctx.boolLiteral().FALSE() == null);
        if (ctx.INT_LITERAL() != null)
            return new IntValue(parseIntLiteralText(ctx.INT_LITERAL().getText()));
        if (ctx.UINT_LITERAL() != null) {
            String str = ctx.UINT_LITERAL().getText();
            return new UintValue(parseIntLiteralText(str.substring(0, str.length() - 1)));
        }
        if (ctx.REAL_LITERAL() != null)
            return new FloatValue(Float.parseFloat(ctx.REAL_LITERAL().getText()));
        return null;
    }
}
