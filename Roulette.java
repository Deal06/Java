import java.util.Random;
import java.util.Scanner;

// Hauptklasse für das Roulette-Spiel
public class Roulette {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Scanner für Benutzereingaben

        boolean nochmalSpielen = true; // Steuert, ob das Spiel erneut gestartet wird
        while (nochmalSpielen) {
            Random random = new Random(); // Zufallszahlengenerator für die Kugelposition

            // Spieleranzahl abfragen (1-6)
            int players = spielerAnzahlAbfragen(scanner);

            // Spielernamen abfragen und speichern
            String[] namen = spielerNamenAbfragen(scanner, players);

            // Arrays für Gewinne, Status (lebt/tot) und Schussanzahl je Spieler
            int[] gewinne = new int[players];
            boolean[] aktiv = new boolean[players];
            int[] schuesse = new int[players];
            // Initialisierung: Alle Spieler starten mit 0 Gewinn, sind aktiv und haben 0 Schüsse
            for (int i = 0; i < players; i++)  {
                gewinne[i] = 0;
                aktiv[i] = true;
                schuesse[i] = 0;
            }

            int bulletPosition = random.nextInt(6); // Zufällige Kugelposition (0-5)
            int currentPosition = 0; // Startposition der Trommel
            boolean weiter = true; // Steuert, ob die aktuelle Spielrunde weiterläuft
            int aktuellerSpieler = 0; // Index des aktuellen Spielers
            boolean regulärGewonnen = false; // Gibt an, ob das Spiel regulär gewonnen wurde

            // Hauptspielschleife (läuft, solange weiter == true)
            while (weiter) {
                // Zähle, wie viele Spieler noch leben
                int lebende = 0;
                for (boolean a : aktiv) if (a) lebende++;

                // Wenn nur noch einer lebt (bei mehreren Spielern), ist das Spiel vorbei
                if (players > 1 && lebende <= 1) {
                    System.out.println("Das Spiel ist vorbei! Es lebt nur noch einer.");
                    regulärGewonnen = true;
                    break;
                }

                // Suche den nächsten aktiven Spieler
                int startSpieler = aktuellerSpieler;
                while (!aktiv[aktuellerSpieler]) {
                    aktuellerSpieler = (aktuellerSpieler + 1) % players;
                    // Wenn kein aktiver Spieler mehr gefunden wird, Spiel beenden
                    if (aktuellerSpieler == startSpieler) {
                        weiter = false;
                        break;
                    }
                }
                if (!weiter) break;

                boolean zugGemacht = false; // Steuert, ob der aktuelle Spieler seinen Zug gemacht hat
                while (!zugGemacht) {
                    // Menü für den aktuellen Spieler anzeigen
                    System.out.println("Am Zug: " + namen[aktuellerSpieler]);
                    System.out.println("1) Auf mich selber schiessen");
                    if (lebende > 1) {
                        System.out.println("2) Auf einen anderen Spieler schiessen (Einsatz 500'000.-)");
                        System.out.println("3) Spiel beenden");
                        System.out.println("4) Kontostand anzeigen");
                    } else {
                        System.out.println("2) Spiel beenden");
                        System.out.println("3) Kontostand anzeigen");
                    }

                    // Menüauswahl mit Fehlerprüfung
                    int wahl = -1;
                    while (true) {
                        System.out.print("Deine Auswahl: ");
                        if (!scanner.hasNextInt()) {
                            System.out.println("Bitte gib eine Zahl ein!");
                            scanner.nextLine();
                            continue;
                        }
                        wahl = scanner.nextInt();
                        scanner.nextLine();
                        break;
                    }

                    // Aktionen je nach Auswahl
                    switch (wahl) {
                        case 1:
                            // Spieler schießt auf sich selbst
                            schuesse[aktuellerSpieler]++;
                            int nummer = aktuellerSpieler;
                            if (currentPosition == bulletPosition) {
                                // Spieler verliert (tot)
                                System.out.println("BAMM! " + namen[nummer] + " hat verloren");
                                // ASCII-Art für den "Tod"
                                System.out.println("       ______");
                                System.out.println("    .-'      '-.");
                                System.out.println("   /            \\");
                                System.out.println("  |              |");
                                System.out.println("  |,  .-.  .-.  ,|");
                                System.out.println("  | )(_o/  \\o_)( |");
                                System.out.println("  |/     /\\     \\|");
                                System.out.println("  (_     ^^     _)");
                                System.out.println("   \\__|IIIIII|__/");
                                System.out.println("    | \\IIIIII/ |");
                                System.out.println("    \\          /");
                                System.out.println("     `--------`");
                                aktiv[nummer] = false; // Spieler ist tot
                                bulletPosition = random.nextInt(6); // Neue Kugelposition
                                weiter = false; // Runde beenden
                                zugGemacht = true;
                                break;
                            } else {
                                // Spieler überlebt, bekommt 500.000
                                gewinne[nummer] += 500_000;
                                System.out.println("Glück gehabt, " + namen[nummer] + "! Zu deinem Gewinn werden 500000.- addiert. Du hast jetzt "
                                        + gewinne[nummer] + ".-");
                            }
                            currentPosition = (currentPosition + 1) % 6; // Trommel weiterdrehen
                            zugGemacht = true;
                            break;

                        case 2:
                            // Spieler schießt auf anderen (nur wenn mehr als einer lebt)
                            if (lebende <= 1) {
                                weiter = false;
                                zugGemacht = true;
                                break;
                            }
                            int schiesser = aktuellerSpieler;
                            if (gewinne[schiesser] < 500_000) {
                                System.out.println(namen[schiesser] + " hat nicht genug Geld, um zu schiessen! (mind. 500000 nötig)");
                                break;
                            }
                            String zielName = "";
                            while (true) {
                                System.out.println("Auf wen möchtest du schiessen? (Name eingeben)");
                                zielName = scanner.nextLine().trim();
                                if (zielName.isEmpty()) {
                                    System.out.println("Bitte gib einen Namen ein!");
                                    continue;
                                }
                                break;
                            }
                            int ziel = -1;
                            // Zielspieler suchen und prüfen, ob er aktiv ist und nicht man selbst
                            for (int i = 0; i < players; i++) {
                                if (namen[i].equalsIgnoreCase(zielName) && aktiv[i] && i != aktuellerSpieler) {
                                    ziel = i;
                                    break;
                                }
                            }
                            if (ziel == -1) {
                                System.out.println("Diesen Namen gibt es nicht, der Spieler ist schon tot oder du kannst nicht auf dich selbst schießen!");
                                break;
                            }
                            schuesse[aktuellerSpieler]++;
                            gewinne[schiesser] -= 500_000; // Schuss kostet 500.000
                            System.out.println(namen[schiesser] + " zahlt 500000 für den Schuss. Neuer Kontostand: " + gewinne[schiesser] + ".-");

                            if (currentPosition == bulletPosition) {
                                // Zielspieler verliert (tot)
                                System.out.println(namen[ziel] + " hat verloren! " + namen[schiesser] + " bekommt 500000.-");
                                // ASCII-Art für den "Tod"
                                System.out.println("       ______");
                                System.out.println("    .-'      '-.");
                                System.out.println("   /            \\");
                                System.out.println("  |              |");
                                System.out.println("  |,  .-.  .-.  ,|");
                                System.out.println("  | )(_o/  \\o_)( |");
                                System.out.println("  |/     /\\     \\|");
                                System.out.println("  (_     ^^     _)");
                                System.out.println("   \\__|IIIIII|__/");
                                System.out.println("    | \\IIIIII/ |");
                                System.out.println("    \\          /");
                                System.out.println("     `--------`");
                                gewinne[schiesser] += 500_000; // Schütze bekommt 500.000
                                aktiv[ziel] = false; // Ziel ist tot
                                bulletPosition = random.nextInt(6); // Neue Kugelposition
                                System.out.println("Dein Gewinn ist jetzt "
                                        + gewinne[schiesser] + ".-");
                            } else {
                                // Ziel überlebt, bekommt 500.000
                                gewinne[ziel] += 500_000;
                                System.out.println(namen[ziel] + " hat überlebt! Er bekommt 500000.- und sein Gewinn beträgt jetzt "
                                        + gewinne[ziel] + ".-");
                            }
                            currentPosition = (currentPosition + 1) % 6; // Trommel weiterdrehen
                            zugGemacht = true;
                            break;

                        case 3:
                            // Spiel beenden oder Kontostand anzeigen (je nach Spielerzahl)
                            if (lebende > 1) {
                                weiter = false;
                                zugGemacht = true;
                                regulärGewonnen = false; // Kein regulärer Sieg
                                break;
                            } else {
                                // Kontostand anzeigen (bei nur einem Spieler)
                                String name = "";
                                while (true) {
                                    System.out.println("Bitte gib deinen Namen ein:");
                                    name = scanner.nextLine().trim();
                                    if (name.isEmpty()) {
                                        System.out.println("Bitte gib einen Namen ein!");
                                        continue;
                                    }
                                    break;
                                }
                                int index = -1;     //Index des gesuchten Spielers
                                // Suche den Spieler anhand des Namens
                                for (int i = 0; i < players; i++) {
                                    if (namen[i].equalsIgnoreCase(name)) {
                                        index = i;          //Spieler gefunden
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    System.out.println(namen[index] + ", dein Kontostand beträgt: " + gewinne[index] + ".-");
                                } else {
                                    System.out.println("Name nicht gefunden!");
                                }
                                break;     //verlässt den switch-case
                            }

                        case 4:
                            // Kontostand anzeigen (bei mehreren Spielern)
                            String name = "";
                            while (true) {
                                System.out.println("Bitte gib deinen Namen ein:");
                                name = scanner.nextLine().trim();
                                if (name.isEmpty()) {
                                    System.out.println("Bitte gib einen Namen ein!");
                                    continue;
                                }
                                break;
                            }
                            int index = -1;
                            for (int i = 0; i < players; i++) {
                                if (namen[i].equalsIgnoreCase(name)) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1) {
                                System.out.println(namen[index] + ", dein Kontostand beträgt: " + gewinne[index] + ".-");
                            } else {
                                System.out.println("Name nicht gefunden!");
                            }
                            break;

                        default:
                            System.out.println("Ungültige Auswahl!");
                    }
                }

                // Nächster Spieler ist an der Reihe
                aktuellerSpieler = (aktuellerSpieler + 1) % players;
            }

            // --- Endausgabe: Gewinne und Schüsse aller Spieler ---
            System.out.println("Spiel beendet. Gewinne und Schüsse:");
            for (int i = 0; i < players; i++) {
                System.out.println(namen[i] + ": " + gewinne[i] + ".- " + (aktiv[i] ? "(lebt)" : "(tot)") +
                        ", Schüsse: " + schuesse[i]);
            }

            // Pokal für Gewinner (bei mehreren Spielern, nur bei regulärem Gewinn)
            if (players > 1 && regulärGewonnen) {
                int gewinner = -1;
                for (int i = 0; i < players; i++) {
                    if (aktiv[i]) {
                        gewinner = i;
                        break;
                    }
                }
                if (gewinner != -1) {
                    System.out.println();
                    System.out.println("Herzlichen Glückwunsch, " + namen[gewinner] + "! Du bist der Gewinner!");
                    // ASCII-Art Pokal
                    System.out.println("     .-=========-.");
                    System.out.println("     \\'-=======-'/");
                    System.out.println("     _|   .=.   |_");
                    System.out.println("    ((|  {{1}}  |))");
                    System.out.println("     \\|   /|\\   |/");
                    System.out.println("      \\__ '`' __/");
                    System.out.println("        _`) (`_");
                    System.out.println("      _/_______\\_");
                    System.out.println("     /___________\\");
                }
            }

            // --- Abfrage, ob nochmal gespielt werden soll ---
            String antwort = "";
            while (true) {
                System.out.println("\nMöchtest du nochmal spielen? (j/n)");
                antwort = scanner.nextLine().trim().toLowerCase();
                if (antwort.equals("j") || antwort.equals("ja")) {
                    nochmalSpielen = true;
                    break;
                } else if (antwort.equals("n") || antwort.equals("nein")) {
                    nochmalSpielen = false;
                    System.out.println("Danke fürs Spielen!");
                    break;
                } else {
                    System.out.println("Bitte gib nur 'j' für ja oder 'n' für nein ein!");
                }
            }
        }
        scanner.close(); // Scanner schließen
    }

    // Methode 1: Fragt die Spieleranzahl ab (1-6)
    public static int spielerAnzahlAbfragen(Scanner scanner) {
        int players = 0;
        while (players < 1 || players > 6) { 
            System.out.println("Willkommen zum Roulette-Spiel!");
            System.out.println("Wieiviele Spieler seid ihr? (max. 6)");
            if (!scanner.hasNextInt()) {
                System.out.println("Bitte gib eine Zahl ein!");
                scanner.nextLine();
                continue;
            }
            players = scanner.nextInt();
            scanner.nextLine();
            if (players < 1 || players > 6) {
                System.out.println("Es muss mindestens ein Spieler sein und maximal 6.");
            }
        }
        return players;
    }

    // Methode 2: Fragt die Namen aller Spieler ab
    public static String[] spielerNamenAbfragen(Scanner scanner, int players) {
        String[] namen = new String[players];
        for (int i = 0; i < players; i++) {
            while (true) {
                System.out.print("Name von Spieler " + (i + 1) + ": ");
                namen[i] = scanner.nextLine().trim();
                if (namen[i].isEmpty()) {
                    System.out.println("Bitte gib einen Namen ein!");
                } else {
                    break;
                }
            }
        }
        return namen;
    }
}