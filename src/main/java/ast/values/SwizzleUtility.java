package ast.values;

import ast.exceptions.*;

import java.util.*;

public class SwizzleUtility {
    static private Map<Character, Integer> xyzw;
    static private Map<Character, Integer> rgba;
    static private Map<Character, Integer> stpq;

    static {
        xyzw = new TreeMap<>() {{
            put('x', 0);
            put('y', 1);
            put('z', 2);
            put('w', 3);
        }};
        rgba = new TreeMap<>() {{
            put('r', 0);
            put('g', 1);
            put('b', 2);
            put('a', 3);
        }};
        stpq = new TreeMap<>() {{
            put('s', 0);
            put('t', 1);
            put('p', 2);
            put('q', 3);
        }};
    }

    static private Integer getSetID(char c) {
        if (xyzw.containsKey(c)) return 0;
        if (rgba.containsKey(c)) return 1;
        if (stpq.containsKey(c)) return 2;
        return null;
    }

    static private Map<Character, Integer> getMap(int setID) {
        if (setID == 0) return xyzw;
        if (setID == 1) return rgba;
        if (setID == 2) return stpq;
        return null;
    }

    static public int[] swizzle(int n, String name) throws InvalidSelectionException {
        if (name.length() > n) throw InvalidSelectionException.illegalVectorFieldSelection(name);
        int[] res = new int[name.length()];
        for (int i = 0; i < name.length(); i++) {
            try {
                res[i] = getSetID(name.charAt(i));
            } catch (NullPointerException exception) {
                throw InvalidSelectionException.illegalVectorFieldSelection(name);
            }
            if (i >= 1 && res[i] != res[0])
                throw InvalidSelectionException.notSameSet(name);
        }
        for (int i = 0; i < name.length(); i++) {
            res[i] = getMap(res[i]).get(name.charAt(i));
            if (res[i] >= n) throw InvalidSelectionException.outOfRange(name);
        }
        return res;
    }

    static public boolean isDuplicate(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            for (int j = i + 1; j < indices.length; j++)
                if (indices[i] == indices[j]) return true;
        }
        return false;
    }
}
