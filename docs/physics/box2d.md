## Box2d
Box2d je najpoznatija open source biblioteka za simulaciju 2d fizike. Vecina 2d igrica koristi ovu biblioteku,
Apple SpriteKit (apple official game engine) isto koristi box2d.
<br>
U box2d postoje tri glavne klase: `World`,`Body` i `Joint`.
### World
`World` je beskonacni 2d svet u kome se simulira fizika. Ova klasa cuva informaciju o gravitaciji u svetu,
preciznost i brzinu simulacije, i event listener koji se poziva svaki put kada se dva objekta sudare.

### Body
`Body` predstavlja materijalnu takcu. Body opisuju pozicija, brzina, ubrzanje, ugao, ugaona brzina, ugaono ubrzanje i tip objekta.
Postoje 3 tipa body-a, to su dynamic, static i kinematic. Dynamic su sve stvari koje mogu da se sudaraju i na koje deluje gravitacija,
na primer igrac, metkovi, objekti koje igrac moze da unisti ili pomera... Static objekti su pod i zidovi, oni nikad ne mogu da se 
pomeraju i na njih ne deluje gravitacija, imaju beskonacnu masu i stvari mogu samo da se odbjaju o njih. Kinematic je isto sto i static
ali mogu da se pomeraju po nekoj fiksnoj putanji, kinematic se koristi za pokretne zidove i pokretne platforme.
Na body moze da se doda fixture. Fixture je 2d oblik, koji ima listu tacaka, trenje, gustinu i koeficijent eleasticnost.

### Joint 
Joint je zglob, ili bilo kakva veza koja povezuje dva objekta. Postoje razliciti tipovi zglobova.
Mogu da se koriste za pravljenje kompleksnih objekata, most, auto, lanac...

### Primer
Za pocetak moramo da napravimo jedan 2d svet u kome gravitacija deluje na dole sa ubrzanjem od 10 metara po sekundi na kvadrat.
```java
World world=new World(new Vec2(10,0));
```
Zatim napravimo jedan body i dodamo ga u simulaciju.
U box2d objekti se prave pomocu pomocnih klasa `BodyDef` i `FixtureDef`.
```java
BodyDef bodyDef=new BodyDef();
bodyDef.position.set(x,y); 
bodyDef.type= BodyType.DYNAMIC; 

FixtureDef fixtureDef=new FixtureDef(); 
fixtureDef.density=1; 
fixtureDef.restitution=0.5f; 
fixtureDef.friction=0.7f; 

PolygonShape shape=new PolygonShape();
shape.setAsBox(20f,20f);

fixtureDef.shape=shape;

Body body=world.createBody(bodyDef); 
body.createFixture(fixtureDef);
```

Na isti nacin moze da se doda jos objekata, samo treba da se promeni pozicija i oblik.

Na kraju 60 puta po sekundi pozivamo funkciju `world.step(delta,10,10)`.
`delta` je vreme koje je proslo od prethodnog frejma, druga dva parametra su preciznost simulacije.
ako se objekti krecu previse brzo treba povecati prvi broj, ako se svaki 
objekat sudara sa dosta drugih objekata treba povecati drugi broj.
