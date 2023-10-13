## Instance Rendering
Elements rendering nam dozvoljava da navedemo zajednicke tacke samo jednom.
Instance rendering nam daje mogucnost da zajednicke delove modela navedemo samo jednom.
Ako zelimo da nacrtamo `n` kvadrata na razlicitim mestima to mozemo da uradimo sa:
```java
int vertexSize=2;
int points=4;
float[] data=new float[n*vertexSize*points];
int[] indices=new int[3*2*n];
mesh=new Mesh(new VertexAttribute("position",2));
shader=new Shader("vertex.glsl","fragment.glsl");
//loop
for(int k=0;k<n;k++){
    for(int j=0;j<2;j++){
        for(int i=0;i<2;i++){
            int index=vertexSize*points*k+vertexSize*(j*2+i);
            data[index+0]=x1;
            data[index+1]=y1;
        }
    }
}
for(int i=0;i<n;i++){
    int index=i*6;
    int rect=i*points;
    indices[index+0]=rect+0;
    indices[index+1]=rect+1;
    indices[index+2]=rect+2;
    indices[index+3]=rect+1;
    indices[index+4]=rect+2;
    indices[index+5]=rect+3;
}
shader.use();
mesh.setVertexData(data);
mesh.setIncides(indices);
mesh.draw();
```

Prva for petlja postavlja po 4 tacke za n cetvorouglova, druga postavlja po 6 indeksa (dva trougla) za svaki 
cetvorougao.
Svi cetvorouglovi imaju isti odnos temena, i isti raspored tacaka, razlikuju se samo po poziciji.
Ovde mozemo da upotrebimo instance rendering da navedemo samo podatke za jedan cetvorougao, jedne indekse za trouglove,
i jedan niz u kome se nalaze pozicije svih cetvorouglova.
```java
//setup
int[] indices=new int[]{
    0,1,2,
    1,2,3,
};
float[] data=new float[]{
    x1,y1,    
    x2,y2,    
    x3,y3,    
    x4,y4,    
};
float[] offsets=new float[]{
    100, 200,    
    300, 400,    
    -100, 200,    
    300, 800,    
    500, 100,    
};
shader=new Shader();
mesh=new Mesh();
mesh.setVertexData(data);
mesh.setIndices(indices);
mesh.setInstanceData(offsets);
//loop
shader.use();
mesh.draw();
```
Vidimo da se dosta prostora ustedi za indekse. Takodje za svaki cetvorougao navodimo samo
poziciju a ne sve 4 tacke. Mnogo je jednostavniji kod.