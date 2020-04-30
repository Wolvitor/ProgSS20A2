package de.ostfalia.prog.ss20.felder;

import de.ostfalia.prog.ss20.figuren.Schlumpf;

import java.util.ArrayList;
import java.util.List;

public class Feld {
    List<Feld> nachbarListe;
    List<Schlumpf> figurListe = new ArrayList<>();
    int nummer;
    boolean istAbzweigung = false;

    public Feld(int nummer, List<Feld> nachbarListe) {
        this.nummer=nummer;
        this.nachbarListe = nachbarListe;

        if(nachbarListe.size() > 1){
            istAbzweigung = true;
        }
    }

    public Feld(int nummer){
        this.nummer = nummer;
        nachbarListe = new ArrayList<>();
    }

    public List<Feld> getNachbarListe() {
        return nachbarListe;
    }

    public void setNachbarListe(List<Feld> nachbarListe) {
        this.nachbarListe = nachbarListe;
    }

    public List<Schlumpf> getFigurListe() {
        return figurListe;
    }

    public void setFigurListe(List<Schlumpf> figurListe) {
        this.figurListe = figurListe;
    }

    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public boolean isIstAbzweigung() {
        return istAbzweigung;
    }

    public void setIstAbzweigung(boolean istAbzweigung) {
        this.istAbzweigung = istAbzweigung;
    }


    //wenn >1 figur auf feld, rufe NPC.wirken auf
}
