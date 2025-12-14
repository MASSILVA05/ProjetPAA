package partie1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaires pour la classe {@link Reseaux}.
 *
 * <p>
 * Cette classe vérifie l'ensemble des fonctionnalités du réseau électrique :
 * </p>
 * <ul>
 *   <li>Initialisation du réseau</li>
 *   <li>Ajout de maisons et de générateurs</li>
 *   <li>Gestion des connexions</li>
 *   <li>Validation du réseau</li>
 *   <li>Calculs énergétiques (taux d'utilisation, surcharge, dispersion)</li>
 *   <li>Calcul du coût global</li>
 *   <li>Copie du réseau</li>
 *   <li>Influence du paramètre lambda sur le coût</li>
 * </ul>
 *
 * <p>
 * Les entrées utilisateur sont simulées à l'aide de {@link ByteArrayInputStream}
 * afin de tester les méthodes utilisant {@link Scanner} sans interaction réelle.
 * </p>
 */
public class ReseauxTest {

    /** Instance du réseau testée avant chaque test */
    private Reseaux reseau;

    /**
     * Initialise un nouveau réseau vide avant chaque test.
     */
    @BeforeEach
    void setUp() {
        reseau = new Reseaux();
    }

    // ===== Tests d'initialisation =====

    /**
     * Vérifie que le réseau est correctement initialisé :
     * toutes les structures internes doivent exister
     * et être vides au départ.
     */
    @Test
    void testInitialisationVide() {
        assertNotNull(reseau.getM(), "La liste des maisons ne doit pas être null");
        assertNotNull(reseau.getG(), "La liste des générateurs ne doit pas être null");
        assertNotNull(reseau.getConnexions(), "La map des connexions ne doit pas être null");
        assertTrue(reseau.getM().isEmpty(), "La liste des maisons doit être vide au départ");
        assertTrue(reseau.getG().isEmpty(), "La liste des générateurs doit être vide au départ");
        assertTrue(reseau.getConnexions().isEmpty(), "La map des connexions doit être vide au départ");
    }

    // ===== Tests d'ajout de maisons =====

    /**
     * Vérifie l'ajout d'une maison via une saisie simulée
     * avec un type de consommation NORMAL.
     */
    @Test
    void testAjouterMaisonViaScanner() {
        String input = "Villa\nNORMAL\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Reseaux.sc = new Scanner(in);

        reseau.ajouterMaison();

        assertEquals(1, reseau.getM().size());
        assertEquals("Villa", reseau.getM().get(0).getnom());
        assertEquals(20, reseau.getM().get(0).getcons(), "NORMAL vaut 20");
    }

    /**
     * Vérifie l'ajout d'une maison avec une consommation BASSE.
     */
    @Test
    void testAjouterMaisonBasse() {
        String input = "Maison1\nBASSE\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Reseaux.sc = new Scanner(in);

        reseau.ajouterMaison();

        assertEquals(1, reseau.getM().size());
        assertEquals("Maison1", reseau.getM().get(0).getnom());
        assertEquals(10, reseau.getM().get(0).getcons(), "BASSE vaut 10");
    }

    /**
     * Vérifie l'ajout d'une maison avec une consommation FORTE.
     */
    @Test
    void testAjouterMaisonForte() {
        String input = "Maison2\nFORTE\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Reseaux.sc = new Scanner(in);

        reseau.ajouterMaison();

        assertEquals(1, reseau.getM().size());
        assertEquals("Maison2", reseau.getM().get(0).getnom());
        assertEquals(40, reseau.getM().get(0).getcons(), "FORTE vaut 40");
    }

    // ===== Tests d'ajout de générateurs =====

    /**
     * Vérifie l'ajout d'un générateur via une saisie simulée.
     */
    @Test
    void testAjouterGenerateurViaScanner() {
        String input = "Nucleaire\n5000\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Reseaux.sc = new Scanner(in);

        reseau.ajoutergenerateur();

        assertEquals(1, reseau.getG().size());
        assertEquals("Nucleaire", reseau.getG().get(0).getnom());
        assertEquals(5000, reseau.getG().get(0).getcap());
    }

    /**
     * Vérifie l'ajout successif de plusieurs générateurs.
     */
    @Test
    void testAjouterPlusieursGenerateurs() {
        String input1 = "Gen1\n100\n";
        Reseaux.sc = new Scanner(new ByteArrayInputStream(input1.getBytes()));
        reseau.ajoutergenerateur();

        String input2 = "Gen2\n200\n";
        Reseaux.sc = new Scanner(new ByteArrayInputStream(input2.getBytes()));
        reseau.ajoutergenerateur();

        assertEquals(2, reseau.getG().size());
        assertEquals("Gen1", reseau.getG().get(0).getnom());
        assertEquals("Gen2", reseau.getG().get(1).getnom());
    }

    // ===== Tests de changement de connexion =====

    /**
     * Vérifie qu'une maison peut changer de générateur
     * et que les connexions sont mises à jour correctement.
     */
    @Test
    void testChangeConnection() {
        Generateur gen1 = new Generateur(100, "Gen1");
        Generateur gen2 = new Generateur(100, "Gen2");
        Maison maison = new Maison("MaMaison", Maison.ConsommationType.NORMAL);

        reseau.getG().add(gen1);
        reseau.getG().add(gen2);
        reseau.getM().add(maison);

        reseau.getConnexions().put(gen1, new ArrayList<>());
        reseau.getConnexions().get(gen1).add(maison);

        reseau.changeConnection(maison, gen2);

        assertFalse(reseau.getConnexions().get(gen1).contains(maison));
        assertTrue(reseau.getConnexions().get(gen2).contains(maison));
    }

    // ===== Tests de validation des connexions =====

    /**
     * Vérifie que des connexions sont invalides
     * si le réseau est vide.
     */
    @Test
    void testVerifierConnexionsVide() {
        assertFalse(reseau.verifierConnexions());
    }

    /**
     * Vérifie que les connexions sont valides
     * lorsqu'une maison est correctement connectée.
     */
    @Test
    void testVerifierConnexionsValides() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.NORMAL);

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);

        assertTrue(reseau.verifierConnexions());
    }

    /**
     * Vérifie que les connexions sont invalides
     * lorsqu'une maison n'est reliée à aucun générateur.
     */
    @Test
    void testVerifierConnexionsMaisonNonConnectee() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison1 = new Maison("Maison1", Maison.ConsommationType.NORMAL);
        Maison maison2 = new Maison("Maison2", Maison.ConsommationType.NORMAL);

        reseau.getG().add(gen);
        reseau.getM().add(maison1);
        reseau.getM().add(maison2);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison1);

        assertFalse(reseau.verifierConnexions());
    }

    // ===== Tests énergétiques =====

    /**
     * Vérifie le calcul du taux d'utilisation d'un générateur.
     */
    @Test
    void testTauxUtilisation() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.NORMAL);

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);

        assertEquals(0.2, reseau.tauxutilisation(gen), 0.01);
    }

    /**
     * Vérifie que le taux d'utilisation est nul
     * lorsqu'aucune maison n'est connectée.
     */
    @Test
    void testTauxUtilisationGenerateurVide() {
        Generateur gen = new Generateur(100, "Gen1");
        reseau.getG().add(gen);

        assertEquals(0.0, reseau.tauxutilisation(gen), 0.01);
    }

    // ===== Tests de dispersion et surcharge =====

    /**
     * Vérifie que la dispersion est nulle pour un réseau vide
     * ou contenant un seul générateur.
     */
    @Test
    void testDispersionReseauVide() {
        assertEquals(0.0, reseau.Disp(), 0.01);
    }

    /**
     * Vérifie que la surcharge est positive
     * lorsque la consommation dépasse la capacité.
     */
    @Test
    void testSurchargeDepassement() {
        Generateur gen = new Generateur(100, "Gen1");

        reseau.getG().add(gen);
        reseau.getConnexions().put(gen, new ArrayList<>());

        reseau.getConnexions().get(gen).add(new Maison("M1", Maison.ConsommationType.FORTE));
        reseau.getConnexions().get(gen).add(new Maison("M2", Maison.ConsommationType.FORTE));
        reseau.getConnexions().get(gen).add(new Maison("M3", Maison.ConsommationType.FORTE));
        reseau.getConnexions().get(gen).add(new Maison("M4", Maison.ConsommationType.FORTE));

        assertTrue(reseau.surcharge() > 0.0);
    }

    // ===== Tests de coût et de copie =====

    /**
     * Vérifie que le coût est nul pour un réseau vide.
     */
    @Test
    void testCalculerCoutReseauVide() {
        assertEquals(0.0, reseau.calculercout(), 0.01);
    }

    /**
     * Vérifie que la méthode {@code copier()}
     * crée un réseau distinct mais équivalent.
     */
    @Test
    void testCopierReseau() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.NORMAL);

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);

        Reseaux copie = reseau.copier();

        assertEquals(reseau.getM().size(), copie.getM().size());
        assertEquals(reseau.getG().size(), copie.getG().size());
        assertEquals(reseau.getConnexions().size(), copie.getConnexions().size());
    }

    /**
     * Vérifie l'influence du paramètre {@code lambda}
     * sur le coût total du réseau.
     */
    @Test
    void testSetLambda() {
        Reseaux.setLambda(5.0);
        double cout1 = reseau.calculercout();

        Reseaux.setLambda(10.0);
        double cout2 = reseau.calculercout();

        assertTrue(cout2 >= cout1);

        Reseaux.setLambda(10.0); // restauration
    }
}
