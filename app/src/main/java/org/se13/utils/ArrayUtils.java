package org.se13.utils;

import java.util.Arrays;
import java.util.stream.Stream;

public class ArrayUtils {

    public static Stream<Object> flatten(Object[] array) {
        return Arrays.stream(array).flatMap(o -> o instanceof Object[] a ? flatten(a) : Stream.of(o));
    }
}
