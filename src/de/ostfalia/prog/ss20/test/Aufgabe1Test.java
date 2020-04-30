package de.ostfalia.prog.ss20.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import de.ostfalia.junit.annotations.AddOnVersion;
import de.ostfalia.junit.annotations.Integrity;
import de.ostfalia.junit.annotations.TestDescription;
import de.ostfalia.junit.base.IAnnotationRules;
import de.ostfalia.junit.base.IMessengerRules;
import de.ostfalia.junit.base.ITraceRules;
import de.ostfalia.junit.common.Enumeration;
import de.ostfalia.junit.conditional.PassTrace;
import de.ostfalia.junit.rules.AnnotationRule;
import de.ostfalia.junit.rules.MessengerRule;
import de.ostfalia.junit.rules.RuleControl;
import de.ostfalia.junit.rules.TraceRule;
import de.ostfalia.junit.runner.TopologicalSortRunner;
import de.ostfalia.prog.ss20.ZombieSchluempfe;
import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;
import de.ostfalia.prog.ss20.interfaces.IZombieSchluempfe;

/**
 *
 *  Feldfolge................:
 *
 *  0-->1-->2-->3-->4-->5-->6-->7-->15-->16-->17-->18-->19-->20-->21-->22
 *      |       |                   |                                   |
 *      |       |                   <---------------                    |
 *      |       |                                  |                    |
 *      |       --->8-->9-->10-->(11)-->12-->13-->14                    |
 *      |                                                               |
 *      |                       -->36 (Dorf)                            |
 *      |                       |                                       |
 *      <--35<--34<--33<--32<--31<--30<--29<--28<--27<--26<--25<--24<--23
 *
 *  Was wird getestet........:
 *
 *      - Konstruktor mit einer und zwei Spielerfarben in standard Spielstellung.
 *      - Konstruktor mit geaenderter Spielstellung.
 *      - Positionsrueckgabe der nicht am Spiel beteiligten Figuren.
 *      - Ziehen einer Figur auf ein besetztes / unbesetztes Feld.
 *      - Ziehen einer Figur ueber ein besetztes Feld.
 *      - Ziehen von Figuren ueber das Verzweigungsfeld.
 *      - Ziehen einer Figuren von Feld 36 auf Feld 1.
 *      - Gewinner des Spiels, wenn sich alle Figuren einer Spielerfarbe in Ziel befindet.
 *
 *
 *  @author MG
 *
 */
@RunWith(TopologicalSortRunner.class)
@AddOnVersion("4.5.3")
@Integrity("ae749960dd20e1f1fd9ff819dcfaffb3")
public class Aufgabe1Test {

    public static RuleControl opt = RuleControl.NONE;
    public IMessengerRules messenger = MessengerRule.newInstance(opt);
    public ITraceRules trace = TraceRule.newInstance(opt);

    public static String ConstrMsg  = "Konstruktoraufruf ZombieSchluempfe";
    public static String moveMsg    = "Aufruf der Methode bewegeFigur(%s, %d)";
    public static String moveDirMsg = "Aufruf der Methode bewegeFigur(%s, %d, %s)";

    @ClassRule
    public static IAnnotationRules classAnno = AnnotationRule.newInstance(opt);

    @Rule
    public TestRule chain = RuleChain
            .outerRule(trace)
            .around(messenger);

    @Rule
    public TestRule timeout = new DisableOnDebug(new Timeout(500, TimeUnit.MILLISECONDS));

    @Before
    public void before() {}

    @After
    public void after() {}

    /**
     * Konstruktoraufruf mit nur einer Spielerfarbe. Alle Figuren muessen sich
     * auf den vorgegebenen Feldern befinden.
     */
    @Test
    @TestDescription("Alle Figuren muessen sich auf den vorgegebenen Feldern befinden.")
    public void testKonstruktor() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Konstruktoraufruf mit zwei Spielerfarben. Alle Figuren muessen sich auf
     * den festgelegten Feldern befinden.
     */
    @Test
    @TestDescription("Alle Figuren muessen sich auf den vorgegebenen Feldern befinden.")
    public void testKonstruktorZweiSpieler() throws Exception {
        trace.add(ConstrMsg + "(%s, %s)", Farbe.BLAU, Farbe.ROT);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU, Farbe.ROT);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0",
                "ROT-A:0",  "ROT-B:0",  "ROT-C:0",  "ROT-D:0",
                "Bzz:20:Z", "Doc:29");

    }

    /**
     * Konstruktoraufruf mit geaenderter Figurenstellung und einer Spielerfarbe.
     * Alle Schluempfe muessen sich auf dem Startfeld befinden. Die Fliege und
     * der Oberschlumpf bekommen neue Felder zugewiesen.
     */
    @Test
    @TestDescription("Die Fliege und der Oberschlumpf bekommen neue Felder zugewiesen.")
    public void testKonstruktorStellung() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:4, Doc:6";
        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);

        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:4:Z", "Doc:6");
    }

    /**
     * Konstruktoraufruf mit geaenderter Figurenstellung und zwei Spielerfarben.
     * Die Figuren der Farbe "Blau" muessen sich auf den Feldern 1 und 2 befinden
     * und die Figuren der Farbe "rot" auf den Feldern 4 bis 7 befinden.
     * Die Fliege und der Oberschlumpf behalten ihre Standardfelder 20 und 29.
     */
    @Test
    @TestDescription("Figuren der Farben BLAU und ROT bekommen neue Felder zugewiesen.")
    public void testKonstruktorStellungZweiSpieler() throws Exception {
        String stellung = "BLAU-A:1, BLAU-B:2, BLAU-C:1, BLAU-D:2, "
                + "ROT-A:4, ROT-B:5, ROT-C:6, ROT-D:7";
        trace.add(ConstrMsg + "(\"%s\", %s, %s)", stellung, Farbe.BLAU, Farbe.ROT);

        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU, Farbe.ROT);
        evaluate(z, "BLAU-A:1", "BLAU-B:2", "BLAU-C:1", "BLAU-D:2",
                "ROT-A:4",  "ROT-B:5",  "ROT-C:6",  "ROT-D:7",
                "Bzz:20:Z", "Doc:29");
    }

    /**
     * Konstruktoraufruf mit nur einer Spielerfarbe. Alle Figuren der nicht am
     * Spiel teilnehmenden Spieler muessen die Position -1 liefern.
     */
    @Test
    @TestDescription("Alle Figuren der nicht am Spiel teilnehmenden Spieler muessen die Position -1 liefern.")
    public void testNichtVorhandeneFiguren() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.ROT);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.ROT);
        evaluate(z, "BLAU-A:-1",  "BLAU-B:-1",  "BLAU-C:-1",  "BLAU-D:-1",
                "GELB-A:-1",  "GELB-B:-1",  "GELB-C:-1",  "GELB-D:-1",
                "GRUEN-A:-1", "GRUEN-B:-1", "GRUEN-C:-1", "GRUEN-D:-1");
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht einen Schlumpf
     * auf Felder 1.
     */
    @Test
    @TestDescription("Der Spieler zieht BLAU-A auf Felder 1.")
    public void testZieheSchlumpf() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:1", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht zwei Figuren
     * auf unterschiedliche Felder, die nicht besetzt sind.
     */
    @Test
    @TestDescription("Der Spieler zieht BLAU-A und BLAU-B auf Felder, die nicht besetzt sind.")
    public void testZieheZweiSchluempfe() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:2", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-B", 1);
        z.bewegeFigur("BLAU-B", 1);
        evaluate(z, "BLAU-A:2", "BLAU-B:1", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht zwei Figuren
     * auf das gleiche Feld.
     */
    @Test
    @TestDescription("Der Spieler zieht BLAU-A und BLAU-B auf das gleiche Feld.")
    public void testZieheSchluempfeAufEinFeld() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:1", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-B", 1);
        z.bewegeFigur("BLAU-B", 1);
        evaluate(z, "BLAU-A:1", "BLAU-B:1", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderter Spielstellung, eine Spielerfarbe. Schlunpf A befindet sich auf Feld
     * 4 und Schlumpf B auf Feld 5. Der Spieler zieht Schlumpf A auf das besetzte Feld 5.
     * Anschliessend verlaesst Schlumpf B das Feld 5 und zieht auf Feld 6.
     */
    @Test
    @TestDescription("Betreten und wieder verlassen eines besetzten Feldes.")
    public void testZieheVonBesetztemFeld() throws Exception {
        String stellung = "BLAU-A:4, BLAU-B:5, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";
        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:4", "BLAU-B:5", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:5", "BLAU-B:5", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-B", 1);
        z.bewegeFigur("BLAU-B", 1);
        evaluate(z, "BLAU-A:5", "BLAU-B:6", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht Schlumpf A
     * auf Feld 1 und anschliessend Schlumpf B auf Feld 2. Feld 1 wird hierbei
     * von Schlumpf B ueberschritten.
     */
    @Test
    @TestDescription("Schlumpf BLAU-B ueberschreitet Feld 1, auf dem sich BLAU-A befindet.")
    public void testUeberschreiteBesetztesFeld() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:1", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-B", 2);
        z.bewegeFigur("BLAU-B", 2);
        evaluate(z, "BLAU-A:1", "BLAU-B:2", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht einen Schlumpf
     * von Feld 35 auf das Feld 1.
     */
    @Test
    @TestDescription("Der Spieler zieht BLAU-A von Feld 35 von Feld 1.")
    public void testZieheSchlumpfVomFeld35() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:35, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";
        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:35", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-B", 1);
        z.bewegeFigur("BLAU-B", 1);
        evaluate(z, "BLAU-A:0", "BLAU-B:1", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht eine Figur
     * auf das Verzweigungsfeld 3 und im anschliessenden Zug in Richtung WEITER.
     * Der Spieler zieht eine andere Figur auf das Verzweigungsfeld und im
     * anschliessenden Zug in Richtung ABZWEIGEN.
     */
    @Test
    @TestDescription("Schluempfe in verschiedene Richtungen ueber das Verzeigungsfeld ziehen.")
    public void testVerzweigung() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 3);
        z.bewegeFigur("BLAU-A", 3);
        evaluate(z, "BLAU-A:3", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveDirMsg, "BLAU-A", 1, Richtung.WEITER);
        z.bewegeFigur("BLAU-A", 1, Richtung.WEITER);
        evaluate(z, "BLAU-A:4", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-B", 3);
        z.bewegeFigur("BLAU-B", 3);
        evaluate(z, "BLAU-A:4", "BLAU-B:3", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveDirMsg, "BLAU-B", 1, Richtung.ABZWEIGEN);
        z.bewegeFigur("BLAU-B", 1, Richtung.ABZWEIGEN);
        evaluate(z, "BLAU-A:4", "BLAU-B:8", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Konstruktoraufruf mit geaenderter Figurenstellung und einer Spielerfarbe.
     * Alle Figuren muessen sich auf den festgelegten Feldern befinden. BLAU
     * gewinnt das Spiel.
     */
    @Test
    @TestDescription("Alle Figuren des Spielers BLAU befinden sich auf dem Zielfeld 36. "
            + "Spieler BLAU muss der Gewinner sein.")
    public void testGewinnerEinSpieler() throws Exception {
        String msg = "Gewinner muss Spieler BLAU sein.";
        String stellung = "BLAU-A:36, BLAU-B:36, BLAU-C:36, BLAU-D:36, Bzz:4, Doc:6";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung,  Farbe.BLAU);
        evaluate(z, "BLAU-A:36", "BLAU-B:36", "BLAU-C:36", "BLAU-D:36", "Bzz:4:Z", "Doc:6");

        Farbe gewinner = z.gewinner();
        trace.add(PassTrace.ifEquals(msg, Farbe.BLAU, gewinner));
        assertEquals(msg, Farbe.BLAU, gewinner);
    }

    /**
     * Konstruktoraufruf mit geaenderter Figurenstellung und zwei Spielerfarbe.
     * Alle Figuren muessen sich auf den festgelegten Feldern befinden. GELB
     * gewinnt das Spiel.
     */
    @Test
    @TestDescription("Alle Figuren des Spielers GELB befinden sich auf dem Zielfeld 36. "
            + "Spieler GELB muss der Gewinner sein.")
    public void testGewinnerZweiSpieler() throws Exception {
        String msg = "Gewinner muss Spieler GELB sein.";
        String stellung = "GELB-A:36, GELB-B:36, GELB-C:36, GELB-D:36, Bzz:4, Doc:6";

        trace.add(ConstrMsg + "(\"%s\", %s, %s)", stellung, Farbe.BLAU, Farbe.GELB);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung,  Farbe.BLAU, Farbe.GELB);
        evaluate(z, "BLAU-A:0",  "BLAU-B:0",  "BLAU-C:0",  "BLAU-D:0",
                "GELB-A:36", "GELB-B:36", "GELB-C:36", "GELB-D:36", "Bzz:4:Z", "Doc:6");

        Farbe gewinner = z.gewinner();
        trace.add(PassTrace.ifEquals(msg, Farbe.GELB, gewinner));
        assertEquals(msg, Farbe.GELB, gewinner);
    }

    /**
     * Es werden zwei Instanzen der Klasse ZombieSchluempfe erstellt, die
     * unterschiedliche Firurenstellungen aufweisen. Fuer beide Instanzen wird
     * jeweils ein gueltige Zug durchgefuehrt. Die beiden Instanzen duerfen sich
     * nicht beeinflussen.
     * Ueberprueft wird ausserdem, welcher Spieler jeweils am Zug ist.
     *
     * @author MG
     */
    @Test
    @TestDescription("Es muss moeglich sein zwei Instanzen unabhaengig voneinander zu spielen.")
    public void testZweiInstanzen() throws Exception {

        trace.add("Spiel 1: " + ConstrMsg + "(%s, %s)", Farbe.BLAU, Farbe.ROT);
        IZombieSchluempfe z1 = new ZombieSchluempfe(Farbe.BLAU, Farbe.ROT);
        evaluate(z1, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "ROT-A:0",
                "ROT-B:0", "ROT-C:0", "ROT-D:0", "Bzz:20:Z", "Doc:29");
        evaluate("Spiel 1: ", Farbe.BLAU, z1.getFarbeAmZug());

        String stellung = "GELB-A:21:Z, GELB-B:30, GELB-C:0, GELB-D:0, GRUEN-A:19, "
                + "GRUEN-B:0, GRUEN-C:0, GRUEN-D:0, Bzz:20:Z, Doc:29";

        trace.add("Spiel 2: " + ConstrMsg + "(\"%s\", %s, %s)", stellung, Farbe.GRUEN, Farbe.GELB);
        IZombieSchluempfe z2 = new ZombieSchluempfe(stellung, Farbe.GRUEN, Farbe.GELB);
        evaluate(z2, "GELB-A:21:Z", "GELB-B:30", "GELB-C:0", "GELB-D:0", "GRUEN-A:19",
                "GRUEN-B:0", "GRUEN-C:0", "GRUEN-D:0", "Bzz:20:Z", "Doc:29");
        evaluate("Spiel 2: ", Farbe.GRUEN, z2.getFarbeAmZug());
        //-------------------------------------------------------------------------

        trace.add("Spiel 2: " + moveMsg, "GRUEN-A", 2);
        z2.bewegeFigur("GRUEN-A", 2);
        evaluate(z2, "GELB-A:21:Z", "GELB-B:30", "GELB-C:0", "GELB-D:0", "GRUEN-A:21:Z",
                "GRUEN-B:0", "GRUEN-C:0", "GRUEN-D:0", "Bzz:20:Z", "Doc:29");
        //-------------------------------------------------------------------------

        trace.add("Spiel 1: " + moveMsg, "BLAU-A", 3);
        z1.bewegeFigur("BLAU-A", 3);
        evaluate(z1, "BLAU-A:3", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "ROT-A:0",
                "ROT-B:0", "ROT-C:0", "ROT-D:0", "Bzz:20:Z", "Doc:29");
        //-------------------------------------------------------------------------

        evaluate("Spiel 1: ", Farbe.ROT,  z1.getFarbeAmZug());
        evaluate("Spiel 2: ", Farbe.GELB, z2.getFarbeAmZug());
    }


    //-------------------------------------------------------------


    // Feldbelegung: "BLAU-A:5", "BLAU-B:4", ""BLAU-C:8:Z"...
    private void evaluate(IZombieSchluempfe spiel, String... feldbelegung) {
        ITraceRules subtrace = trace.newSubtrace(opt);
        subtrace.enumeration(new Enumeration(0, Enumeration.letters));
        subtrace.add("Spielstellung (toString): %s", spiel);

        StringJoiner  got = new StringJoiner(", ", "[", "]");
        for (String belegung : feldbelegung) {
            String[] parts = belegung.split(":");
            String name = parts[0].trim();
            int gotField = spiel.getFeldnummer(name);
            boolean gotZombie = spiel.istZombie(name);
            if (gotZombie) {
                got.add(name + ":" + gotField + ":Z");
            } else {
                got.add(name + ":" + gotField);
            }
        }
        subtrace.add(PassTrace.ifEquals("Ueberpruefung der Spielfiguren.", feldbelegung, got));
        trace.add(subtrace);
        assertEquals("Unerwartete Spielstellung", Arrays.toString(feldbelegung), got.toString());
    }

    private void evaluate(String msg, Farbe erwartet, Farbe amZug) {
        trace.add(PassTrace.ifEquals(msg + "Spieler %s ist am Zug.", erwartet, amZug, erwartet));
        assertFalse("Falsche Farbe am Zug.", trace.hasOccurrences());
    }

}
