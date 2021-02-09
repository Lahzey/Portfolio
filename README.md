# Java
## PoopGame
Dies ist ein Spiel das ich ursprünglich während eines 2 Wöchigen Kurses entwickelt habe.
Das Prinzip ist simpel:
Man kämpft in einer Arena gegen einen anderen Spieler, dem man Schaden zufügen kann wenn man ihn mit dem eigenen Kot trifft.
Das spezielle hier ist, dass man den Kot nicht wirklich schiessen kann. Er übernimmt einfach die Geschwindigkeit des Spielers.
Dies macht das Spiel schwieriger und die Spezialfähigkeiten der verschiedenen Charakteren sorgen für etwas Abwechslung.

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
