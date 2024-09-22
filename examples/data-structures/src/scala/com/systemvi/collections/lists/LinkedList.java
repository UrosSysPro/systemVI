package com.systemvi.collections.lists;

import java.util.Iterator;

public class LinkedList<T> extends Iterable<T>{
    private class Node {
        public T value;
        public Node next;
        public Node(T value, Node next) {
            this.value = value;
            this.next = next;
        }
    };
    public class ListIterator implements Iterator<T> {
        
    }
    private Node root;

    public void addFirst(T value){
        Node node=new Node(value,root);
        root=node;
    }
    public void addLast(T value){
        root=addLastR(root,value);
//        addLastI(value);
    }
    private void addLastI(T value){
        if(root==null){
            root=new Node(value,null);
            return;
        }
        Node node;
        for(node=root;node.next!=null;node=node.next);
        node.next=new Node(value,null);
    }
    private Node addLastR(Node root,T value){
        if(root==null)return new Node(value,null);
        root.next=addLastR(root.next,value);
        return root;
    }
    public void removeFirst(){
        root=root.next;
    }
    public void removeLast(){

    }
    public void add(int value,int index){

    }
    public T get(int index){
        return getR(root,index);
    }
    private T getI(int index){
        int i;
        Node node;
        for(node=root,i=0;node!=null;node=node.next,i++)
            if(i==index)return node.value;
        return node.value;
    }
    private T getR(Node node,int index){
        if(index==0)return node.value;
        return getR(node.next,index-1);
    }
    public int size(){
        return 0;
    }
}
