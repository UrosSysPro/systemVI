na## TextureRenderer
Na grafickoj mozemo da ucitamo onoliko slika koliko moze da stane u vram. Ali postoji ogranicenje koliko slika mozemo
da koristimo u jednom trenutku za crtanje. Zato se gomila slika spaja u sprite sheet. Sada zelimo da napisemo klasu
kojoj mozemo da posaljemo kameru i teksturu i kazemo nacrtaj mi samo jednu slicicu iz sprite sheet-a.
### TextureRegion
Ova klasa nam cuva pokazivac na teksturu, i poziciju jednog sprite-a unutar sprite-sheet-a.
```java 
 private Texture texture;
    public int x,y, width, height;
    
    public TextureRegion(Texture texture,int x,int y,int width,int height)
    
    public float getTop()
    public float getBottom()
    public float getLeft()
    public float getRight()

    public Texture getTexture()
    public static TextureRegion[][] split(Texture texture, int tileWidth, int tileHeight)
```
TextureRegion cuva poziciju i veliciju jednog sprite-a. Ove velicine su u pikselima.
Kada citamo teksturu iz shader-a potrebne su nam koordinate od 0 do 1, funkcije getTop, getBottom, getLeft i getRight
vracaju racunaju te koordinate.

`TextureRenderer` je veoma slican `ShapeRenderer` klasi, samo u mesto da mesh cuva poziciju i boju,
cuva poziciju i koordinatu piksela unutar teksture, i metode primaju `TextureRegion` i poziciju i veliciju sprajta na ekranu.

