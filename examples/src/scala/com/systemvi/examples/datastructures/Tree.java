package com.systemvi.examples.datastructures;

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
//        addI(value);
        root=addR(root,value);
    }
    private Node addR(Node root,int value){
        if(root==null)return new Node(value);

        if(value>root.value)
            root.right=addR(root.right,value);
        else
            root.left=addR(root.left,value);
        return root;
    }
    private void addI(int value){
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
        return containsR(root,value);
    }
    private boolean containsR(Node root,int value){
        if(root==null)return false;
        if(root.value==value)return true;
        if(value>root.value)
            return containsR(root.right,value);
        else
            return containsR(root.left,value);
    }
    public void print(){
        printR(root);
    }
    private void printR(Node root){
        if(root==null)return;

        printR(root.left);
        System.out.println(root.value);
        printR(root.right);
    }
    private void printI(){
        LinkedList<Node> list=new LinkedList<>();
        list.addStart(root);
        while(list.getSize()!=0){
            Node node=list.get(0);
            list.removeStart();
            if(node==null)continue;
            System.out.println(node.value);
            list.addStart(node.left);
            list.addStart(node.right);
        }
    }
}
