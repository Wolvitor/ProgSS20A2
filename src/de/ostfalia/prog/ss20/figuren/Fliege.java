package de.ostfalia.prog.ss20.figuren;

public class Fliege extends NPC {
    private int aktuellesFeld;
    private boolean istZombie = true;
    private String name;

    public Fliege(String name, int aktuellesFeld) {
        super(name, aktuellesFeld);
        this.aktuellesFeld=aktuellesFeld;
        this.name=name;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getAktuellesFeld(){
        return aktuellesFeld;
    }

    public void setAktuellesFeld(int feld){
        aktuellesFeld = feld;
    }

    public boolean getIstZombie(){
        return istZombie;
    }

}
