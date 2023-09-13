 
# Struktura Projekta

U prjektu se koriste programski jezici java i scala,<br>
kao i biblioteke lwjgl (light weight java graphics libary), lanterna (cool terminal graphics) i tako dalje.
## Gradle
Gradle je program koji ima dve bitne uloge.
### Package Manager
U proslosti jedini nacin da dodamo neku biblioteku u projekat je
da trazimo .jar fajlove na razlicitim sajtovima.<br> Problem je sto taj proces traje dugo, pitanje je da li smo pronasli pravu biblioteku ili pravu verziju... Zbog toga je izmisljen package manager.<br> U projektu u jednom fajlu samo nabrojimo sve biblioteke koje koristimo i ovaj program ce ih preuzeti sa interneta automatski. Biblioteke nabrajamo u `build.gradle` fajlu, u dependencies bloku.<br>
`dependencies{`<br>
`   implementation group: 'com.googlecode.lanterna', name: 'lanterna', version: '3.0.1'
`<br>
`}`
### Build System
Kompajliranje je proces u kome se java source code prevodi u java bytecode. Kada instaliramo java development kit u terminalu se pojave dve komande: java i javac(java je komanda koja pokrece java program, javac je komanda za kompajliranje).
Program koji se sastoji samo od jednog source fajla se moze kompajlirati i pokrenuti sa <br>
`javac Main.java`<br>
`java Main`<br>
Problem nastaje u slucajevima kada u projektu imamo vise klasa. Svaku klasu moramo da navedemo kompajleru, da navedemo u kojoj klasi je `main` funkcija...
Zato je napravljen build system. U `build.gradle` fajlu samo napisemo u kom folderu se nalaze source fajlovi, i da je nas projekat aplikacija a ne biblioteka.
