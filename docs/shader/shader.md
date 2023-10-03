# Shader
Shader je program koji se izvrsava na grafickoj i napisan je u glsl jeziku.
Postoji vise tipova shadera, mi smo koristili vertex i fragment na casovima.
Svaki shader ima jednu `void main()` funkciju. 
<br><br>
Cilj shader-a je da dobije neke podatke, obradi ih i posalje u sledeci korak.
Vertex shader prima atribute jednog vertex-a u 3d modelu, i salje u sledeci korak `gl_Position`, takodje mozemo da definisemo i ostale 
vrednosti koje se prenose.
<br><br>
Fragment shader dobija vrednosti iz vertex shadera, i na osnovu njih racuna konacnu boju piksela.
<br><br>
Svi podaci izmedju ovih koraka mogu da se salju na dva nacina. Koristeci `in`/`out` promenljive, ili `uniform` promenljive.
`in` `out` promenljive mogu da budu razlicite za svaki vertex, i postave se u jednom shaderu kao out, i u sledecem kao in.
```glsl
//Vertex shader
out vec4 vColor;

void main(){
    vColor=aColor;
}
```
i 
```glsl
//Fragment shader
in vec4 vColor;

void main(){
    FragColor=vColor;
}
```

Za atribute iz 3d modela osim `in` keyword mora da se naglasi koji je to atribut po redu u jednom vertexu,
do sada smo koristili modele koji imaju prvi atribut `vec3 aPosition` i drugi `vec3 aColor`.
Ona u vertex shaderu pisemo:
```glsl
layout(location=0) in vec3 aPosition;
layout(location=1) in vec4 aColor;
```
Atributima se uglavnom dodaje `a` ispred imena, a promenljivama koje se salju iz vertexa u fragment `v`, kao u primeru iznad.
<br><br>
`uniform` promenljive se postavljaju iz jave, i iste su za svaki vertex i svaki piksel na ekranu.
Uglavnom se koriste za stvari kao sto su matrica kamere, pozicije izvora svetlosti, teksture, i tako dalje.
U javi se postavjlaju sa dve opengl funkcije:
```java
int uniformID=glGetUniformLocation(program,"camera");
glUniform4f(uniformID,false,new float[]{...});
```

Ove funkcije su dodate u klasu `Shader`, tako da sada samo pozovemo:
```java
shader.setUniform("view",camera.getView());
shader.setUniform("projection",camera.getProjection());
```
