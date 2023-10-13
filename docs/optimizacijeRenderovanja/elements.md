## Elements Rendering
Mnogougao crtamo pomocu vise trouglova, na primer cetvorougao se crta pomocu dva trougla.
```java
mesh.setVertexData(new float[]{
    //prvi trougao
      0, 0,
    100, 0,
      0, 100,
    //drugi trougao
    100, 0,
      0, 100,
    100, 100
});
```
Vidimo da navodimo 6 tacaka u mesto 4, zajednicke tacke za trouglove se navode dva puta.
OpenGL ima mogucnost da u jednom nizu navedemo sve tacke, a u drugom nizu trouglove(skup od 3 tacke).
```java
mesh.setVertexData(new float[]{
    0, 0,
    100, 0,
    0, 100,
    100, 100
});
mesh.setIndices(new int[]{
    0,1,2,
    1,2,3
});
```
U ovom primeru navedemo sve posebne tacke cetvorougla, i zatim niz u kom svaka 3 clana oznacavaju indekse 
tacaka koje cine taj trougao. Nulta, prva i druga tacka cine jedan, a prva, druga i treca cine drugi.
U ovom primeru element rendering zauzima vise mesta od batch renderinga, ali uglavnom vertex-i imaju vise atributa
osim pozicije.
```java
mesh.setVertexData(new float[]{
    //pozicija  //color      //random atribut
      0, 0,     1, 0, 1, 1   100, 200, 300, 400, 500
    100, 0,     1, 1, 0, 1   100, 200, 300, 400, 500
      0, 100,   0, 1, 1, 1   100, 200, 300, 400, 500
    100, 100    1, 0, 1, 1   100, 200, 300, 400, 500
});
mesh.setIndices(new int[]{
    0,1,2,
    1,2,3
});
```
U ovom slucaju 3 indeksa zauzimaju znacajno manje mesta od dva vertex-a.