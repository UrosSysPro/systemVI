# Binary tree

## Graf
U matematici postoje dva pojma sa istim imenom, graf.
Jedno znacenje reci graf, dvodimenzioni prostor u kome su obelezene tacke tipa (x,f(x)).
Drugo znacenje nas sad zanima.<br>

Graf je skup cvorova koji su povezani granama. Cvorovi se obicno crtaju kao tacke,
a grane kao linije koje povezuju tacke.
Ovakvi grafovi imaju veliku primenu u programiranju, mnogo sistema moze da se modeluje na ovaj nacin.
Primer: na mapi gradovi mogu da se smatraju kao cvorovi, a autoputevi kao grane koje povezuju cvorove (gradove).
Na drustvenim mrezama korisnicki nalog predstavlja cvor,
a prijateljstva predstavljaju grane.
Postoje razlicite klase grafova, put, ciklicni, kompletni, komponentni, stabla...
Prosle nedelje smo pisali implementaciju `LinkedList`-e, ova struktura podataka moze da se gleda kao put.
Ovaj vikend nas zanimaju stabla, specificno binarna stabla.
Binarno stablo je tip stabla u kome svaki cvor ima maksimalno dva child cvora, i maksimalno jedan parent cvor.


## Node class

Stablo se sastoji od cvorova (Node na engleskom), za pocetak moramo da definisemo pomocnu klasu Node.
Klasa `Node` definise tri polja:
```
    public int value;
    public Node left,right;
```
`value` predstavlja vrednost koja se cuva u tom cvoru, a `left` i `right`
pokazivace na levo i desno podstablo. `left` i `right` su grane.

## Tree class
Klasa `Tree` definise jedno polje `Node root`, i tri metode:
```
    public void add(int value);
    public boolean contains(int value);
    public void print();
```
Sve metode mogu da se definisu na dva nacina, iterativno i rekurzivno.
`add` metoda dodaje novi element u stablo, rekurzivno se definise kao:
1. ako root ne postoji vrati `new Node(value)`.
2. ako root postoji proveri da li je `value > root.value`
   + ako jeste onda je `root.right=addR(root.right,value)`
   + ako nije onda je `root.left=addR(root.left,value)`
3. `return root;`

`contains` metoda vraca `true` ili `false` u zavisnosti da li se element nalazi u stablu.
Rekurzivni algoritam glasi:
1. Proverimo da li root postoji. ako ne `return false` jer smo stigli do kraja stabla i nismo nasli element
2. Ako je `root.value==value` onda `return true` jer smo pronasli element
3. Proveravamo da li se element nalazi u levom ili desnom pod stablu
   + ako je `value > root.value` onda `return contains(root.right,value)`
   + u suprotnom `return contains(root.left,value)`

Kao i za ostale metode krecemo sa pretpostavkom da je tesko ispisati celo stablo od jednom.
Kada pogledamo strukturu stabla, vidimo da je svaki element koren nekog podstabla.
Ovo mozemo da upotrebimo i definisemo funkciju kao 
1. rekurzivno ispisi levi deo stabla
2. ispisi trenutni element
3. rekurzivno ispisi desni deo stabla

Ovakva funkcija moze zauvek da se izvrsava, tako da moramo da stavimo neki uslov izlaza, a to je
da se za prazno stablo ne ispisuje nista.
