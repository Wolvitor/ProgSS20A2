package de.ostfalia.prog.ss20.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

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
 * @author M. Gruendel
 * @author D. Dick
 *
 * Feldfolge................:
 *
 * 	0-->1-->2-->3-->4-->5-->6-->7-->15-->16-->17-->18-->19-->20-->21-->22
 *      |       |                   |                                   |
 *      |       |                   <---------------                    |
 *      |       |                                  |                    |
 *      |       --->8-->9-->10-->(11)-->12-->13-->14                    |
 *      |                                                               |
 *      |                       -->36 (Dorf)                            |
 *      |                       |                                       |
 *      <--35<--34<--33<--32<--31<--30<--29<--28<--27<--26<--25<--24<--23
 *
 *	Was wird getestet........:
 *
 * 		- Konstruktor fuer die Standardposition der Figuren (Startfeld)
 * 		- Konstruktor fuer die konfigurierbare Position der Figuren auf dem Spielfeld
 * 		- Konstruktoraufruf mit geaenderter Figurenstellung und Zombie
 * 		- Erster Spieler am Zug
 * 		- Bewegen der Schluempfe vom Startfeld auf das Spielfeld
 * 		- Bewegen der Schluempfe auf/ueber die Standardfelder
 * 		- Bewegen mehrerer Schluempfe auf ein Feld
 * 		- Bewegen der Schluempfe auf/ueber das Verzweigungsfeld (Position 3)
 * 		- Bewegen eines Schluempfes auf/ueber die Position der Fliege
 * 		- Bewegen eines Schluempfes/Zombies auf/ueber die Position des Oberschlumpfs
 * 		- Bewegen der Fliege auf die Position eines Schlumpfs/mehrerer Schluempfe
 * 		- Bewegen der Fliege hinter die Position eines Schlumpfs
 * 		- Versuch den Oberschlumpf zu bewegen
 * 		- Ziehen ueber das Verzweigungsfeld 3 und Verzweigungsfeld 31 (vor dem Dorf)
 * 		- Ziehen ueber das Feld 15 aus verschiedenen Richtungen
 * 		- Das Spielverhalten beim Ziehen eines Schlumpfs in das Dorf
 * 		- Das Spielverhalten beim Ziehen einer Figur, die sich im Dorf befindet
 * 		- Die Richtigkeit der Farbe, die gerade spielen darf
 *
 *
 */

@RunWith(TopologicalSortRunner.class)
@AddOnVersion("4.5.3")
@Integrity("7dc3a1ee9d8d8ae66a73276095d40d42")
public class Aufgabe2Test {

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
    public void setUp() throws Exception {}

    /**
     * Konstruktoraufruf mit nur einer Spielerfarbe. Alle Figuren muessen sich
     * auf den festgelegten Feldern befinden.
     * @author MG
     */
    @Test
    @TestDescription("Alle Figuren muessen sich auf den festgelegten Feldern befinden.")
    public void testKonstruktor() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);

        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Konstruktoraufruf mit zwei Spielerfarben. Alle Figuren muessen sich auf
     * den festgelegten Feldern befinden.
     * @author MG
     */
    @Test
    @TestDescription("Alle Figuren muessen sich auf den festgelegten Feldern befinden.")
    public void testKonstruktorZweiSpieler() throws Exception {
        trace.add(ConstrMsg + "(%s, %s)", Farbe.BLAU, Farbe.ROT);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU, Farbe.ROT);

        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0",
                "ROT-A:0", "ROT-B:0",  "ROT-C:0",  "ROT-D:0",
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
     * Konstruktoraufruf mit geaenderter Figurenstellung und einer Spielerfarbe.
     * Mit Ausnahme vom Schlumpf "GELB-B", sind alle anderen "normale" Schluempfe und
     * befinden sich auf dem Startfeld. Der o.g. Schlumpf befindet sich hinter der Fliege
     * und ist ein Zombie. Darueber hinaus, bekommen sowohl die Fliege als auch der Oberschlumpf
     * neue Felder zugewiesen.
     * @author DD
     */
    @Test
    @TestDescription("GELB-A, GELB-C und GELB-D sind \"normal\", GELB-B ist ein Zombie.")
    public void testKonstruktorStellungMitZombie() throws Exception {
        String stellung = "GELB-A:0, GELB-B:5:Z, GELB-C:0, GELB-D:0, Bzz:4, Doc:6";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.GELB);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.GELB);

        evaluate(z, "GELB-A:0", "GELB-B:5:Z", "GELB-C:0", "GELB-D:0", "Bzz:4:Z", "Doc:6");
    }

    /**
     * Ueberprueft, ob die richtige Farbe als naechste spielen darf. Da die Figuren
     * sich noch nicht bewegt haben, muss die erste Farbe die korrekte sein.
     * @author DD
     *
     */
    @Test
    @TestDescription("Der erste Spieler in der Liste beginnt das Spiel.")
    public void testFarbeAmZug() throws Exception {
        trace.add(ConstrMsg + "(%s, %s)", Farbe.GELB, Farbe.GRUEN);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.GELB, Farbe.GRUEN);

        Farbe anZug = z.getFarbeAmZug();
        trace.add(PassTrace.ifEquals("Der erste Spieler ist am Zug.", Farbe.GELB, anZug));
        assertFalse("Falsche Farbe am Zug.", trace.hasOccurrences());
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht nacheinander
     * seine Figuren auf die Felder 1 bis 4. Die Schluempfe ueberschreiten hierbei
     * besetzte Felder.
     * @author MG
     */
    @Test
    @TestDescription("Ueberschreiten von Feldern, die von Schluempfen besetzt sind.")
    public void testZieheSchluempfe() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);

        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:1", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-B", 2);
        z.bewegeFigur("BLAU-B", 2);
        evaluate(z, "BLAU-A:1", "BLAU-B:2", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-C", 3);
        z.bewegeFigur("BLAU-C", 3);
        evaluate(z, "BLAU-A:1", "BLAU-B:2", "BLAU-C:3", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-D", 4);
        z.bewegeFigur("BLAU-D", 4);
        evaluate(z, "BLAU-A:1", "BLAU-B:2", "BLAU-C:3", "BLAU-D:4", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf befindet sich
     * gerade im Dorf, soll aber mit Augenzahl 4 aus dem Dorf gezogen werden.
     * Dieser Spielzug ist ungueltig. Der Schlump muss die Position behalten und
     * der naechste Spieler ist an der Reihe.
     * @author DD
     */
    @Test
    @TestDescription("Schluempfe duerfen nicht aus dem Dorf gezogen werden koennen.")
    public void testZieheSchluempfeAusDorf() throws Exception {
        String stellung = "GELB-A:36, GELB-B:6, GELB-C:36, GELB-D:18, ROT-A:10, ROT-B:5, ROT-C:5, ROT-D:0, Bzz:30, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s, %s)", stellung, Farbe.GELB, Farbe.ROT);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.GELB, Farbe.ROT);

        evaluate(z, "GELB-A:36", "GELB-B:6", "GELB-C:36", "GELB-D:18", "ROT-A:10",
                "ROT-B:5", "ROT-C:5", "ROT-D:0", "Bzz:30:Z", "Doc:29");

        trace.add(moveMsg, "GELB-A", 4);
        z.bewegeFigur("GELB-A", 4);
        evaluate(z, "GELB-A:36", "GELB-B:6", "GELB-C:36", "GELB-D:18", "ROT-A:10",
                "ROT-B:5", "ROT-C:5", "ROT-D:0", "Bzz:30:Z", "Doc:29");

        Farbe anZug = z.getFarbeAmZug();
        trace.add(PassTrace.ifEquals("Der naechste Spieler ist am Zug.", Farbe.ROT, anZug));
        assertFalse("Falsche Farbe am Zug.", trace.hasOccurrences());
    }

    /**
     * Standard Spielstellung, zwei Spielerfarben.
     * Ueberprueft, ob die richtige Farbe als naechste spielen darf. Da die erste
     * Figur sich bereits bewegte, muss die zweite Farbe die korrekte sein.
     * @author DD
     *
     */
    @Test
    @TestDescription("Die Spielreihenfolge entspricht der Reihenfolge der Farben beim Konstruktoraufruf.")
    public void testFarbeAmZugNachBewegung() throws Exception {
        trace.add(ConstrMsg + "(%s, %s)", Farbe.GELB, Farbe.GRUEN);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.GELB, Farbe.GRUEN);

        trace.add(moveMsg, "GELB-A", 1);
        z.bewegeFigur("GELB-A", 1);

        Farbe anZug = z.getFarbeAmZug();
        trace.add(PassTrace.ifEquals("Der zweite Spieler ist am Zug.", Farbe.GRUEN, anZug));
        assertFalse("Falsche Farbe am Zug.", trace.hasOccurrences());
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht nacheinander
     * seine Figuren auf das gleiche Feld.
     * @author MG
     */
    @Test
    @TestDescription("Ziehen der vier Spielerfiguren auf ein Feld.")
    public void testZieheSchluempfeAufEinFeld() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:2", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-B", 2);
        z.bewegeFigur("BLAU-B", 2);
        evaluate(z, "BLAU-A:2", "BLAU-B:2", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-C", 2);
        z.bewegeFigur("BLAU-C", 2);
        evaluate(z, "BLAU-A:2", "BLAU-B:2", "BLAU-C:2", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-D", 2);
        z.bewegeFigur("BLAU-D", 2);
        evaluate(z, "BLAU-A:2", "BLAU-B:2", "BLAU-C:2", "BLAU-D:2", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht nacheinander
     * die gleiche Figur. Die gezogene Figur muss sich korrekt auf dem Spielfeld
     * bewegen. Alle anderen Figuren muessen sich weiterhin auf dem Startfeld
     * befinden.
     * @author MG
     */
    @Test
    @TestDescription("Mehrfaches ziehen einer Firgur ueber das Spielfeld.")
    public void testZieheSchluempfeMehrmals() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:1", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:3", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 3);
        z.bewegeFigur("BLAU-A", 3);
        evaluate(z, "BLAU-A:15", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler zieht eine Figur
     * auf das Verzweigungsfeld 3 und im anschliessenden Zug in Richtung WEITER.
     * Der Spieler zieht eine andere Figur auf das Verzweigungsfeld und im
     * anschliessenden Zug in Richtung ABZWEIGEN.
     * @author MG
     */
    @Test
    @TestDescription("Ziehen ueber das Verzweigungsfeld in Richtung WEITEN und ABZWEIGEN.")
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
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Spieler zieht eine Figur
     * auf das Verzweigungsfeld 31 und im anschliessenden Zug in Richtung
     * WEITER. Der Spieler zieht eine andere Figur auf das Verzweigungsfeld und
     * im anschliessenden Zug in Richtung ABZWEIGEN. Wobei die Augenzahl zu
     * einem Ziel ueber das Darf hinaus fuehren wuerde.
     * @author MG
     */
    @Test
    @TestDescription("Ziehen ueber das Verzeigungsfeld vor dem Dorf in Richtung WEITER und ABZWEIGEN.")
    public void testVerzweigung2() throws Exception {
        String stellung = "BLAU-A:30, BLAU-B:31, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:30", "BLAU-B:31", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveDirMsg, "BLAU-A", 2, Richtung.WEITER);
        z.bewegeFigur("BLAU-A", 2, Richtung.WEITER);
        evaluate(z, "BLAU-A:32", "BLAU-B:31", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveDirMsg, "BLAU-B", 2, Richtung.ABZWEIGEN);
        z.bewegeFigur("BLAU-B", 2, Richtung.ABZWEIGEN);
        evaluate(z, "BLAU-A:32", "BLAU-B:36", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf wird vom
     * Startfeld auf das Feld der Fliege gezogen. Der Schlumpf muss zum Zombie
     * werden!
     * @author MG
     */
    @Test
    @TestDescription("Ziehen eines Schlumpfs auf das Feld der Fliege.")
    public void testSchlumpfAufFliege() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:4, Doc:6";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:4:Z", "Doc:6");

        trace.add(moveMsg, "BLAU-A", 4);
        z.bewegeFigur("BLAU-A", 4);
        evaluate(z, "BLAU-A:4:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:4:Z", "Doc:6");

    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf wird vom
     * Startfeld auf das Feld hinter der Fliege gezogen. Der Schlumpf muss zum
     * Zombie werden!
     * @author MG
     */
    @Test
    @TestDescription("Ziehen eines Schlumpfs ueber das Feld der Fliege.")
    public void testSchlumpfHinterFliege() throws Exception {
        String stellung = "BLAU-A:0, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:4, Doc:6";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:4:Z", "Doc:6");

        trace.add(moveMsg, "BLAU-A", 5);
        z.bewegeFigur("BLAU-A", 5);
        evaluate(z, "BLAU-A:5:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:4:Z", "Doc:6");

    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege wird auf ein Feld
     * gezogen, auf dem sich ein Schlumpf befindet. Der Schlumpf muss zum Zombie
     * werden!
     * @author MG
     */
    @Test
    @TestDescription("Ziehen der Fliege auf das Feld eines Schlumpfs.")
    public void testFliegeAufSchlumpf() throws Exception {
        String stellung = "BLAU-A:5, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:4, Doc:7";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:4:Z", "Doc:7");

        trace.add(moveMsg, "Bzz", 1);
        z.bewegeFigur("Bzz", 1);
        evaluate(z, "BLAU-A:5:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");

    }

    /**
     * Ueberprueft, ob die richtige Farbe den Zug hat. Da GELB die Fliege bewegt
     * hat, muss jetzt ROT am Zug sein.
     * @author DD
     */
    @Test
    @TestDescription("Nachdem ein Spieler die Fliege bewegt hat, ist der naechste Spieler am Zug.")
    public void testFarbeAmZugNachBewegungDerFliege() throws Exception {
        String stellung = "GELB-A:5, GELB-B:6, GELB-C:36, GELB-D:18, ROT-A:10, ROT-B:5, ROT-C:5, ROT-D:0, Bzz:4, Doc:6";

        trace.add(ConstrMsg + "(\"%s\", %s, %s)", stellung, Farbe.GELB, Farbe.ROT);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.GELB, Farbe.ROT);

        trace.add(moveMsg, "Bzz", 1);
        z.bewegeFigur("Bzz", 1);

        Farbe anZug = z.getFarbeAmZug();
        trace.add(PassTrace.ifEquals("Der zweite Spieler ist am Zug.", Farbe.ROT, anZug));
        assertFalse("Falsche Farbe am Zug.", trace.hasOccurrences());
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege wird auf ein Feld
     * gezogen, auf dem sich mehrere Schluempfe befindet. Alle Schluempfe
     * muessen zu Zombies werden!
     * @author MG
     */
    @Test
    @TestDescription("Ziehen der Fliege auf ein Feld, auf dem sich mehrere Schluempfe befindet.")
    public void testFliegeAufSchluempfe() throws Exception {
        String stellung = "BLAU-A:5, BLAU-B:5, BLAU-C:5, BLAU-D:0, Bzz:4, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:5", "BLAU-B:5", "BLAU-C:5", "BLAU-D:0", "Bzz:4:Z", "Doc:29");

        trace.add(moveMsg, "Bzz", 1);
        z.bewegeFigur("Bzz", 1);
        evaluate(z, "BLAU-A:5:Z", "BLAU-B:5:Z", "BLAU-C:5:Z", "BLAU-D:0", "Bzz:5:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Die Fliege wird hinter ein
     * Feld gezogen, auf dem sich ein Schlumpf befindet. Der Schlumpf darf nicht zum
     * Zombie werden!
     * @author MG
     */
    @Test
    @TestDescription("Ziehen der Fliege ueber das Feld eines Schlumpfs hinweg.")
    public void testFliegeHinterSchlumpf() throws Exception {
        String stellung = "BLAU-A:5, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:4, Doc:7";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:4:Z", "Doc:7");

        trace.add(moveMsg, "Bzz", 2);
        z.bewegeFigur("Bzz", 2);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:6:Z", "Doc:7");

    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Schlumpf (kein Zombie) zieht
     * auf das Feld des Oberschlumpfs. Der Zustand des Schlumpfs darf sich nicht
     * aendern!
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf auf das Feld des Oberschlumpfs ziehen.")
    public void testSchlumpfAufOberschlumpf() throws Exception {
        String stellung = "BLAU-A:5, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:7";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:5", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:7");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:7", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:7");

    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf zieht auf das
     * Feld der Fliege und wird zum Zombie. Danach zieht der Schlumpf auf das
     * Feld des Oberschlumpfs und wird geheilt. Der Schlumpf darf jetzt kein
     * Zombie mehr sein!
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf wird durch die Fliege zum Zombie und durch den Oberschlumpf wieder geheilt.")
    public void testZombieAufOberschlumpf() throws Exception {
        String stellung = "BLAU-A:4, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:5, Doc:7";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:4", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:5:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");

        trace.add(moveMsg, "BLAU-A", 2);
        z.bewegeFigur("BLAU-A", 2);
        evaluate(z, "BLAU-A:7", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");

    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf zieht auf das
     * Feld der Fliege und wird zum Zombie. Danach zieht der Schlumpf auf das
     * Feld hinter dem des Oberschlumpfs und wird auf dem Weg geheilt. Der
     * Schlumpf darf jetzt kein Zombie mehr sein!
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf wird durch die Fliege zum Zombie und durch den Oberschlumpf wieder geheilt.")
    public void testZombieHinterOberschlumpf() throws Exception {
        String stellung = "BLAU-A:4, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:5, Doc:7";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:4", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");

        trace.add(moveMsg, "BLAU-A", 1);
        z.bewegeFigur("BLAU-A", 1);
        evaluate(z, "BLAU-A:5:Z", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");

        trace.add(moveMsg, "BLAU-A", 3);
        z.bewegeFigur("BLAU-A", 3);
        evaluate(z, "BLAU-A:15", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:5:Z", "Doc:7");

    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf befindet sich
     * auf Feld 7 und zieht 4 Felder weiter. Der Schlumpf muss sich jetzt auf
     * Feld 18 befinden.
     * @author MG
     */
    @Test
    @TestDescription("Ziehen ueber das Feld 15 (Zugfolge 7-15-16-17-18).")
    public void testZugrichtungFeld_7_15_16() throws Exception {
        String stellung = "BLAU-A:7, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:7", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 4);
        z.bewegeFigur("BLAU-A", 4);
        evaluate(z, "BLAU-A:18", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf befindet sich
     * auf Feld 14 und zieht 4 Felder weiter. Der Schlumpf muss sich jetzt auf
     * Feld 18 befinden.
     * @author MG
     */
    @Test
    @TestDescription("Ziehen ueber das Feld 15 (Zugfolge 14-15-16-17-18).")
    public void testZugrichtungFeld_14_15_16() throws Exception {
        String stellung = "BLAU-A:14, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:14", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "BLAU-A", 4);
        z.bewegeFigur("BLAU-A", 4);
        evaluate(z, "BLAU-A:18", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Standard Spielstellung, eine Spielerfarbe. Der Spieler versucht den
     * Oberschlumpf zu bewegen. Der Oberschlumpf darf sein Feld nicht verlassen
     * haben!
     * @author MG
     */
    @Test
    @TestDescription("Versuch den Oberschlumpf zu bewegen.")
    public void testBewegeOberschlumpf() throws Exception {
        trace.add(ConstrMsg + "(%s)", Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(Farbe.BLAU);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveMsg, "Doc", 4);
        z.bewegeFigur("Doc", 4);
        evaluate(z, "BLAU-A:0", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf wird vom Feld
     * 28 mit Augenzahl 4 ins Dorf (Feld 36) gezogen. Der Schlumpf muss dabei
     * das Oberschlumpffeld 29 ueberschreiten und bei Feld 31 abzweigen.
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf ueber das Feld des Oberschlumpfs ins Dorf ziehen.")
    public void testSchlumpfInsDorf() throws Exception {
        String stellung = "BLAU-A:28, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:28", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveDirMsg, "BLAU-A", 4, Richtung.ABZWEIGEN);
        z.bewegeFigur("BLAU-A", 4, Richtung.ABZWEIGEN);
        evaluate(z, "BLAU-A:36", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Geaenderte Spielstellung, eine Spielerfarbe. Der Schlumpf wird vom Feld
     * 30 mit Augenzahl 4 ins Dorf (Feld 36) gezogen. Der Schlumpf muss dabei
     * bei Feld 31 abzweigen.
     * @author MG
     */
    @Test
    @TestDescription("Schlumpf ueber Verzweigungsfeld 31 ins Dorf ziehen.")
    public void testSchlumpfInsDorf2() throws Exception {
        String stellung = "BLAU-A:30, BLAU-B:0, BLAU-C:0, BLAU-D:0, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s)", stellung, Farbe.BLAU);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU);
        evaluate(z, "BLAU-A:30", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");

        trace.add(moveDirMsg, "BLAU-A", 4, Richtung.ABZWEIGEN);
        z.bewegeFigur("BLAU-A", 4, Richtung.ABZWEIGEN);
        evaluate(z, "BLAU-A:36", "BLAU-B:0", "BLAU-C:0", "BLAU-D:0", "Bzz:20:Z", "Doc:29");
    }

    /**
     * Konstruktoraufruf mit geaenderter Figurenstellung und zwei Spielerfarben.
     * Alle Figuren muessen sich auf den festgelegten Feldern befinden. BLAU
     * gewinnt das Spiel.
     * @author DD
     */
    @Test
    @TestDescription("Alle Figuren von Spieler BLAU befinden sich im Dorf.")
    public void testGewinnerEinfach() throws Exception {
        String stellung = "BLAU-A:36, BLAU-B:36, BLAU-C:36, BLAU-D:36, ROT-A:10, ROT-B:5, ROT-C:5, ROT-D:0, Bzz:4, Doc:6";

        trace.add(ConstrMsg + "(\"%s\", %s, %s)", stellung, Farbe.BLAU, Farbe.ROT);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU, Farbe.ROT);

        Farbe gewinner = z.gewinner();
        trace.add(PassTrace.ifEquals("Gewinner des Spiels.", Farbe.BLAU, gewinner));
        assertFalse("Gewinner des Spiels inkorrekt.", trace.hasOccurrences());
    }

    /**
     * Geaenderte Spielstellung, zwei Spielerfarben (BLAU und GELB). Die Figuren
     * werden abwechselnd in das Dorf (Feld 36) gezogen. BLAU gewinnt das Spiel.
     * Nachden der Gewinner feststeht, dart keine Figur mehr gezogen werden
     * koennen.
     * @author MG
     */
    @Test
    @TestDescription("Zwei Spieler ziehen ihre Figuren abwechselnd ins Dorf.")
    public void testGewinner() throws Exception {
        String[] name = {"BLAU-A", "GELB-A", "BLAU-B", "GELB-B", "BLAU-C", "GELB-C"};
        String[] end = {"BLAU-A:36", "GELB-A:36", "BLAU-B:36", "GELB-B:36", "BLAU-C:36", "GELB-C:36", "BLAU-D:36",
                "GELB-D:28", "Bzz:20:Z", "Doc:29"};
        int[] augenzahl = {2, 2, 4, 4, 2, 2};

        String stellung = "BLAU-A:30, BLAU-B:28, BLAU-C:30, BLAU-D:28, "
                + "GELB-A:30, GELB-B:28, GELB-C:30, GELB-D:28, Bzz:20, Doc:29";

        trace.add(ConstrMsg + "(\"%s\", %s, %s)", stellung, Farbe.BLAU, Farbe.GELB);
        IZombieSchluempfe z = new ZombieSchluempfe(stellung, Farbe.BLAU, Farbe.GELB);
        evaluate(z, "BLAU-A:30", "GELB-A:30", "BLAU-B:28", "GELB-B:28", "BLAU-C:30",
                "GELB-C:30", "BLAU-D:28", "GELB-D:28", "Bzz:20:Z", "Doc:29");

        evaluate("Es darf noch keinen Gewinner geben.", null, z.gewinner());
        trace.add("Bewegen der Figuren in Richtung Ziel.");
        ITraceRules subtrace = trace.newSubtrace(opt);
        subtrace.enumeration(new Enumeration(0, Enumeration.letters));
        for (int i = 0; i < augenzahl.length; i++) {
            subtrace.add(moveDirMsg, name[i], augenzahl[i], Richtung.ABZWEIGEN);
            z.bewegeFigur(name[i], augenzahl[i], Richtung.ABZWEIGEN);
            subtrace.addInfo(PassTrace.ifNull("Es darf noch keinen Gewinner geben.", z.gewinner()));
        }
        trace.add(subtrace);
        trace.add(moveDirMsg, "BLAU-D", 4, Richtung.ABZWEIGEN);
        z.bewegeFigur("BLAU-D", 4, Richtung.ABZWEIGEN);

        evaluate(z, end);
        evaluate("BLAU muss der Gewinner sein.", Farbe.BLAU, z.gewinner());

        trace.add(moveDirMsg, "GELB-D", 4, Richtung.ABZWEIGEN);
        z.bewegeFigur("GELB-D", 4, Richtung.ABZWEIGEN);

        evaluate(z, end);
        evaluate("BLAU ist noch immer der Gewinner.", Farbe.BLAU, z.gewinner());
    }

    // -----------------------------------------------------------------

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

    private void evaluate(String msg, Farbe erwartet, Farbe gewinner) {
        trace.add(PassTrace.ifEquals(msg, erwartet, gewinner));
        assertFalse("Gewinner ist nicht korrekt.", trace.hasOccurrences());
    }

}
