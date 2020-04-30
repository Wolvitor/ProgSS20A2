package de.ostfalia.prog.ss20.figuren;

public class Figur {
    private int aktuellesFeld;
    private String name;

    public Figur(String name, int aktuellesFeld) {
        this.aktuellesFeld = aktuellesFeld;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public int getAktuellesFeld() {
        return aktuellesFeld;
    }
}
