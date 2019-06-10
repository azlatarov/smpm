package com.smpm.functional;

import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

public class Predicates {
    public static <T> Predicate<T> isNull() {
        return Objects::isNull;
    }
    
    public static DoublePredicate isPositive() {
    	return (d) -> d > 0;
    }
    
    public static Predicate<String> isEmpty() {
        return s -> s.isEmpty();
    }
}
