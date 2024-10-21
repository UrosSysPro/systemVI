package com.systemvi.java;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GenericType<T, V> {
    public T value1;
    public V value2;

    public GenericType(T value1, V value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public static void main(String[] args) {
        GenericType<String, Integer> first = new GenericType<>("first", 1);
        GenericType<Float, Float> second = new GenericType<>(3.14f, 2f);
        
        List<Integer> list = new ArrayList<>();
        List<Float> list2 = new LinkedList<>();
    }
}
