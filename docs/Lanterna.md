# Terminal 
Terminal je aplikacija koja ima jedan zadatak, da prikazuje output stream nekog programa.
Output stream je sve sto ispisuju funkcje kao sto su System.out.println(), console.log(), cout, Console.writeln(), 
ekvivalentne funkcije u razlicitim programskim jezicima.
Terminali su ranije koristili samo ascii karaktere i ispisivali ih jedan za drugim.
U poslednjih 30 ubaceni su posebni karakteri koji se zovu ansi kodovi.
To su nevidljivi karakteri koji menjaju osobine karaktera koji se ispisuju nakon njih.
Ovi karakteri mogu da promene poziciju kursora i boju teksta.
## Zasto bi neko danas koristio terminal?
1. Server: Serveri uglavnom nemaju graficki interfejs, nemaju cak ni povezan monitor.
    Kada zelimo da nesto proverimo ulogujemo se sa drugog kompjutera u terminal na serveru.
1. Automatizacija: Kod grafickih aplikacija input su mis i tastatura a output su pikseli na ekranu.
   U terminalu su input i output uvek tekst, i na ovaj nacin mozemo da povezemo izlaz jednog programa sa ulazom drugog.
1. Debagovanje: Kada program ne radi ono sto ocekujemo najcesci nacin za trazenje greske je da ispisujemo neke medjurezultate i
    tako vidimo gde nastaje greska. 
## Lanterna
U teoriji mi mozemo da koristimo sve mogucnosti terminala samo sa `System.out.println()`.
Problem je sto su ansi kodovi bas zbunjujuci i losa je dokumentacija na internetu.
Ova biblioteka nam daje gotove funkcije kao sto su `screen.setCharacter(character,color,position)` koje urade sav posao za
nas i mi samo treba da se fokusiramo na podatke koje ispisujemo i kako ce izgledati.
U example paketu se nalazi terminalgraphics paket u kome je jednostavan primer kako se koristi biblioteka.
[Test](../src/com/systemvi/examples/terminalgraphics/Test.java)<br>
