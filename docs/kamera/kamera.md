# Kamera
Lako mozemo da ukucamo koordinate random tacaka u 3d prostoru i napravimo 3d model.
Problem nastaje kada zelimo taj model da predstavimo na ekranu jer je ekran 2d.
Potrebno je da napravimo neku funkciju koja projektuje 3d tacke u prostoru u 2d piksele na ekranu.
Na slicnom principu rade kamere u stvarnom zivotu, zato se i u programiranju ova funkcija zove kamera.
Znamo da je u opengl prozor portal u virtuelni svet, i prozoru vidimo samo mali deo tog sveta,
kocku cije koordinate idu od -1 do 1 u svim smerovima. 
<br><br>
U programiranju kamera je matrica koja koordinate iz 3d virtuelnog sveta premesta u tu kocku.
Kada u realnom svetu zelimo da snimimo spomenik, spomenik stoji u mestu i mi pomeramo kameru.
U programiranju kocka od -1 do 1 ne moze da se pomeri, tako da ne pomeramo kameru, vec pomeramo 3d svet oko kamere.
Znaci u mesto da pomerimo kameru u levo, sve predmete u svetu pomerimo u desno, ako hocemo da kamera pogleda 90 stepeni u desno,
ceo svet rotiramo za 90 stepeni u levo.

Za pocetak napravicemo kameru koja ima sve sto nam je potrebno za 2d igrice.
Zelimo da kamera pretvori koordinate u rasponu od 0 do 800 po x i od 0 do 600 po y osi u -1 do 1 po x i 1 do -1 po y.
To mozemo da postignemo tako sto redom: transliramo sve tacke tako da budu od -400 do 400, skaliramo sa 1/400 tako da postanu -1 do 1, i na kraju dodamo rotaciju ako je potrebno.
Za ovo koristimo 3 matrice
```java
private Matrix4f translate,rotate,scale;
public Camera(){
    translate=new Matrix4f().identity();
    rotate=new Matrix4f().identity();
    scale=new Matrix4f().identity();
}
```
Takodje mozemo da dodamo jednu matricu koja kombinuje ove 3.
```java
private Matrix4f view;
public Camera(){
    view=new Matrix4f().identity();    
}
public void update(){
    view.identity().mul(translate).mul(rotate).mul(scale);    
}
```
Prvo postavimo sve matrice, zatim pozovemo update da spoji sve matrice u jednu, koju saljemo na graficku.
Takodje sam dodao jos jednu matricu koja sluzi samo za skaliranje za velicinu ekrana.

## Projekcija

Ove matrice iznad resavaju problem za x i y osu, potrebna nam je jos jedna matrica da sredi z osu,
i da odredi nacin na koji se tacke projektuju.
Postoji vise tipova projekcija, nas zanimaju ortogonalna i perspektivna. U stvarnom svetu 
ortogonalna projekcija se koristi za skiciranje modela na papiru(za arhitekturu na primer),
linije koje su paralelne u stvarnosti su paralelne i na crtezu kod ove projekcije. Sa slike ne mozemo da vidimo da li je jedan objekat blizi ili dalji,
jer velicina ne zavisi od pozicije u svetu.
Perspektivna projekcija imitira nacin na koji radi oko. Kod ove projekcije postoji jedna tacka u beskonacnosti,
i sve paralelne linije se sastaju u toj tacki. Postoji perspektivna projekcija sa jednom, dve i tri tacke.
Opengl nam daje funkcije `ortho(float near,float far)` i `perspective(float fov,float near,float far)`.
U klasi kamera je dodata jos jedna matrica `projection`. i moze da se postavi da bude ili perspektivna ili ortogonalna.
Na kraju mozemo obe matrice `view` i `projection` da prosledimo shader-u ili da ih spojimo u jednu `combined` i tu posaljemo.
