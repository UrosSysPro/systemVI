# Generic class

Kao vezbu smo implementirali klase iz java standardne biblioteke.
Bilo bi super kada bi ove klase koristili dalje tokom skolske godine,
ali postoji jedan problem. Za `ArrayList`,`LinkedList`,`Tree`,`Stack`,`Queue`
najzanimljivija stvar je nacin na koji se objekti organizuju i skladiste u memoriju.
Kljucna stvar je da nije bitno koji tip objekta se cuva.
Zato ka da koristimo ove klase u deklaraciji navedemo koji tip podataka cuvaju.
Nasa implementacija uvek cuva `int`. Hajde ovo da ispravimo.

```java
public class ArrayList<T>{
    //code
}
```
Svakoj klasi u definiciji mozemo da dodamo neki nepoznati tip `T` pored imena.
Ovakva klasa se zove generic class. `T` moze da se zameni bilo kojom reci, samo je bitno da u 
programu ne postoji klasa sa istim imenom.<br>
Sada na svakom mestu gde koristimo `int` stavimo `T`.
```java
    public void add(T value);
    public T get(int i);
    public void set(T value,int i);
```
Naravno indeks i ostaje `int` jer sa njime pristupamo nizu.
Nastaje jedan problem jer ne mozemo da napravimo niz klase `T`.
Zato menjamo
```java
    private int[] niz;
```
u
```java
    private Object[] niz;
```
U javi `Object` je nadklasa svih klasa, posto ne znamo tip `T`
`Object` je jedina zagarantovana nadklasa.
U drugim klasama nemamo ovaj problem jer ne pravimo niz,
samo zaneminmo svako `int` sa `T`

# Design pattern

Design pattern je generalna ideja za neko resenje za probleme sa kojima se programeri cesto susrecu.
Sa nekim paternima smo se vec upoznali, firebase biblioteka koristi singleton pattern.
`Firestore.instance` je jedina instancla klase `Firestore`. Event listener u java script-u su isto design pattern.
Css osobine koriste observer, ili signal patern, kada u js promenimo css osobinu ui se automatski promeni.
Postoje paterni koji resavaju probleme kreiranja objekata, cuvanja objekata, i ponesanje.
Observer i signal, event listener su za ponasanje.
Builder i factory su za kreiranja objekata.<br>
Na casu smo napravili klasu `Sendvic` koja ima mnogo boolean atributa.
Svaki atribut je po default-u false. Mozemo da napravimo konsturktor koji prima 10 argumenata, ili konstruktor koji ne prima argumente
i postavlja atribute na false, pa posle rucno da promenimo one koje treba.
U ovom slucaju mozemo da upotrebimo [builder](Sendvic.java). 



