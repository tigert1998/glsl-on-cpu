import ast.types.*;
import ast.values.*;

public class Utility {
    static private int getDigitAt(String text, int idx) {
        return text.charAt(idx) - '0';
    }

    static public Type typeFromBasicTypeContext(LangParser.BasicTypeContext ctx) {
        if (ctx.BOOL() != null) return BoolType.TYPE;
        if (ctx.BVECN() != null) return BvecnType.fromN(getDigitAt(ctx.BVECN().getText(), 4));
        if (ctx.FLOAT() != null) return FloatType.TYPE;
        if (ctx.INT() != null) return IntType.TYPE;
        if (ctx.IVECN() != null) return IvecnType.fromN(getDigitAt(ctx.IVECN().getText(), 4));
        if (ctx.MATNXM() != null) {
            int n = getDigitAt(ctx.MATNXM().getText(), 3);
            int m = getDigitAt(ctx.MATNXM().getText(), 5);
            return MatnxmType.fromNM(n, m);
        }
        if (ctx.MATN() != null) {
            int n = getDigitAt(ctx.MATN().getText(), 3);
            return MatnxmType.fromNM(n, n);
        }
        if (ctx.UINT() != null) return UintType.TYPE;
        if (ctx.UVECN() != null) return UvecnType.fromN(getDigitAt(ctx.UVECN().getText(), 4));
        if (ctx.VECN() != null) return VecnType.fromN(getDigitAt(ctx.VECN().getText(), 3));
        return null;
    }

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
        if (ctx.REAL_LITERAL() != null)
            return new FloatValue(Float.parseFloat(ctx.REAL_LITERAL().getText()));
        return null;
    }
}
