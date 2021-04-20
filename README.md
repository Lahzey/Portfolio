Link zur Hausaufgabe: https://www.dropbox.com/s/sfwee9q3jmmd6fz/Vorkurs%20Aufgabe.pdf?dl=1

Alle Desktop Applikationen können unter Releases gefunden werden. Java wird benötig (min. 1.8.0).

- [Java](#java)
  * [Bank City](#bank-city)
  * [No Contact](#no-contact)
    + [Technologie](#technologie)
    + [Assets](#assets)
  * [PoopGame](#poopgame)
    + [Meine Idee fürs Networking](#meine-idee-fürs-networking)
    + [Sonstige Entwicklung](#sonstige-entwicklung)
- [C#](#c-)
  * [Bloons TD 6 Assembly Browser](#bloons-td-6-assembly-browser)

# Java

## Bank City
Dies war ein Spiel, das ich für die Schule entwickelt habe. Ich konnte es aber nie fertig stellen und war nach der Abgabe auch nich mehr motiviert daran weiterzuschaffen.
Es ist ein City Builder (wie Cities Skylines oder SimCity), in dem man jedes Haus einzeln platziert.
Alle Personen werden einzeln simuliert (haben / suchen einen Job und wohnen in einem Haus), jedoch hatte ich keine herumlaufende Menschen oder Strassen mit Autos.

Nun habe ich das Spiel für das Portfolio wieder aufgenommen und weiter entwickelt.
Ich habe immer noch keine spazierende Meschen oder fahrende Autos, da ich nicht sehr gut mit Animationen bin und auch keinen passenden Editor habe (ich benutze Paint.NET mit einem Plugin und bearbeite jedes Frame einzeln).

Vom Gameplay her ist es sehr änlich wie existierende City Builder: Man versucht die Stadt stetig zu erweitern, ohne dass die Finanzen ins Rote gehen.

## No Contact
Da ich mich zuerst für ein Studium beworben habe, habe ich die diesjährige Hausaufgabe auch abgeschlossen:
Passend zur momentanen Situation mit Corona habe ich ein Spiel gemacht, bei dem der Spieler verhindern muss, dass Personen sich zu nahe kommen.
Es erscheinen ständig Personen auf der linken und oberen Seite und laufen auf die gegenüberliegende Seite. Der Spieler kann ihnen befehlen, dabei einen Umweg zu nehmen und muss so verhindern, dass sie sich in den Weg kommen.
Falls sich Personen zu nahe kommen, kann es sein, dass sie Infiziert werden. Je mehr infizierte, desto höher diese Chance.
Wenn die Infektionsrate 100% erreicht, ist das Spiel verloren. Das Ziel ist möglichst lange zu spielen, bevor dies passiert.
Es gibt verschiedene vorgefertigte Schwierigkeitsstufen, es können aber alle Einstellungen auch einzeln verändert werden.

Video: https://www.dropbox.com/s/9x3cukf6p7hzhop/NoContact.mp4?dl=1

### Technologie
Aus Zeitgründen habe ich für das Spiel nur Native Java verwendet. Dargestellt wird das ganze in Swing Component und gezeichnet mit AWT Graphics.
Ein Eintity Component System ist nicht vorhanden und war für so simple Game-Logik auch nicht nötig.

### Assets
Einige Assets werden während Runtime generiert oder abgeändert.
* Lauf-Animation der Personen: https://www.pinterest.co.uk/pin/682506518504852791/
* Uhr Icon: FontAwesome (https://fontawesome.com/), direkt integriert in Java durch eine Library
* Virus Icon: https://freeicons.io/viruses-icons-3/viruses-virus-icon-39038
* Digital Font: https://allfont.net/download/ds-digital-bold/


## PoopGame
Dies ist ein Humor-Spiel, welches ich ursprünglich während eines 2 Wöchigen Kurses entwickelt habe.
Das Prinzip ist simpel:
Man kämpft in einer Arena gegen einen anderen Spieler, dem man Schaden zufügen kann wenn man ihn mit dem eigenen Kot trifft.
Das spezielle hier ist, dass man den Kot nicht wirklich schiessen kann. Er übernimmt einfach die Geschwindigkeit des Spielers.
Dies macht das Spiel schwieriger und die Spezialfähigkeiten der verschiedenen Charakteren sorgen für etwas Abwechslung.

Video: https://www.dropbox.com/s/ufz6scct3r9qz67/PoopGame.mp4?dl=1

Für dieses Portfolio habe ich dieses Spiel wieder aufgenommen und umgeschrieben. Dieses Mal wollte ich online Spiele ermöglichen.
Zusätzlich wollte ich aber etwas ganz neues probieren:

### Meine Idee fürs Networking
Ich wollte, dass die verschiedenen Computer immer die genau gleiche Simulation ausführen.
Wenn eine Aktion erst später vom Server ausgeführt wird, schickt dieser die Aktion mit dem Timestamp zurück.
Der Client soll dann einen Rollback zu einem Zeitpunkt vor der Aktion machen.
Dann führt er die Aktion nochmals aus, diesmal zur gleichen Game-Zeit wie der Server, und simuliert dann vorwärts zurück in die Gegenwart.

Damit sollte ein Problem von Online Spielen gelöst werden.
Wenn sonst zum Beispiel ein Projektil wegen einer verfrühten / verspäteten Ausführung einer Aktion nicht trifft kann das Spiel einen ganz anderen lauf nehmen.
Um dies zu lösen muss der Server ständing updates zum Status der Physik-Simulation schicken. Zudem müssen wichtige Events vom Server bestätigt werden.
Packet loss macht das ganze nur noch schlimmer.

Ich erhoffte mir, dass ich schlussendlich gar keine Status Updates mehr schicken muss und die Simulationen automatisch exakt gleich sind.
In der Theorie müsste es möglich sein, weil die Physik Steps (2D) für mein Spiel weniger als eine Millisekunde brauchten.
Das Rendering System kann man ja bei einem Rollback kurz deaktivieren.

Jedoch hatte ich bis am Schluss ungenaue Simulationen und musste die Status Updates wieder aktivieren.
Das System ist an sich immer noch drin und läuft (siehe poopgame.gamelogic.engine.TimeEngine), muss aber von den Updates ergänzt werden.
Ausser den User Aktionen und dem gelegentlichen Status Update musste ich aber nichts verschicken, weil der Spielstand komplett serialisierbar war.

### Sonstige Entwicklung
An sonsten lief die Entwicklung aber relativ einfach.
Ich habe aber auch die Technologien verwendet, mit denen ich mich schon am besten auskenne:
  - Framework: LibGDX
  - Physics: Box2D
  - UI: Java Swing
    An sich nicht wieman es machen sollte (LibGDX hat sein eigenes UI, aber supported auch Swing), jedoch habe ich schon viel Erfahrung mit Java Swing und einige selbstgemachte Utilities dafür.
    Zum Beispiel kann man F12 drücken, um das Userinterface zu debuggen (wie der Inspect von Browsern).

Was mir wohl am meisten Schwierigkeiten bereitet hat waren die Animationen.
Ich habe selbst kein Programm dafür und habe schlussendlich mit paint NET und einenm Plugin Frame nach Frame manuell editiert.
Die einzelnen Frames habe ich aus Bildern vom Internet zusammengestellt, da ich selbst nich gut genug im Zeichnen bin.

Sounds hingegen waren viel simpler und sind grössten Teils selbst aufgenommen.

# C#
## Bloons TD 6 Assembly Browser
Vor nicht all zu langer Zeit habe ich erfahren, dass man für eines meiner Lieblingsspiele, Bloons TD 6, Mods schreiben kann.
Der Prozess ist aber noch sehr primitiv, da die Entwickler selbst keine Modding Tools bereitstellten.

Die Mods schreiben kann ich mit MelonLoader und C#. Dafür muss ich aber wissen, in welche Teile des Spieles ich überhaupt eingreifen will.
Zuerst versuchte ich, das Spiel zu dekompilieren. Da es aber als Maschinen Code kompiliert ist kam ich da nirgens hin.
Also habe ich stattdessen einen Mod geschrieben, der mithilfe von Reflection Informationen über das Assembly ausliest und in ein JSON File schreibt.
Für dieses JSON File habe dann hier ein UI geschrieben: https://wdg65.csb.app/ (CodeSandbox)
