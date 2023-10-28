# systemVI
Projekat za 6.godinu 2023-2024 u terminu 13:30

## Casovi
1. [Git, Gradle](GitGradle.md)
2. [Data structures: ArrayList, LinkedList](src/com/systemvi/examples/datastructures/Lists.md)
3. [Binarno stablo](src/com/systemvi/examples/datastructures/Tree.md)
4. [Generic class, Builder pattern](src/com/systemvi/examples/datastructures/GenericBuilder.md)
   + [Bonus terminal graphics](docs/Lanterna.md)
5. [GLFW window, uvod u opengl](docs/openglwindow/window.md)
6. [Crtanje trougla](docs/openglwindow/triangle.md)
7. [Linearna Algebra, Vektori i Matrice](docs/vektori/vektori.md)
8. [Kamera](docs/kamera/kamera.md)
   + [shader in/out/uniform](docs/shader/shader.md)
9. [Batch rendering](docs/optimizacijeRenderovanja/batch.md)
10. [Elements ](docs/optimizacijeRenderovanja/elements.md)i [Instance rendering](docs/optimizacijeRenderovanja/instance.md)
11. [Breakout igrica](docs/breakout.md)
12. [Texture](docs/textures/texture.md)
13. [TextureRenderer](docs/textures/textureRenderer.md)
14. [Box2d](docs/physics/box2d.md)
## Domaci
1. + Napraviti `Stack` klasu. `Stack` je struktura podataka koja definise dve operacije 
push i pop. `push` dodaje element na vrh steka, `pop` vraca i brise element sa vrha. Obe operacije
se izvrsavaju konstantnom brzinom O(1). implementirati slicno kao `LinkedList`-u
   +  Napraviti `Queue` klasu. `Queue` je struktura podataka koja definise dve operacije
   add i remove. `add` dodaje element na vrh reda, `remove` vraca i brise element sa kraja reda. Obe operacije
   se izvrsavaju konstantnom brzinom O(1). implementirati slicno kao `LinkedList`-u
1. + Napraviti klasu `PravougliTrougao`. Klasa definise samo dva atributa `public float a,b;`
   i jednu metodu 
   ```java 
   public float izracunajHipotenuzu(){
      return (float)Math.sqrt(a*a+b*b);
   }
   ```
   + Napisati test program koji napravi `PravougliTrougao[] p=new PravougliTrougao[100];`i popuni ga trouglovima random stranica.
   + Napraviti novi niz `float[] niz=new float[300]` i popuniti taj niz tako da za svaka 3 uzastopna clana niza budu a, b, hipotenuza tog trougla.
## Korisni linkovi
+ [https://learnopengl.com/](https://learnopengl.com/)
+ [Git pull request](https://opensource.com/article/19/7/create-pull-request-github)
## Screenshots
+ Coming soon

## Requirements
Od softvera za ovu godinu potrebni su nam:
+ [git](https://git-scm.com/)
+ [IntelliJ](https://www.jetbrains.com/idea/download) (Skinuti Community edition, pri dnu stranice je link)

