package partie1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaires pour la classe {@link Generateur}.
 *
 * <p>
 * Cette classe vérifie le bon fonctionnement des éléments fondamentaux
 * d'un générateur électrique :
 * </p>
 * <ul>
 *   <li>Le constructeur</li>
 *   <li>Les méthodes d'accès (getters)</li>
 *   <li>La méthode de modification de la capacité</li>
 *   <li>La représentation textuelle via {@code toString()}</li>
 * </ul>
 *
 * <p>
 * Ces tests permettent de garantir que les données internes du générateur
 * sont correctement initialisées, modifiées et affichées.
 * </p>
 */
public class GenerateurTest {

    /**
     * Vérifie que le constructeur de {@link Generateur}
     * initialise correctement le nom et la capacité.
     *
     * <p>
     * Le test contrôle que les valeurs passées en paramètres
     * sont bien accessibles via les méthodes {@code getcap()} et {@code getnom()}.
     * </p>
     */
    @Test
    void testConstructeurEtGetters() {

        int capaciteAttendue = 100;
        String nomAttendu = "Générateur Principal";

        Generateur gen = new Generateur(capaciteAttendue, nomAttendu);

        assertEquals(capaciteAttendue, gen.getcap(),
                "La capacité initiale doit correspondre à celle passée au constructeur");
        assertEquals(nomAttendu, gen.getnom(),
                "Le nom doit correspondre à celui passé au constructeur");
    }

    /**
     * Vérifie que la méthode {@code setCap(int)}
     * met correctement à jour la capacité du générateur.
     *
     * <p>
     * Le test s'assure que la nouvelle valeur
     * est bien reflétée par la méthode {@code getcap()}.
     * </p>
     */
    @Test
    void testSetCap() {

        Generateur gen = new Generateur(50, "Générateur Secours");
        int nouvelleCapacite = 75;

        gen.setCap(nouvelleCapacite);

        assertEquals(nouvelleCapacite, gen.getcap(),
                "La capacité doit être mise à jour après l'appel à setCap");
    }

    /**
     * Vérifie que la méthode {@code toString()}
     * retourne une représentation textuelle conforme au format attendu.
     *
     * <p>
     * Le format attendu est :
     * </p>
     * <pre>
     * Nom (capacité kW)
     * </pre>
     *
     * <p>
     * Exemple :
     * </p>
     * <pre>
     * Générateur Solaire (120 kW)
     * </pre>
     */
    @Test
    void testToString() {

        Generateur gen = new Generateur(120, "Générateur Solaire");
        String resultatAttendu = "Générateur Solaire (120 kW)";

        String resultatObtenu = gen.toString();

        assertEquals(resultatAttendu, resultatObtenu,
                "La méthode toString ne retourne pas le format attendu");
    }
}
