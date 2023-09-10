package com.systemvi.examples.datastructures;

public class LinkedList {
    private class Node{
        public int value;
        public Node next;
        public Node(int value){
            this(value,null);
        }
        public Node(int value,Node next){
            this.value=value;
            this.next=next;
        }
    }
    private Node root;
    public LinkedList(){
        root=null;
    }

    public int getSize(){
        int n=0;
        for(Node node=root;node!=null;node=node.next){
            n++;
        }
        return n;
    }
    public void addStart(int value){
        Node node=new Node(value);
        node.next=root;
        root=node;
    }
    public void addEnd(int value){
        Node node = new Node(value);
        if(root==null){
            root=node;
            return;
        }
        Node last=root;
        while(last.next!=null)last=last.next;
        last.next=node;
    }
    public void removeStart(){
        root=root.next;
    }
    public void removeEnd(){
        if(root==null){
            return;
        }
        if(root.next==null){
            root=null;
            return;
        }
        Node beforeLast=root;
        while(beforeLast.next.next!=null)beforeLast=beforeLast.next;
        beforeLast.next=null;
    }
    public void print(){
        for(Node node=root;node!=null;node=node.next){
            System.out.println(node.value);
        }
    }
}
