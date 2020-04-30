package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;
import de.ostfalia.prog.ss20.felder.Feld;
import de.ostfalia.prog.ss20.felder.Normalfeld;
import de.ostfalia.prog.ss20.felder.Spezialfeld;
import de.ostfalia.prog.ss20.felder.Startfeld;
import de.ostfalia.prog.ss20.felder.Zielfeld;
import de.ostfalia.prog.ss20.figuren.Doc;
import de.ostfalia.prog.ss20.figuren.Fliege;
import de.ostfalia.prog.ss20.figuren.Schlumpf;
import de.ostfalia.prog.ss20.interfaces.IZombieSchluempfe;

import java.util.ArrayList;
import java.util.List;

public class ZombieSchluempfe implements IZombieSchluempfe {

    /*
    debugging/cheatbefehle, um bestimmte spielsituationen herstellen zu können
     */

    private List<Feld> feldListe = new ArrayList<>();
    private List<Spieler> spielerListe = new ArrayList<>();
    private List<Schlumpf> zombieSchluempfe = new ArrayList<>();
    private Spieler spielerAmZug;
    Fliege fliege;
    Doc doc;
    Zielfeld zielfeld;

    public void initialisieren() {
        zielfeld = new Zielfeld(36);
        feldListe = felderGenerieren();

        //Startspieler bestimmen
        spielerAmZug = spielerListe.get(0);
    }

    public List<Feld> felderGenerieren() {
        List<Feld> felder = new ArrayList<>();
        for (int x = 36; x >= 0; x--) {
            switch (x) {
                case 0:
                    //Startfeld
                    felder.add(new Startfeld(x));
                    break;
                case 11:
                    //Tuberose
                    felder.add(new Spezialfeld(x));
                    break;
                case 16:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 17:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 25:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 26:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 27:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 24:
                    //Pilz
                    felder.add(new Spezialfeld(x));
                    break;
                case 29:
                    //Docs Labor
                    felder.add(new Spezialfeld(x));
                    break;
                case 36:
                    //Dorf
                    felder.add(new Zielfeld(x));
                    break;
                default:
                    felder.add(new Normalfeld(x));
                    break;
            }
        }
        return felder;
    }

    public void zugBeenden() {
        int newIndex = spielerListe.indexOf(spielerAmZug) + 1;
        if (newIndex >= spielerListe.size()) {
            newIndex = 0;
        }
        spielerAmZug = spielerListe.get(newIndex);
    }

    public static int wuerfeln() {
        return (int) (Math.random() * 6) + 1;
    }

    public ZombieSchluempfe(Farbe... farben) {
        //je nachdem wie viele Farben, so viele Spieler
        for (Farbe farbe : farben) {
            spielerListe.add(new Spieler(farbe));
        }

        doc = new Doc("Doc", 29);
        fliege = new Fliege("Bzz", 20);

        initialisieren();
    }

    public ZombieSchluempfe(String conf, Farbe... farben) {
        for (Farbe farbe : farben) {
            spielerListe.add(new Spieler(farbe));
        }

        doc = new Doc("Doc", 29);
        fliege = new Fliege("Bzz", 20);

        //conf aufspalten:
        String[] confs = conf.split(", ");
        for (String config : confs) {
            String configName = config.substring(0, config.indexOf(":"));
            int feld;
            boolean istZombie;
            if (config.contains(":Z")) {
                feld = Integer.parseInt(config.substring(config.indexOf(":") + 1, config.indexOf(":Z")));
                istZombie = true;
            } else {
                feld = Integer.parseInt(config.substring(config.indexOf(":") + 1));
                istZombie = false;
            }

            for (Spieler spieler : spielerListe) {
                for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                    if (schlumpf.getName().contentEquals(configName)) {
                        schlumpf.setAktuellesFeld(feld);
                        schlumpf.setIstZombie(istZombie);
                        if(istZombie){
                            zombieSchluempfe.add(schlumpf);
                        }
                    }
                }
            }
            if (configName.contentEquals("Bzz")) {
                fliege.setAktuellesFeld(feld);
            } else if (configName.contentEquals("Doc")) {
                doc.setAktuellesFeld(feld);
            }
        }
        initialisieren();
    }

    @Override
    public boolean bewegeFigur(String figurName, int augenzahl, Richtung richtung) {

        if(gewinner() == null) {
            if (figurName.equals(fliege.getName())) {
                fliegeBewegung(augenzahl, richtung);
                zugBeenden();
                return true;
            } else {
                for (Spieler spieler : spielerListe) {
                    for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                        if (schlumpf.getName().equals(figurName)) {
                            if(schlumpf.getAktuellesFeld() == 36){
                                zugBeenden();
                                return true;
                            }
                            for (int i = 1; i <= augenzahl; i++) {
                                //überprüfen ob abzweigung
                                if (schlumpf.getAktuellesFeld() == 3 || schlumpf.getAktuellesFeld() == 31) {
                                    if (richtung == Richtung.WEITER) {
                                        schlumpf.setAktuellesFeld(schlumpf.getAktuellesFeld() + 1);
                                    } else if (schlumpf.getAktuellesFeld() == 3) {
                                        schlumpf.setAktuellesFeld(8);
                                    } else if (schlumpf.getAktuellesFeld() == 31) {
                                        schlumpf.setAktuellesFeld(36);
                                    }

                                } else if (schlumpf.getAktuellesFeld() == 7) {
                                    schlumpf.setAktuellesFeld(15);
                                } else if (schlumpf.getAktuellesFeld() == 35) {
                                    schlumpf.setAktuellesFeld(1);
                                } else {
                                    schlumpf.setAktuellesFeld(schlumpf.getAktuellesFeld() + 1); //zieht feld für feld
                                }

                                //wenn schlumpf im ziel ist, ist zug beendet:
                                if (schlumpf.getAktuellesFeld() >= 36) { // >= statt == falls irgendwas schief läuft
                                    zielfeld.addToZielListe(schlumpf);
                                    System.out.println(figurName + " ist nun im Ziel.");
                                    zugBeenden();
                                    return true;
                                }

                                //pro feld statusveränderungen anpassen:
                                if (schlumpf.getAktuellesFeld() == 11 && schlumpf.isIstZombie()) {
                                    schlumpf.setIstZombie(false);
                                    zombieSchluempfe.remove(schlumpf);
                                    System.out.println("Das Blütenstaubfeld heilt " + figurName + ". Er ist nun kein Zombie mehr.");
                                }
                                if (schlumpf.getAktuellesFeld() == doc.getAktuellesFeld() && schlumpf.isIstZombie()) {
                                    schlumpf.setIstZombie(false);
                                    zombieSchluempfe.remove(schlumpf);
                                    System.out.println("Doc heilt Schlumpf " + figurName + ". Er ist nun kein Zombie mehr.");
                                }
                                if (schlumpf.getAktuellesFeld() == fliege.getAktuellesFeld() && !schlumpf.isIstZombie()) {
                                    schlumpf.setIstZombie(true);
                                    zombieSchluempfe.add(schlumpf);
                                    System.out.println("Die Fliege beißt Schlumpf " + figurName + ". Er ist nun ein Zombie.");
                                }
                                for (Schlumpf schlumpf2 : zombieSchluempfe) {
                                    if (schlumpf.getAktuellesFeld() == schlumpf2.getAktuellesFeld()) {
                                        schlumpf.setIstZombie(true);
                                    }
                                }
                                System.out.println(figurName + " ist nun auf Feld " + schlumpf.getAktuellesFeld() + ".");
                            }
                            zugBeenden();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void fliegeBewegung(int augenzahl, Richtung richtung){
        //überprüfen ob Abzweigung
            for (int i = 1; i <= augenzahl; i++) {
                //überprüfen ob abzweigung
                if (fliege.getAktuellesFeld() == 3) {
                    if (richtung == Richtung.WEITER) {
                        fliege.setAktuellesFeld(fliege.getAktuellesFeld() + 1);
                    } else {
                        fliege.setAktuellesFeld(8);
                    }
                } else if (fliege.getAktuellesFeld() == 7) {
                    fliege.setAktuellesFeld(15);
                } else if (fliege.getAktuellesFeld() == 35) {
                    fliege.setAktuellesFeld(1);
                } else {
                    fliege.setAktuellesFeld(fliege.getAktuellesFeld() + 1); //zieht feld für feld
                }
        }

        // ggf statusveränderungen anpassen wenn auf feld ein zombie ist:
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (fliege.getAktuellesFeld() == schlumpf.getAktuellesFeld() && !schlumpf.isIstZombie()) {
                    schlumpf.setIstZombie(true);
                    zombieSchluempfe.add(schlumpf);
                    System.out.println("Die Fliege beißt Schlumpf " + schlumpf.getName() + ". Er ist nun ein Zombie.");
                }
            }
        }
        System.out.println(fliege.getName() + " ist nun auf Feld " + fliege.getAktuellesFeld() + ".");
    }


    @Override
    public boolean bewegeFigur(String figurName, int augenzahl) {
        return bewegeFigur(figurName, augenzahl, Richtung.WEITER);
    }


    @Override
    public int getFeldnummer(String figurName) {
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {
                    return schlumpf.getAktuellesFeld();
                }
            }
        }
        if (figurName.contentEquals("Bzz")) {
            return fliege.getAktuellesFeld();
        } else if (figurName.contentEquals("Doc")) {
            return doc.getAktuellesFeld();
        }
        return -1;
    }

    @Override
    public boolean istZombie(String figurName) {
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {
                    return schlumpf.isIstZombie();
                }
            }
        }
        if (figurName.contentEquals("Bzz")) {
            return fliege.getIstZombie();
        } else if (figurName.contentEquals("Doc")) {
            return doc.getIstZombie();
        }
        return false;
    }

    @Override
    public Farbe getFarbeAmZug() {
        if (spielerAmZug != null) {
            return spielerAmZug.getFarbe();
        }
        return null;
    }

    @Override
    public Farbe gewinner() {
        int counter = 0;
        for (Spieler spieler : spielerListe) { //für jeden spieler die liste durchsuchen
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getAktuellesFeld() == 36) {
                    counter++;
                }
            }
            if (counter == 4) { //spieler hat 4 schlümpfe im ziel, hat gewonnen
                return spieler.getSpielerFarbe();
            }
            counter = 0;
        }
        return null;
    }

    @Override
    public String toString() {
        String spielStatus = "Spielstatus: ";
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                spielStatus = spielStatus.concat(schlumpf.getName() + ":" + schlumpf.getAktuellesFeld());
                if (schlumpf.isIstZombie()) {
                    spielStatus = spielStatus.concat(":Z");
                }
                spielStatus = spielStatus.concat(", ");
            }
        }
        //Bzz
        spielStatus = spielStatus.concat("Bzz:" + fliege.getAktuellesFeld());
        if (fliege.getIstZombie()) {
            spielStatus = spielStatus.concat(":Z");
        }
        spielStatus = spielStatus.concat(", ");

        //Doc
        spielStatus = spielStatus.concat("Doc:" + doc.getAktuellesFeld());
        if (doc.getIstZombie()) {
            spielStatus = spielStatus.concat(":Z");
        }
        return spielStatus;
    }

    public List<Spieler> getSpielerListe() {
        return spielerListe;
    }
}
