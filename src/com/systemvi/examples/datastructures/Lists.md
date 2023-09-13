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




