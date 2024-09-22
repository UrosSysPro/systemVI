package com.systemvi.collections.lists;

public class LinkedList {
    private class Node {
        public int value;
        public Node next;
        public Node(int value, Node next) {
            this.value = value;
            this.next = next;
        }
    };
    private Node root;

    public void addFirst(int value){
        Node node=new Node(value,root);
        root=node;
    }
    public void addLast(int value){
        root=addLastR(root,value);
        addLastI(value);
    }
    private void addLastI(int value){
        if(root==null){
            root=new Node(value,null);
            return;
        }
        Node node;
        for(node=root;node.next!=null;node=node.next);
        node.next=new Node(value,null);
    }
    private Node addLastR(Node root,int value){
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
    public int get(int index){
        return getR(root,index);
    }
    private int getI(int index){
        int i;
        Node node;
        for(node=root,i=0;node!=null;node=node.next,i++) if(i==index)return node.value;
    }
    private int getR(Node node,int index){
        if(index==0)return node.value;
        return getR(node.next,index-1);
    }
    public int size(){
        return 0;
    }
}
