# 3D vs 2D model

OpenGL ne pravi nikakvu razliku izmedju 3d (kocka, sfera, piramida...) i 2d (trougao, kvadrat...) modela.
I 2d i 3d modeli se sastoje od trouglova koji se sastoje od tacaka, openGL ne zanima koji podaci se cuvaju u tackama.
Najveca razlika je u tome sto kod 3d grafike moramo da mislimo o nekim pojavama koje nisu bitne za 2d, kao sto su osvetljenje,
senke, tipovi materijala... Sto se tice renderovanja, isto kao i kod 2d, `Mesh` posalje informacije o 3d modelu u vram na grafickoj
`vertex shader` pomocu kamere odredi konacnu poziciju tacke na ekranu, `raster` pronadje sve tacke koje se nalaze unutar trougla,
`fragment shader` se izvrsava za svaki piksel i postavlja konacnu boju piksela.

## Formati za cuvanje 3D modela

### OBJ
Obj (skraceno od wave front format??? iz nekog razloga) je najjednostavniji format u kome se cuvaju 3d modeli.
```obj
o cube
v 0.5 0.5 0.5
v 0.5 0.5 -0.5
v 0.5 -0.5 0.5
v 0.5 -0.5 -0.5
v -0.5 0.5 -0.5
v -0.5 0.5 0.5
v -0.5 -0.5 -0.5
v -0.5 -0.5 0.5
vt 0 1
vt 0.25 1
vt 0.25 0.75
vt 0 0.75
vt 0 0.75
vt 0.25 0.75
vt 0.25 0.5
vt 0 0.5
vt 0.25 1
vt 0.5 1
vt 0.5 0.75
vt 0.25 0.75
vt 0.25 0.75
vt 0.5 0.75
vt 0.5 0.5
vt 0.25 0.5
vt 0.25 0.25
vt 0 0.25
vt 0 0.5
vt 0.25 0.5
vt 0.75 1
vt 0.5 1
vt 0.5 0.75
vt 0.75 0.75
vn 0 0 -1
vn 1 0 0
vn 0 0 1
vn -1 0 0
vn 0 1 0
vn 0 -1 0
f 4/4/1 7/3/1 5/2/1 2/1/1
f 3/8/2 4/7/2 2/6/2 1/5/2
f 8/12/3 3/11/3 1/10/3 6/9/3
f 7/16/4 8/15/4 6/14/4 5/13/4
f 6/20/5 1/19/5 2/18/5 5/17/5
f 7/24/6 4/23/6 3/22/6 8/21/6
```
Ovo je primer jednog obj fajla. Vidimo da je ovo samo txt fajl sa jednostavnom sintaksom. Svaki red pocinje sa nekim `keyword`.
+ `o` keyword oznacava pocetak jednog 3d modela, format moze da cuva vise modela unutar jednog fajla.
+ `v` keyword definise poziciju jednog verteksa, to je 3d vektor.
+ `vt` keyword je kordinata teksture u toj tacki. Ovaj parametar u kodu cesto nazivamo `texCoord` ili `uv`
+ `vn` keyword definise vektor koji je normalan na jednu stranicu, ovaj vektor je koristan kada racunamo koliko je jedan trougao osvetljen

Ovaj fajl cuva 3d model kocke, i vidimo da ima 8 redova koji pociju sa `v` jer kocka ima 8 temena, ima 6 `vn` jer kocka ima 6 stranica,
 i 24 `vt` jer je 6\*4=24 (6 stranica i 4 temena po stranici)
+ `f` keyword povezuje sve ove informacije u trouglove, ima 6 `f` naredbi koje za svaku stranicu kocke definisu po dva trougla.

Primecujemo da smo kod 2d modela mogli da definisemo bilo kakve atribute, kod 3d modela je to isto moguce ali je vise standardizovano, 
skoro svi formati podrzavaju `position`, `texCoords`, `normal`.
### GLTF
GLTF je najmocniji open source format. Postoje dve varijante gltf i glb. `.gltf` fajlovi cuvaju 3d model kao `json`, a glb
kompresuje te iste infromacije u neki binarni format, kako bi se smanjio prostor, cime se model brze ucitava u igrici.
GLTF podrzava iste osnovne atribute kao OBJ i mnogo vise, kao sto su `roughness`, `metalic`, `opacity`, animation bones...


U zavisnosti od tipa igrice treba koristiti odgovarajuci format, za igrice kao sto je minecraft, ili neke racing igrice, sasvim je dovoljan obj,
jer 3d modeli nisu mnogo animirani, a za igrice koje imaju animirane karaktere mora gltf.
