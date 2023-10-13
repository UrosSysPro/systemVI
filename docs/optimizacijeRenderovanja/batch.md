## Magistrala

Do sada smo rekli da je graficka ceo kompjuter u kompjuteru. Svaka zasebna komponenta racunara
je brza, (procesor, graficka, ram...). Sve komponente komuniciraju medjusobno preko magistrale.
Problem sa magistralom je brzina, brz procesor ne sluzi nicemu ako nema podatke da racuna, 
brza memorija isto ako nema sta da upisuje. Zato je cilj da smanjimo komunikaciju, izmedju komponenti.
Postoje nacini na koji se to postize, to su batch, elements in instance rendering.

## Batch Rendering

Jedan trougao mozemo da nacrtamo sa:
```java
//setup
mesh=new Mesh(new VertexAttribute("position",2));
mesh.setVertexData(new float[]{
    -0.5, -0.5,
     0.5, -0.5,
     0.0,  0.5
});
shader=new Shader("vertex.glsl","fragment.glsl");
//loop
shader.use();
mesh.draw();
```

Vise trouglova mozemo da nacrtamo tako sto promenimo podatke u modelu:
```java
//setup
mesh=new Mesh(new VertexAttribute("position",2));
shader=new Shader("vertex.glsl","fragment.glsl");
//loop
shader.use();
for(int i=0;i<n;i++){
    mesh.setVertexData(new float[]{
        x1, y1,
        x2, y2,
        x3, y3
    });
    mesh.draw(); 
}
```
`mesh.setVertexData` i `mesh.draw` su dve funkcije koje komuniciraju sa grafickom.
U ovom primeru pozivaju se `n` puta.
Ovo mozemo da optimizujemo tako sto napravimo jedan veliki model (mesh), u kome se nalaze 
svi trouglovi koje zelimo da nacrtamo u jednom frejmu.
```java
//setup
mesh=new Mesh(new VertexAttribute("position",2));
shader=new Shader("vertex.glsl","fragment.glsl");
float[] data=new float[n*2];
//loop
for(int i=0;i<n;i++){
    data[i*2+0]=x1;
    data[i*2+1]=y1;
}
shader.use();
mesh.setVertexData(data);
mesh.draw();
```
U ovom primeru isto crtamo `n` trouglova, ali se `setVertexData` i `draw` poziva samo jednom.
U praksi ovo drasticno ubrzava program, jer mozemo da napravimo samo par stotina poziva `setVertexData`,
a sa jednim `draw` pozivom mozemo da nacrtamo od stotine hiljada do par miliona trouglova.
