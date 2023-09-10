package com.systemvi;

import com.systemvi.examples.datastructures.LinkedList;
import com.systemvi.examples.hello.JavaTest;

public class Main {
    public static void main(String[] args) {
        LinkedList list=new LinkedList();
        list.addEnd(2);
        list.addEnd(3);
        list.addEnd(4);
        list.addEnd(5);
        list.addEnd(6);
        list.print();
    }
}
