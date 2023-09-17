package com.systemvi.examples.datastructures;

import java.util.Stack;

public class Tree {
    private class Node{
        public int value;
        public Node left,right;
        public Node(int value,Node left,Node right){
            this.value=value;
            this.left=left;
            this.right=right;
        }
        public Node(int value){
            this(value,null,null);
        }
    }

    private Node root;

    public Tree(){
        root=null;
    }

    public void add(int value){
        Node node=root;
        if(root==null){
            root=new Node(value);
            return;
        }
        while (true){
            if(value>node.value){
                if(node.right!=null){
                    node=node.right;
                }else{
                    node.right=new Node(value);
                    break;
                }
            }else{
                if(node.left!=null){
                    node=node.left;
                }else{
                    node.left=new Node(value);
                    break;
                }
            }
        }
    }
    public boolean contains(int value){
        return false;
    }
    public void print(){
        printI();
    }
    private void printR(Node root){
        if(root==null)return;

        printR(root.left);
        System.out.println(root.value);
        printR(root.right);
    }
    private void printI(){
        Stack<Node> stack=new Stack<>();
        stack.push(root);
        while(!stack.isEmpty()){
            Node node=stack.pop();
            if(node==null)continue;
            System.out.println(node.value);
            stack.push(node.left);
            stack.push(node.right);
        }
    }
}
