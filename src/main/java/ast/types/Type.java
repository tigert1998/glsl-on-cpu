package ast.types;

public abstract class Type {
    static private int getDigitAt(String text, int idx) {
        return text.charAt(idx) - '0';
    }

    static public Type fromBasicTypeContext(LangParser.BasicTypeContext ctx) {
        if (ctx.BOOL() != null) return BoolType.TYPE;
        if (ctx.BVECN() != null) return BvecnType.fromN(getDigitAt(ctx.BVECN().getText(), 4));
        if (ctx.FLOAT() != null) return FloatType.TYPE;
        if (ctx.INT() != null) return IntType.TYPE;
        if (ctx.IVECN() != null) return IvecnType.fromN(getDigitAt(ctx.IVECN().getText(), 4));
        if (ctx.MATNXM() != null) {
            int n = getDigitAt(ctx.MATNXM().getText(), 3);
            int m = getDigitAt(ctx.MATNXM().getText(), 5);
            return new MatnxmType(n, m);
        }
        if (ctx.MATN() != null) {
            int n = getDigitAt(ctx.MATN().getText(), 3);
            return new MatnxmType(n, n);
        }
        if (ctx.UINT() != null) return UintType.TYPE;
        if (ctx.UVECN() != null) return UvecnType.fromN(getDigitAt(ctx.UVECN().getText(), 4));
        if (ctx.VECN() != null) return VecnType.fromN(getDigitAt(ctx.VECN().getText(), 3));
        return null;
    }
}
