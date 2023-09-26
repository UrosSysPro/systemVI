## Trougao
Prosli cas smo napravili prozor i event listener-e koji reaguju na mis i tastaturu.
Sada cemo videti kako da nesto nacrtamo unutar tog prozora.
OpenGL koristi posebne termine za mnoge pojmove u programiranju.
<br>
+ Texture - tekstura je slika koja se nalazi u vram-u na grafickoj. U opengl-u postoje 1d,2d, i 3d teksture.
+ Vertex -  verteks je tacka u 3d prostoru koja moze da ima dodatne osobine kao sto su na primer boja.
+ Vertex Attribute - svaka osobina koja opisuje vertex, boja i pozicija, se naziva atribut.
+ Buffer - buffer je niz tipa float. 3d model moze da se smatra kao niz verteksa(tacaka).
Svaki verteks moze da se opise sa 3 float-a za x y z poziciju, 3 float-a za r g b boju i tako dalje.
Znaci 3d model se na grafickoj cuva kao jednodimenzioni niz tipa float. Tekstura je dvodimenzioni niz piksela.
+ Shader - sejder je program koji se izvrsava na grafickoj. Ovi programi se pisu u programskom jeziku glsl (graphics library shading language)
koji je dosta slican c programskom jeziku.
Nemaju nikakve veze sa senkama, shader je samo program. Postoji vise vrsta shader programa, vertex, geometry, tesselation, fragment i compute.
Na pocetku, a i verovatno do kraja godine, cemo koristiti samo vertex i fragment.

Prozor je portal u 3d svet. U prozoru vidimo samo jedan mali deo tog sveta, tacnije vidimo samo unutrasnjost kocke.
Granice ove kocke su od -1 do 1 po svim osama (x,y,z). U opengl-u kordinatni sistem pocinje od sredine prozora, tu se nalazi kordinata 0,0,0.
X i y osa funkcionisu kao u matematici, kada idemo na gore y se povecava, kada idemo desno x se povecava.
Z osa prolazi kroz ekran. Ako zamislimo da neka tacka izadje iz ekrana i krece se ka nama, onda se povecava z osa, u suprotnom
ako zamislimo da tacka prodje kroz ekran i ode iza smanjuje se z osa.
<br><br>
U prozoru vidimo samo objekte koji su nacrtani unutar ove kocke, sve sto je van se nece pojaviti na ekranu.
Opengl moze da crta samo trouglove. Trougao je odabran zbog nekih osobina kao sto su koplanarnost(sve tacke trougla su uvek u istoj ravni),
stranice trougla ne mogu da se seku, trougao je uvek konveksan i tako dalje. Cetvorougao moze da se nacrta koristeci 2 trougla, petougao koristeci 3 trougla...
Svaki 3d model je skup tacaka koje cine trouglove.
<br><br>

Kako bi nacrtali trougao moramo prvo da napravimo 3d model:
```java 
float[] vertices = {
    -0.5f, -0.5f, 0.0f,
    0.5f, -0.5f, 0.0f,
    0.0f,  0.5f, 0.0f
};
```
3 float-a u svakom redu predstavljaju jedan vertex (poziciju jedne tacke).
Sledeci korak je da ovaj model prenesemo iz rama u vram na grafickoj.
```java
int vertexBuffer=glGenBuffers();
glBindBuffer(GL_ARRAY_BUFFER,vertexBuffer);
glBufferData(GL_ARRAY_BUFFER,vertices,GL_STATIC_DRAW);
glVertexAttribPointer(0, 3, GL_FLOAT, false, 3*4 , 0);
glEnableVertexAttribArray(0);
```
Kao sto smo rekli buffer je samo niz, isto kao i kod prozora kada napravimo buffer dobijamo njegov id koji posle prosledjujemo u druge funkcije.
Moramo da naglasimo da se svaki vertex sastoji od jednog atributa (pozicije), i da taj atribut ima 3 float vrednosti
<br><br>
Sada treba da napisemo program koji ce da nacrta ovaj model.
Potrebno je da napisemo dva programa vertex i fragment shader.
[Vertex](../../assets/openglwindow/vertex.glsl) shader je program koji se izvrsava jedanput za svai vertex u modelu(u nasem slucaju 3 puta, po jednom za svaku tacku).
Zanimljivo je da se programi za svaku tacku izvrsavaju paralelno.
Vertex shader ima `void main();` funkciju koja ne prima parametre i ne vraca nista.
Jedini cilj ovog programa je da postavi vrednost za globalnu promenljivu `glPosition`.

Nakon ovoga graficka zna konacnu poziciju svake tacke, sada se odredjuju tacke ovih trouglova koje se nalaze u kocki sa pocekta.
Program koji odredjuje koji pikseli su unutar kocke se zove raster. 

Zatim se za svaki pronadjen piksel poziva [fragment](../../assets/openglwindow/fragment.glsl) shader.
Isto kao i vertex, fragment ima main funkciju i jedini cilj ovog programa je da postavi globalnu progmenljivu `FragColor`, 
ovo je konacna rgb vrednost piksela na ekranu.

Prvo je potrebno ucitati source code shader-a u `Stiring` u javi. Shaderi se kompajliraju u trenutku kada se program pokrece, ne kada i java.
```java
int vertexShader=glCreateShader(GL_VERTEX_SHADER);
glShaderSource(vertexShader,vertexSource);
glCompileShader(vertexShader);

int fragmentShader=glCreateShader(GL_FRAGMENT_SHADER);
glShaderSource(fragmentShader,fragmentSource);
glCompileShader(fragmentShader);

int shaderProgram=glCreateProgram();
glAttachShader(shaderProgram,vertexShader);
glAttachShader(shaderProgram,fragmentShader);
glLinkProgram(shaderProgram);

glDeleteShader(vertexShader);
glDeleteShader(fragmentShader);
glUseProgram(shaderProgram);
```
Sada kad se na grafickoj nalaze i 3d model, i program koji ce da ga nacrta, samo treba da pozovemo par funkcija u game loop-u.

```java
while(!glfwWindowShouldClose(window)){
    glfwPollEvents();
    glClearColor(r,g,b,1.0f);
    glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
    
    glUseProgram(shaderProgram);
    glBindVertexArray(attributeBuffer);
    glDrawArrays(GL_TRIANGLES,0,3);
    
    glfwSwapBuffers(window);
    sleep(16);
}
```

+ `glBindVertexArray` selektuje 3d model kao sledeci za crtanje.
+ `glUseProgram` selektuje shader koji ce nacrtati model.
+ `glDrawArrays` crta model, mozemo da biramo da li da nacrta cele trouglove, ili samo tacke, kao i od kog do kog trougla da crta, ne mora da se nacrta ceo model.

