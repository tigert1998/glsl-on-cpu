package ast.values;

import ast.exceptions.*;
import ast.types.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Value {
    protected Type type = null;
    private String id = null;

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    static private int dropFractionPart(float v) {
        return (int) (v - (v % 1));
    }

    static private Value extractSoleParameter(Value[] values) throws ConstructionFailedException {
        if (values.length == 0)
            throw ConstructionFailedException.noArgument();
        if (values.length >= 2)
            throw ConstructionFailedException.tooManyArguments();
        return values[0];
    }

    static private List<FloatValue> flattenThenConvertToFloatValue(Value[] values)
            throws ConstructionFailedException {
        List<FloatValue> valueList = new ArrayList<>();
        for (var value : values) {
            if (value instanceof Vectorized) {
                Value[] newValues = ((Vectorized) value).retrieve();
                for (var newValue : newValues)
                    valueList.add(constructor(FloatType.TYPE, new Value[]{newValue}));
            } else {
                valueList.add(constructor(FloatType.TYPE, new Value[]{value}));
            }
        }
        return valueList;
    }

    // type[]()
    static private ArrayValue constructor(ArrayType type, Value[] values) throws ConstructionFailedException {
        if (!type.isLengthUnknown() && type.getLength() != values.length)
            throw ConstructionFailedException.arraySizeUnmatched();
        if (values.length == 0)
            throw ConstructionFailedException.arraySizeNotPositive();
        for (var value : values) {
            if (!value.getType().equals(type.collapse()))
                throw ConstructionFailedException.arrayIncorrectType();
        }
        type.setLength(values.length);
        return new ArrayValue(type, values);
    }

    // int()
    static private IntValue constructor(IntType omitted, Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return new IntValue((int) (long) ((UintValue) value).value);
        } else if (value instanceof BoolValue) {
            return new IntValue(((BoolValue) value).value ? 1 : 0);
        } else if (value instanceof FloatValue) {
            return new IntValue(dropFractionPart(((FloatValue) value).value));
        } else if (value instanceof IntValue) {
            return (IntValue) value;
        } else if (value instanceof Vectorized) {
            return constructor(IntType.TYPE, new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), IntType.TYPE);
    }

    // float()
    static private FloatValue constructor(FloatType omitted, Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return new FloatValue(((UintValue) value).value);
        } else if (value instanceof BoolValue) {
            return new FloatValue(((BoolValue) value).value ? 1 : 0);
        } else if (value instanceof FloatValue) {
            return (FloatValue) value;
        } else if (value instanceof IntValue) {
            return new FloatValue(((IntValue) value).value);
        } else if (value instanceof Vectorized) {
            return constructor(FloatType.TYPE, new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), FloatType.TYPE);
    }

    // bool()
    static private BoolValue constructor(BoolType omitted, Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return new BoolValue(((UintValue) value).value != 0);
        } else if (value instanceof BoolValue) {
            return (BoolValue) value;
        } else if (value instanceof FloatValue) {
            return new BoolValue(((FloatValue) value).value != 0.f);
        } else if (value instanceof IntValue) {
            return new BoolValue(((IntValue) value).value != 0);
        } else if (value instanceof Vectorized) {
            return constructor(BoolType.TYPE, new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), BoolType.TYPE);
    }

    // uint()
    static private UintValue constructor(UintType omitted, Value[] values) throws ConstructionFailedException {
        var value = extractSoleParameter(values);
        if (value instanceof UintValue) {
            return (UintValue) value;
        } else if (value instanceof BoolValue) {
            return new UintValue(((BoolValue) value).value ? 1 : 0);
        } else if (value instanceof FloatValue) {
            return new UintValue(dropFractionPart(((FloatValue) value).value));
        } else if (value instanceof IntValue) {
            return new UintValue(((IntValue) value).value);
        } else if (value instanceof Vectorized) {
            return constructor(UintType.TYPE, new Value[]{((Vectorized) value).retrieve()[0]});
        } else throw ConstructionFailedException.invalidConversion(value.getType(), UintType.TYPE);
    }

    // vecn()
    static private VecnValue constructor(VecnType type, Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new VecnValue(type, constructor(FloatType.TYPE, values));
        }
        List<FloatValue> valueList = flattenThenConvertToFloatValue(values);
        if (valueList.size() < type.getN())
            throw ConstructionFailedException.notEnoughData();
        return new VecnValue(type, valueList);
    }

    // ivecn()
    static private IvecnValue constructor(IvecnType type, Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new IvecnValue(type, constructor(IntType.TYPE, values));
        }
        List<IntValue> valueList = new ArrayList<>();
        for (var value : values) {
            if (value instanceof Vectorized) {
                Value[] newValues = ((Vectorized) value).retrieve();
                for (var newValue : newValues)
                    valueList.add(constructor(IntType.TYPE, new Value[]{newValue}));
            } else {
                valueList.add(constructor(IntType.TYPE, new Value[]{value}));
            }
        }
        if (valueList.size() < type.getN())
            throw ConstructionFailedException.notEnoughData();
        return new IvecnValue(type, valueList);
    }

    // uvecn()
    static private UvecnValue constructor(UvecnType type, Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new UvecnValue(type, constructor(UintType.TYPE, values));
        }
        List<UintValue> valueList = new ArrayList<>();
        for (var value : values) {
            if (value instanceof Vectorized) {
                Value[] newValues = ((Vectorized) value).retrieve();
                for (var newValue : newValues)
                    valueList.add(constructor(UintType.TYPE, new Value[]{newValue}));
            } else {
                valueList.add(constructor(UintType.TYPE, new Value[]{value}));
            }
        }
        if (valueList.size() < type.getN())
            throw ConstructionFailedException.notEnoughData();
        return new UvecnValue(type, valueList);
    }

    // bvecn()
    static private BvecnValue constructor(BvecnType type, Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new BvecnValue(type, constructor(BoolType.TYPE, values));
        }
        List<BoolValue> valueList = new ArrayList<>();
        for (var value : values) {
            if (value instanceof Vectorized) {
                Value[] newValues = ((Vectorized) value).retrieve();
                for (var newValue : newValues)
                    valueList.add(constructor(BoolType.TYPE, new Value[]{newValue}));
            } else {
                valueList.add(constructor(BoolType.TYPE, new Value[]{value}));
            }
        }
        if (valueList.size() < type.getN())
            throw ConstructionFailedException.notEnoughData();
        return new BvecnValue(type, valueList);
    }

    static private MatnxmValue constructor(MatnxmType type, Value[] values) throws ConstructionFailedException {
        if (values.length == 0) throw ConstructionFailedException.noArgument();
        boolean hasMat = false;
        for (var value : values) if (value instanceof MatnxmValue) hasMat = true;
        if (hasMat && values.length >= 2)
            throw ConstructionFailedException.matrixFromMatrix();
        if (hasMat) return new MatnxmValue(type, (MatnxmValue) values[0]);
        if (values.length == 1 && !(values[0] instanceof Vectorized)) {
            return new MatnxmValue(type, constructor(FloatType.TYPE, values));
        }
        List<FloatValue> valueList = flattenThenConvertToFloatValue(values);
        if (valueList.size() < type.getN() * type.getM())
            throw ConstructionFailedException.notEnoughData();
        return new MatnxmValue(type, valueList);
    }

    static public Value constructor(Type type, Value[] values) throws ConstructionFailedException {
        try {
            var method = Value.class.getDeclaredMethod("constructor", type.getClass(), values.getClass());
            return (Value) method.invoke(null, type, values);
        } catch (InvocationTargetException exception) {
            throw (ConstructionFailedException) exception.getCause();
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(0);
            return null;
        }
    }
}
