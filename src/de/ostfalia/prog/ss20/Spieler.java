package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.figuren.Schlumpf;

import java.util.ArrayList;
import java.util.List;

public class Spieler {
    private Farbe farbe;
    private String name;

    private List<Schlumpf> schlumpfListe = new ArrayList<>();

    public Spieler(Farbe farbe) {
        this.farbe = farbe;


        switch (farbe) {
            case ROT:
                schlumpfListe.add(new Schlumpf("ROT-A", 0, false));
                schlumpfListe.add(new Schlumpf("ROT-B", 0, false));
                schlumpfListe.add(new Schlumpf("ROT-C", 0, false));
                schlumpfListe.add(new Schlumpf("ROT-D", 0, false));
                break;
            case BLAU:
                schlumpfListe.add(new Schlumpf("BLAU-A", 0, false));
                schlumpfListe.add(new Schlumpf("BLAU-B", 0, false));
                schlumpfListe.add(new Schlumpf("BLAU-C", 0, false));
                schlumpfListe.add(new Schlumpf("BLAU-D", 0, false));
                break;
            case GELB:
                schlumpfListe.add(new Schlumpf("GELB-A", 0, false));
                schlumpfListe.add(new Schlumpf("GELB-B", 0, false));
                schlumpfListe.add(new Schlumpf("GELB-C", 0, false));
                schlumpfListe.add(new Schlumpf("GELB-D", 0, false));
                break;
            case GRUEN:
                schlumpfListe.add(new Schlumpf("GRUEN-A", 0, false));
                schlumpfListe.add(new Schlumpf("GRUEN-B", 0, false));
                schlumpfListe.add(new Schlumpf("GRUEN-C", 0, false));
                schlumpfListe.add(new Schlumpf("GRUEN-D", 0, false));
                break;
            default:
                System.err.println("ERROR 001: UNKNOWN COLOUR");
                break;
        }
        name = farbe.name();
    }

    public Farbe getSpielerFarbe(){
        return farbe;
    }

    public String getSpielerFarbeString(){
        return farbe.name();
    }

    public List<Schlumpf> getSchlumpfListe() {
        return schlumpfListe;
    }

    public Farbe getFarbe(){
        return farbe;
    }
}
