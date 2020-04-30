package de.ostfalia.prog.ss20.interfaces;
import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;


/**
 * Das Spiel "Die ZombieSchlümpfe" ist ein Brettspiel, bei dem sich Schlümpfe auf Pfaden
 * durch den Schlumpfwald zum Schlumpfdorf bewegen.
 *
 * Gewonnen hat der Spieler, dessen Schlümpfe zuerst vollzählig das Dorf erreichen.
 * Im Schlumpfwald lauert die gefährliche Fliege, die einen Schlumpf infizieren kann und
 * dadurch in einen Zombieschlumpf verwandelt. Zombieschlümpfe infizieren gesunde Schlümpfe,
 * wenn sie ihnen begegnen, und  dürfen das Schlumpfdorf verständlicherweise nicht betreten.
 *
 * Auf speziellen Feldern, dem Blütenstaubfeld und dem Pilzfeld, oder durch eine Begegnung mit dem Oberschlumpf
 * oder die Schlumpfine können Zombieschlümpfe geheilt werden,
 * d.h. sie verwandeln sich wieder in gesunde Schlümpfe.
 *
 *
 * Weitere Erklärungen zum Spiel können aus der Aufgabestellung entnommen
 * werden.
 *
 * @author M. Gruendel und D. Dick
 * @since SS 2020
 */
public interface IZombieSchluempfe {

    /*
     * *********************************************************************
     * ****************************** METHODEN *****************************
     * *********************************************************************
     */

    /**
     * Bewegt die Figur mit dem angegebenen Namen um die gewürfelte Augenzahl
     * weiter. Sollte die Figur allerdings nicht bewegt werden können (weil sie
     * z.B. auf ein Feld landen würde, was nicht erlaubt ist), behält sie ihre
     * ursprüngliche Position. Der Zug gilt dennoch als vollzogen und der
     * nächste Spieler ist an der Reihe.
     *
     * Muss ein Spieler anstatt einer seiner Schlümpfe, die Fliege bewegen, gilt
     * der Zug ebenso als vollzogen und der nächste Spieler ist an der Reihe.
     * Auch, wenn die Bewegung der Fliege zu einem ungültigen Spielzug führen
     * würde.
     *
     * @param figurName
     *            der Name bzw. die eindeutige Identifikation der Figur, welche
     *            bewegt werden soll
     * @param augenzahl
     *            der gewürfelte Wert entspricht die Anzahl von Felder, die die
     *            Figur vorrücken muss
     * @param richtung
     *            die Richtungangabe für die Verzweigungsfelder. Wenn keine
     *            Richtung angegeben wird, dann bewegt sich die Figur einfach
     *            "geradeaus"
     * @return true, wenn die Figur bewegt werden konnte; sonst false
     */
    public boolean bewegeFigur(String figurName, int augenzahl, Richtung richtung);
    public boolean bewegeFigur(String figurName, int augenzahl);

    /**
     * Liefert die Position (die Feldnummer) der Figur mit dem angegebenen
     * Namen.
     *
     * @param name
     *            der Name bzw. die eindeutige Identifikation der Figur
     * @return die Position der Figur, wenn sie gefunden wurde; sonst -1
     */
    public int getFeldnummer(String name);

    /**
     * Ermittelt, ob die Figur mit dem angegebenen Namen ein Zombie ist oder
     * nicht.
     *
     * @param name
     *            der Name bzw. die eindeutige Identifikation der Figur
     * @return true, wenn die Figur ein Zombie ist; sonst false
     */
    public boolean istZombie(String name);

    /**
     * Methode liefert die Farbe des Spieler der aktuell spielen darf.
     *
     * @return die Farbe des Spieler, der aktuell spielen darf
     */
    public Farbe getFarbeAmZug();

    /**
     * Liefert die Farbe des Spielers, der das Spiel gewonnen hat.
     *
     * @return Farbe des Spielgewinners, wenn ein Gewinner gibt; sonst null
     */
    public Farbe gewinner();

    /**
     * Methode liefert eine String-Repräsentation des Spiels zurück.
     *
     * @return Das Spiel in Form eines Strings
     */
    @Override
    public String toString();

}
