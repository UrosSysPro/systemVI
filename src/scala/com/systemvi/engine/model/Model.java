package com.systemvi.engine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class Model {
    public static class Vertex{
        public Vector3f position,normal,tangent,bitangent;
        public ArrayList<Vector3f> texCoords;
        public ArrayList<Vector4f> colors;
        public Vertex(Vector3f position, Vector3f normal, Vector3f tangent, Vector3f bitangent, ArrayList<Vector3f> texCoords, ArrayList<Vector4f> colors){
            this.position = position;
            this.normal = normal;
            this.tangent = tangent;
            this.bitangent = bitangent;
            this.texCoords = texCoords;
            this.colors = colors;
        }
    }
    public static class Mesh{
        public ArrayList<Vertex> vertices;
        public int materialIndex;
        public Material material;
        public String name;
        public Mesh(String name,ArrayList<Vertex> vertices,Material material, int materialIndex) {
            this.vertices = vertices;
            this.material = material;
            this.materialIndex = materialIndex;
            this.name = name;
        }
    }
    public static class Material{
        public Vector4f ambient,diffuse,specular;
        public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular){
            this.ambient = ambient;
            this.diffuse = diffuse;
            this.specular = specular;
        }
    }
    public static class Light{

    }
    public static class Node{
        public String name;
        public ArrayList<Node> children;
        public ArrayList<Integer> meshIndices;
        public ArrayList<Mesh> meshes;
        public Matrix4f transform;
        public Node(String name, ArrayList<Node> children, ArrayList<Integer> meshIndices, ArrayList<Mesh> meshes,Matrix4f transform){
            this.name = name;
            this.children = children;
            this.meshIndices = meshIndices;
            this.meshes = meshes;
            this.transform = transform;
        }
    }

    public ArrayList<Mesh> meshes;
    public ArrayList<Material> materials;
    public Node root;

    public Model(ArrayList<Mesh> meshes,ArrayList<Material> materials,Node root) {
        this.meshes = meshes;
        this.materials = materials;
        this.root=root;
    }
}
