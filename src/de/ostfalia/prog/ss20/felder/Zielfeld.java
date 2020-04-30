package de.ostfalia.prog.ss20.felder;

import de.ostfalia.prog.ss20.figuren.Schlumpf;

import java.util.ArrayList;
import java.util.List;

public class Zielfeld extends Spezialfeld {

    private List<Schlumpf> zielListe = new ArrayList<>();

    public Zielfeld(int nummer) {
        super(nummer);
    }

    public boolean addToZielListe(Schlumpf schlumpf){
        try {
            zielListe.add(schlumpf);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public List<Schlumpf> getZielListe(){
        return zielListe;
    }
}
