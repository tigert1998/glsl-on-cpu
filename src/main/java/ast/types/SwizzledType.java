package ast.types;

import org.reflections.Reflections;
import java.util.*;

public interface SwizzledType extends IndexedType, VectorizedType {
    int getN();

    SwizzledType changeN(int n);

    static String[] typeNames() {
        List<String> list = new ArrayList<>();
        Reflections reflections = new Reflections("ast.types");
        var subTypes = reflections.getSubTypesOf(SwizzledType.class);
        for (var opClass : subTypes) {
            list.add(opClass.getSimpleName());
        }
        String[] arr = new String[list.size()];
        list.toArray(arr);
        return arr;
    }
}
