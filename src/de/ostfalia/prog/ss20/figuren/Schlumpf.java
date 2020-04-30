package de.ostfalia.prog.ss20.figuren;

public class Schlumpf extends Figur {
    private int aktuellesFeld;
    private boolean istZombie = false;
    private String name;

    public Schlumpf(String name, int aktuellesFeld, boolean istZombie) {
        super(name, aktuellesFeld);
        this.name = name;
        this.aktuellesFeld = aktuellesFeld;
        this.istZombie = istZombie;

        //System.out.println(name + " wurde auf Feld " + aktuellesFeld + " platziert!");
    }

    public int getAktuellesFeld() {
        return aktuellesFeld;
    }

    public void setAktuellesFeld(int aktuellesFeld) {
        this.aktuellesFeld = aktuellesFeld;
    }

    public boolean isIstZombie() {
        return istZombie;
    }

    public void setIstZombie(boolean istZombie) {
        this.istZombie = istZombie;
    }

    public String getName() {
        return name;
    }
}
