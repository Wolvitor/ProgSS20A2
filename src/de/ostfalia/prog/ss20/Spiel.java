package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;
import de.ostfalia.prog.ss20.figuren.Schlumpf;

import java.util.InputMismatchException;
import java.util.Scanner;

import static de.ostfalia.prog.ss20.enums.Farbe.GELB;
import static de.ostfalia.prog.ss20.enums.Farbe.BLAU;
import static de.ostfalia.prog.ss20.enums.Farbe.ROT;
import static de.ostfalia.prog.ss20.enums.Farbe.GRUEN;

/**
 * Controller
 */
public class Spiel {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
       ingame(setupGame());
    }

    public static ZombieSchluempfe setupGame(){
        ZombieSchluempfe zombieSchluempfe = new ZombieSchluempfe(GELB);

        System.out.println("Wie viele Spieler soll Ihr Spiel haben?\n" +
                "Die Spielerfarben werden in folgender Reihenfolge hinzugefügt:\n" + "GELB -> ROT -> BLAU -> GRUEN");
        int spielerAnzahl = 0;

        boolean valideAnzahl = false;
        while(!valideAnzahl) {
            try {
                spielerAnzahl = scanner.nextInt();
                if (spielerAnzahl < 1 || spielerAnzahl > 4) {
                    throw new IllegalArgumentException();
                }
                valideAnzahl = true;
            } catch (InputMismatchException e) {
                System.err.println("FALSCHE EINGABE. DIE EINGABE MUSS EINE GANZE ZAHL SEIN.");
            } catch (IllegalArgumentException i) {
                System.err.println("DIE EINGABE MUSS SICH ZWISCHEN INKLUSIVE 1 UND 4 BEFINDEN.");
            }
        }

        System.out.println("Möchtest du eine bestimmte Spielsituation herstellen? (Y/N)");
        String input = scanner.next();
        while(!input.toUpperCase().contentEquals("Y") && !input.toUpperCase().contentEquals("N")){
            System.err.println("UNGÜLTIGE EINGABE! NUR 'Y' UND 'N' erlaubt!");
            input = scanner.next();
        }
        if(input.toUpperCase().contentEquals("N")){
            switch(spielerAnzahl){
                case 1:
                    zombieSchluempfe = new ZombieSchluempfe(GELB);
                    break;
                case 2:
                    zombieSchluempfe = new ZombieSchluempfe(GELB, ROT);
                    break;
                case 3:
                    zombieSchluempfe = new ZombieSchluempfe(GELB, ROT, BLAU);
                    break;
                case 4:
                    zombieSchluempfe = new ZombieSchluempfe(GELB, ROT, BLAU, GRUEN);
                    break;
                default:
                    System.err.println("ERROR 001: spielerAnzahl out of bounds");
                    break;
            }
        } else{
            System.out.println("Gib nun bitte den Konfigurations-String zur Spielerstellung an:");
            String config = scanner.next();

            try {
                switch (spielerAnzahl) {
                    case 1:
                        zombieSchluempfe = new ZombieSchluempfe(config, GELB);
                        break;
                    case 2:
                        zombieSchluempfe = new ZombieSchluempfe(config, GELB, ROT);
                        break;
                    case 3:
                        zombieSchluempfe = new ZombieSchluempfe(config, GELB, ROT, BLAU);
                        break;
                    case 4:
                        zombieSchluempfe = new ZombieSchluempfe(config, GELB, ROT, BLAU, GRUEN);
                        break;
                    default:
                        System.err.println("ERROR 001: spielerAnzahl out of bounds");
                        break;
                }
            } catch(Exception e){
                System.err.println("EIN FEHLER IST AUFGETRETEN. SPIEL KONNTE NICHT INITIALISIERT WERDEN. PROGRAMM WIRD BEENDET!");
                System.exit(0);
            }
        }
        return zombieSchluempfe;
    }

    public static void ingame(ZombieSchluempfe zombieSchluempfe){
        Farbe farbeGewonnen = zombieSchluempfe.gewinner(); //hier kommt grad null zurück

        while (farbeGewonnen == null) {
            System.out.println("\nEs ist Spieler " + zombieSchluempfe.getFarbeAmZug() + " dran.");

            System.out.println(zombieSchluempfe.toString());

            int augenzahl = zombieSchluempfe.wuerfeln(); //6 = fliege
            if (augenzahl == 6) {
                fliegeAmZug(zombieSchluempfe);
            } else {
                System.out.println("Augenzahl: " + augenzahl);

                System.out.println("Welche Figur soll gezogen werden?");

                String figurName = scanner.next();
                while(!figurName.contentEquals(zombieSchluempfe.getFarbeAmZug() + "-A") &&
                        !figurName.contentEquals(zombieSchluempfe.getFarbeAmZug() + "-B") &&
                        !figurName.contentEquals(zombieSchluempfe.getFarbeAmZug() + "-C") &&
                        !figurName.contentEquals(zombieSchluempfe.getFarbeAmZug() + "-D")){
                    System.err.println("FEHLERHAFTE EINGABE. ERNEUT EINGEBEN.");
                    figurName = scanner.next();
                }

                //nachrechnen ob abbiegung zwischen figur und figur+augenzahl da ist
                for (Spieler spieler : zombieSchluempfe.getSpielerListe()) {
                    for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                        if (schlumpf.getName().equals(figurName)) {

                            if ((schlumpf.getAktuellesFeld() <= 3 && schlumpf.getAktuellesFeld() + augenzahl > 3)
                                    || (schlumpf.getAktuellesFeld() <= 31 && schlumpf.getAktuellesFeld() + augenzahl > 31)) {

                                System.out.println("In welche Richtung soll gezogen werden? WEITER oder ABZWEIGEN?");
                                String richtungString = scanner.next();
                                while(!richtungString.toLowerCase().contentEquals("weiter") &&
                                        !richtungString.toLowerCase().contentEquals("abzweigen")){
                                    System.err.println("FEHLERHAFTE EINGABE. ERNEUT EINGEBEN.");
                                    richtungString = scanner.next();
                                }

                                //geht das kürzer?
                                if (richtungString.toLowerCase().equals("weiter")) {
                                    System.out.println("WEITER WURDE AUFGERUFEN");
                                    zombieSchluempfe.bewegeFigur(figurName, augenzahl, Richtung.WEITER);
                                } else if (richtungString.toLowerCase().equals("abzweigen")) {
                                    System.out.println("ABZWEIGEN WURDE AUFGERUFEN");
                                    zombieSchluempfe.bewegeFigur(figurName, augenzahl, Richtung.ABZWEIGEN);
                                }
                            } else {
                                zombieSchluempfe.bewegeFigur(figurName, augenzahl);
                            }
                        }
                    }
                }
            }
        }
        System.out.println(farbeGewonnen + " hat gewonnen");
    }

    public static void fliegeAmZug(ZombieSchluempfe zombieSchluempfe){
        System.out.println("Die Fliege wurde gewürfelt!");
        int augenzahl = 6;
        while (augenzahl == 6) {
            augenzahl = zombieSchluempfe.wuerfeln();
        }
        System.out.println("Augenzahl der Fliege: " + augenzahl);

        //nachrechnen ob abbiegung zwischen figur und figur+augenzahl da ist
        if (zombieSchluempfe.fliege.getAktuellesFeld() <= 3 && zombieSchluempfe.fliege.getAktuellesFeld() + augenzahl > 3) {

            System.out.println("In welche Richtung soll gezogen werden? WEITER oder ABZWEIGEN?");

            String richtungString = scanner.next();
            while(!richtungString.toLowerCase().contentEquals("weiter") &&
                    !richtungString.toLowerCase().contentEquals("abzweigen")){
                System.err.println("FEHLERHAFTE EINGABE. ERNEUT EINGEBEN.");
                richtungString = scanner.next();
            }

            if (richtungString.toLowerCase().equals("weiter")) {
                zombieSchluempfe.bewegeFigur("Bzz", augenzahl, Richtung.WEITER);
            } else if (richtungString.toLowerCase().equals("abzweigen")) {
                zombieSchluempfe.bewegeFigur("Bzz", augenzahl, Richtung.ABZWEIGEN);
            }
        } else {
            zombieSchluempfe.bewegeFigur("Bzz", augenzahl);
        }
    }
}
