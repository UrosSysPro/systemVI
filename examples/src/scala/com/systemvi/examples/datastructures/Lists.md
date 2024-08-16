# Lista
Niz je struktura podataka koja se koriti u slucajevima kada cuvamo veliki broj promenljivih istog tipa.
Prednost niza je brzina pristupa, a mana je sto je fikse velicine.

Zato se javlja potreba za strukturom podataka koja resava isti problem kao i niz ali moze dinamicno da menja velicinu.
Postoje razlicite implementacije ovakve ideje, ArrayList i LinkedList.
## ArrayList
ArrayList je klasa koja definise dva polja:<br>
`private int n;`<br>
`private int[] niz;`<br>
i minimum 4 metode:<br>
`public int  getSize();`<br>
`public void set(int i,int value);`<br>
`public int  get(int i);`<br>
`public void remove();`<br>
Ideja je da se u `niz` cuvaju uneti elementi. `n` predstavlja broj trenutno unetih elemenata u `niz`.
+ Funkcija `getSize` samo vraca n(broj elemenata u ArrayList-i a ne duzinu niza).
+ Funkcija `set` upisuje u `i`ti element `niz`a broj `value`.
+ Funkcija `get` cita i vraca `i`ti element iz `niz`a.
+ Funkcija `add` je najzanimljivija, ako `niz` nije skroz popunjen, dodaje broj `value` na prvo slobodno mesto. Ako `niz` jeste popunjen napravi se pomocni niz duplo vece velicine, prekopira se sve iz trenutnog niza, i novi niz postaje niz.

## LinkedList
Ulancana lista se sastoji od delova koje cemo zvati cvorovi (Node na engleskom). Node je klasa koja ima dva polja:<br>
`public int value;`<br>
`public Node next`<br>
Svaki cvor cuva jedan element u nizu i pokazivac na sledeci cvor u listi. Cvor je rekurzivna struktura jer u svojoj definiciji ima pokazivac na svoju klasu. Poslednji cvor u listi nema sledeci element(`next=null`).
<br><br>
Lista ima samo jedno polje `private Node root;`. Root predstavlja prvi clan liste.
Lista definise nekoliko funkcija.<br>
+ `public int getSize();` funkcija krece od `root` cvora i dokle god je `next` razlicito od `null` brojac se povecava za jedan i prelazi na sledeci node. Kada stigne do poslednjeg cvora, vraca brojac.
+ `public void addFirst(int value);` funkcija dodaje novi node na pocetak liste. Prvi korak je da se napravi novi node i da se `next` postavi na `root`, a `value` na prosledjenu vrednost, zatim se `root` postavi na `node`.
+ `public void addLast(int value);` prvo treba napraviti novi node, zatim pronaci poslednji cvor u listi i kao njegov next postaviti novi node.
+ `public void removeLast();` pronalazi pretposlednji node i next postavlja na null.
+ `public void removeFirst();` root=root.next;
Sve ove funkcije je moguce implementirati rekurzivno i iterativno, mi smo na casu radili iterativno.



