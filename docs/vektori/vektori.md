# Linearna Algebra

## Vektor
Vektor je jedan abstraktan pojam u matematici.
Matematicka definicija vektora je klasa relacije ekvivalencije nad skupom
dekartovog proizvoda R na n. Klasa, relacija, ekvivalencija, dekartov proizvod su fensi reci,
ali sa ovom definicijom ne mozemo nista da uradimo.
Bitna stvar je da matematicari, fizicari i programeri razlicito razmisljaju o vektorima,
ali definicije iz fizike i iz programiranja su podskupovi matematicke definicije.
<br><br>
Vektor je usmerena duz, definise ga pravac, intenzitet i smer.
Pravac je prava linija na kojoj lezi duz, prava linija koja sadrzi obe tacke duzi.
Svaki pravac ima dva smera, od prve do druge tacke, i od druge do prve.
Intenzitet je duzina duzi, razdaljina od prve do druge tacke.
U programiranju vektor bi mogli da definisemo kao klasu koja cuva koordinate dve tacke.
Ovo mozemo da skratimo tako sto predpostavimo sledece, za svaki vektor postoji beskonacno mnogo ekvivalentnih vektora
(onih koji imaju paralelan pravac, isti smer i intenzitet), znaci da za svaki vektor postoji jedan kome je prva tacka 
koordinatni pocetak ((0,0) koordinata). Sa ovom pretpostavkom mozemo da cuvamo samo drugu tacku jer se podrazumeva da je druga (0,0).
Pozicija tacke se cuva kao niz tipa float, svaki clan niza predstavlja koordinatu u jednoj dimenziji.
<br><br>
Vektore cemo koristiti za cuvanje pozicije i za kretanje(brzina i ubrzanje).
Vektori mogu da se sabiraju: (a,b)+(c,d)=(a+c,b+d).
Mogu da se mnoze koeficijentom (realnim brojem): a\*(x,y)=(a\*x,a\*y)

## Matrice
Do sada smo napisali program koji na ekranu crta trougao. Kada pogledamo neku igricu, na primer minecraft,
vidimo da se cela igrica sastoji od kocki, koje su napravljene od kvadrata, koji su napravljeni od dva trougla.
Bitna stvar sa ovim trouglovima je da su svi na razlicitim mestima, razlicite rotacije i velicine.
Zato moramo da nadjemo nacin da brojcano opisemo ove osobine: translacija, rotacija i skaliranje.
Za to se koriste matrice.
<br><br>
Hajde da vidimo kako se pomocu matrica mogu opisati rotacija i skaliranje. Svaki vektor moze da se predstavi kao zbir baznih vektora.
Bazni vektori se uglavnom oznacavaju sa e<sub>1</sub>, e<sub>2</sub>, e<sub>3</sub> ... Skup baznih vektora se zove baza vektorskog prostora.
E={e<sub>1</sub>,e<sub>2</sub>,e<sub>3</sub>,...}. Broj ovih vektora odredjuje dimenziju prostora.
<br>
Vektor (x,y) moze da se zapise kao x \* e<sub>1</sub>+y \* e<sub>2</sub> gde su e<sub>1</sub>=(1,0) i e<sub>2</sub>=(0,1).
Ako zelim da rotiram tacku (x,y) oko koordinatnog pocetka dovoljno je da zamenim e<sub>1</sub> i e<sub>2</sub> sa novom bazom f<sub>1</sub> i f<sub>2</sub>.

Neka su f<sub>1</sub>=(a,b) i f<sub>2</sub>=(c,d). Nova pozicija vektora je x*(a,b)+y*(c,d)=(ax,bx)+(cy,dy)=(ax+cy,bx+dy).
Ako f1 i f2 postavimo kao kolone matrice, i vektor (x,y) kao matricu velicine 1*2 dobijamo:
```
|       |   |   |   |           |
| a   c |   | x |   | a*x + b*y |
|       | x |   | = |           |
| b   d |   | y |   | c*x + d*y |
|       |   |   |   |           |
```
Vidimo da se kao rezultat mnozenja dobija matrica dimenziija 1 * 2, i vidimo da se resenje poklapa za ovim iznad.
<br><br>
Transliranje moze da se opise jednim vektorom, ako tacku (x,y) zelimo da pomerimo za (a,b) onda je nova pozicija jednaka
(x,y)+(a,b)=(x+a,y+b). Bilo bi super kada bi i transliranje moglo da se predstavi mnozenjem matrica. Ovo je moguce ako dodamo jednu dimenziju na vektor i matricu.
```
|         |   |   |   |             |   |     |
| 1  0  a |   | x |   | 1*x+0*y+a*1 |   | x+a |
| 0  1  b | X | y | = | 0*x+1*y+b*1 | = | y+b |
| 0  0  1 |   | 1 |   | 0*x+0*y+1*1 |   |  1  |
|         |   |   |   |             |   |     |
```
Ako zelimo neku tacku da transliramo, rotiramo i skaliramo za svaku operaciju definisemo matricu M<sub>t</sub>, M<sub>r</sub>,
M<sub>s</sub>. Konacna pozicija se dobija sa: M<sub>s</sub>\*M<sub>r</sub>\*M<sub>t</sub>\*(x,y,1). Vidimo da se operacije pisu sa desna na levo.
Krecemo od pocetne tacke (x,y,1), zatim se translira pa rotira pa skalira. Mnozenje matrica nije komutativno (M<sub>1</sub>*M<sub>2</sub> != M<sub>2</sub>*M<sub>1</sub>),
ali jeste asocijativno ((M<sub>1</sub>\*M<sub>2</sub>)\*M<sub>3</sub>==M<sub>1</sub>\*(M<sub>2</sub>\*M<sub>3</sub>)).
Ovo mozemo da upotrebimo i spojimo gomilu transformacija u jednu veliku matricu, i samo tu matricu posaljemo na graficku.


