 
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

## Git
Git je program koji se instalira lokalno na kompjuter, i koristi se za upravljanje git repozitorijuma.
Repozitorijum je cool ime za folder u kome se nalazi projekat. Da bi jedan folder postao git repository potrebno je da otvorimo taj folder u terminalu i pokrenemo komandu `git init`. Git radi dve bitne stvari, cuva svaku izmenu koju je bilo ko napravio u bilo kom fajlu, i dozvoljava nam da pravimo paralelne verzije projekta.
Za pocetak koristicemo samo tri git naredbe.<br>
`git clone`<br>
`git add .`<br>
`git commit -m "komentar o tome sta je zimenjeno"`<br>
Git zapravo ne cuva svaku izmenu, vec cuva nesto sto se zove git commit. Commit je trenutno stanje projekta. Svaki commit ima svoj id, datum i komentar, id i datum se postavljaju sami, mi postavljamo komentar. Kada zalimo da pogledamo kako je projekat izgledao ranije, mozemo da pogledamo listu svih commit-ova i po komentaru zakljucimo koji nas zanima. `git add .` je komanda koja dodaje sve izmene iz trenutnog foldera u commit. `git commit -m"komentar"` kreira novi commit, commit je kao checkpoint u igrici, uvek mozemo da se vratimo i pogledamo ovu verziju projekta.<br>

### Github
Github je drustvena mreza na kojoj programeri, timovi i kompanije, objavljuju source kod svojih projekata. Vazna razlika je da je git program koji se instalira a github drustvena mreza. Jedan od nacina da vise ljudi radi na istom projektu je koristeci github. Glavni projekat se zove `upstream`. Svako ko zeli da doda kod u projekat moze to da ucini tako sto:
1. Napravi nalog na github-u
1. Napravi kopiju projekta na svom nalogu, ovo se naviva fork (to fork a project)
1. Koristeci naredbu `git clone` preuzme lokalnu kopiju projekta.
1. Napravi svoj branch(svoju paralelnu verziju projekta)
Kompletan tutorijal [how to make pull request](https://opensource.com/article/19/7/create-pull-request-github)
