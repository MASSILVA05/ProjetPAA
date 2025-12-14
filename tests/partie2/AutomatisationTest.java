package partie2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import partie1.Generateur;
import partie1.Maison;
import partie1.Reseaux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaires pour la classe {@link Automatisation}.
 *
 * <p>
 * Cette classe vérifie le bon fonctionnement de l'algorithme
 * d'automatisation et d'optimisation du réseau électrique.
 * </p>
 *
 * <p>
 * Les tests portent notamment sur :
 * </p>
 * <ul>
 *   <li>Les constructeurs de la classe Automatisation</li>
 *   <li>Le bon déroulement de l'algorithme {@code resoudre()}</li>
 *   <li>La non-dégradation des critères d'optimisation
 *       (coût, dispersion, surcharge)</li>
 *   <li>La validité des connexions après optimisation</li>
 *   <li>La robustesse face aux cas limites</li>
 *   <li>La possibilité de modifier les connexions</li>
 *   <li>La gestion du nombre d'itérations</li>
 * </ul>
 *
 * <p>
 * Les réseaux utilisés dans les tests sont construits manuellement
 * afin de garantir des configurations initiales contrôlées,
 * parfois volontairement sous-optimales.
 * </p>
 */
class AutomatisationTest {

    /** Réseau utilisé pour les tests */
    private Reseaux reseau;

    /** Générateurs du réseau */
    private Generateur g1, g2;

    /** Maisons du réseau */
    private Maison m1, m2, m3;

    /**
     * Initialise un réseau de test avant chaque méthode.
     *
     * <p>
     * Le réseau contient :
     * </p>
     * <ul>
     *   <li>Deux générateurs de capacités différentes</li>
     *   <li>Trois maisons avec des consommations variées</li>
     *   <li>Des connexions initiales valides mais non optimales
     *       (surcharge volontaire)</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        reseau = new Reseaux();

        // Générateurs
        g1 = new Generateur(50, "G1");
        g2 = new Generateur(100, "G2");

        reseau.getG().add(g1);
        reseau.getG().add(g2);

        // Maisons
        m1 = new Maison("M1", Maison.ConsommationType.BASSE);   // 10
        m2 = new Maison("M2", Maison.ConsommationType.NORMAL);  // 20
        m3 = new Maison("M3", Maison.ConsommationType.FORTE);   // 40

        reseau.getM().add(m1);
        reseau.getM().add(m2);
        reseau.getM().add(m3);

        // Connexions initiales manuelles
        reseau.getConnexions().put(g1, new ArrayList<>());
        reseau.getConnexions().put(g2, new ArrayList<>());

        reseau.getConnexions().get(g1).add(m1);
        reseau.getConnexions().get(g1).add(m3); // surcharge volontaire
        reseau.getConnexions().get(g2).add(m2);
    }

    // ===== Tests des constructeurs =====

    /**
     * Vérifie que le constructeur par défaut
     * initialise correctement l'objet Automatisation.
     */
    @Test
    void constructeurParDefaut() {
        Automatisation auto = new Automatisation(reseau);
        assertNotNull(auto);
    }

    /**
     * Vérifie que le constructeur avec un nombre
     * d'itérations personnalisé fonctionne correctement.
     */
    @Test
    void constructeurAvecIterations() {
        Automatisation auto = new Automatisation(reseau, 5000);
        assertEquals(5000, auto.getK());
    }

    // ===== Tests de l'algorithme global =====

    /**
     * Vérifie que l'algorithme d'optimisation
     * n'augmente jamais le coût total du réseau.
     */
    @Test
    void resoudreNeFaitPasAugmenterLeCout() {
        double coutAvant = reseau.calculercout();

        Automatisation auto = new Automatisation(reseau);
        auto.resoudre();

        double coutApres = reseau.calculercout();
        assertTrue(coutApres <= coutAvant);
    }

    /**
     * Vérifie que la dispersion du réseau
     * ne s'aggrave pas après l'optimisation.
     */
    @Test
    void resoudreNeFaitPasAugmenterLaDispersion() {
        double dispAvant = reseau.Disp();

        Automatisation auto = new Automatisation(reseau);
        auto.resoudre();

        double dispApres = reseau.Disp();
        assertTrue(dispApres <= dispAvant);
    }

    /**
     * Vérifie que la surcharge globale du réseau
     * ne s'aggrave pas après l'optimisation.
     */
    @Test
    void resoudreNeFaitPasAugmenterLaSurcharge() {
        double surchargeAvant = reseau.surcharge();

        Automatisation auto = new Automatisation(reseau);
        auto.resoudre();

        double surchargeApres = reseau.surcharge();
        assertTrue(surchargeApres <= surchargeAvant);
    }

    // ===== Tests de validité du réseau =====

    /**
     * Vérifie qu'après optimisation,
     * chaque maison est connectée exactement une fois.
     */
    @Test
    void chaqueMaisonEstConnecteeExactementUneFoisApresOptimisation() {
        Automatisation auto = new Automatisation(reseau);
        auto.resoudre();

        int totalConnexions = 0;
        for (List<Maison> maisons : reseau.getConnexions().values()) {
            totalConnexions += maisons.size();
        }

        assertEquals(reseau.getM().size(), totalConnexions);
    }

    // ===== Tests des cas limites =====

    /**
     * Vérifie que l'algorithme ne provoque
     * aucune exception sur un réseau vide.
     */
    @Test
    void reseauVideNePlantePas() {
        Reseaux vide = new Reseaux();
        Automatisation auto = new Automatisation(vide);

        assertDoesNotThrow(auto::resoudre);
    }

    /**
     * Vérifie que l'algorithme fonctionne
     * avec un réseau contenant une seule maison.
     */
    @Test
    void uneSeuleMaison() {
        Reseaux r = new Reseaux();

        Generateur g = new Generateur(100, "G");
        Maison m = new Maison("M", Maison.ConsommationType.FORTE);

        r.getG().add(g);
        r.getM().add(m);
        r.getConnexions().put(g, new ArrayList<>());
        r.getConnexions().get(g).add(m);

        Automatisation auto = new Automatisation(r);
        assertDoesNotThrow(auto::resoudre);
    }

    /**
     * Vérifie que l'algorithme fonctionne
     * avec un réseau contenant un seul générateur.
     */
    @Test
    void unSeulGenerateur() {
        Reseaux r = new Reseaux();

        Generateur g = new Generateur(60, "G");
        r.getG().add(g);
        r.getConnexions().put(g, new ArrayList<>());

        Automatisation auto = new Automatisation(r);
        assertDoesNotThrow(auto::resoudre);
    }

    // ===== Tests de modification des connexions =====

    /**
     * Vérifie que l'algorithme peut modifier
     * les connexions existantes lors de l'optimisation.
     */
    @Test
    void resoudrePeutModifierLesConnexions() {
        Map<Generateur, List<Maison>> avant =
                reseau.copier().getConnexions();

        Automatisation auto = new Automatisation(reseau);
        auto.resoudre();

        Map<Generateur, List<Maison>> apres =
                reseau.getConnexions();

        assertNotEquals(avant, apres);
    }

    // ===== Tests du nombre d'itérations =====

    /**
     * Vérifie que le nombre d'itérations par défaut
     * se situe dans un intervalle raisonnable.
     */
    @Test
    void nombreIterationsParDefautDansIntervalleValide() {
        Automatisation auto = new Automatisation(reseau);
        int k = auto.getK();

        assertTrue(k >= 2000);
        assertTrue(k <= 50000);
    }
}

