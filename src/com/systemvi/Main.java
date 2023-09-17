package com.systemvi;

import com.systemvi.examples.datastructures.LinkedList;
import com.systemvi.examples.datastructures.Tree;
import com.systemvi.examples.hello.JavaTest;

public class Main {
    public static void main(String[] args) {
        Tree tree=new Tree();
        tree.add(5);
        tree.add(6);
        tree.add(4);
        tree.add(7);
        tree.add(2);
        tree.add(1);
        tree.print();
    }
}
