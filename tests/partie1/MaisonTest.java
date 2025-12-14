package partie1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaires pour la classe {@link Maison}.
 *
 * <p>
 * Cette classe vérifie le comportement des différents constructeurs
 * de {@code Maison}, la gestion des types de consommation,
 * ainsi que la cohérence des valeurs internes et de l'affichage.
 * </p>
 *
 * <p>
 * Les tests couvrent :
 * </p>
 * <ul>
 *   <li>La création d'une maison à partir d'un type de consommation (Enum)</li>
 *   <li>La création d'une maison avec une consommation numérique explicite</li>
 *   <li>La modification du type de consommation</li>
 *   <li>Les valeurs associées à chaque type de consommation</li>
 *   <li>La représentation textuelle via {@code toString()}</li>
 * </ul>
 */
public class MaisonTest {

    /**
     * Vérifie que le constructeur utilisant l'énumération
     * {@link Maison.ConsommationType} initialise correctement
     * le nom et la consommation numérique associée.
     *
     * <p>
     * Dans ce test, le type {@code BASSE} doit correspondre
     * à une consommation de 10 kW.
     * </p>
     */
    @Test
    void testConstructeurAvecEnum() {

        String nom = "Maison de Campagne";
        Maison.ConsommationType type = Maison.ConsommationType.BASSE;

        Maison maison = new Maison(nom, type);

        assertEquals("Maison de Campagne", maison.getnom());
        assertEquals(10, maison.getcons(),
                "Le constructeur avec Enum BASSE devrait initialiser la conso à 10");
    }

    /**
     * Vérifie que le constructeur acceptant une consommation
     * numérique explicite initialise correctement les attributs
     * {@code nom} et {@code consommation}.
     */
    @Test
    void testConstructeurAvecInt() {

        int consoManuelle = 85;
        String nom = "Usine";

        Maison maison = new Maison(consoManuelle, nom);

        assertEquals("Usine", maison.getnom());
        assertEquals(85, maison.getcons(),
                "Le constructeur avec int devrait définir la conso exactement");
    }

    /**
     * Vérifie que la méthode {@code setCons(ConsommationType)}
     * met à jour correctement la consommation numérique
     * de la maison en fonction du type choisi.
     *
     * <p>
     * Le type {@code FORTE} doit correspondre à une consommation de 40 kW.
     * </p>
     */
    @Test
    void testSetCons() {

        Maison maison = new Maison("Appartement", Maison.ConsommationType.NORMAL);

        maison.setCons(Maison.ConsommationType.FORTE);

        assertEquals(40, maison.getcons(),
                "setCons devrait mettre à jour la valeur numérique de la consommation");
    }

    /**
     * Vérifie que chaque valeur de l'énumération
     * {@link Maison.ConsommationType} retourne la valeur
     * numérique correcte via {@code getValeur()}.
     */
    @Test
    void testValeursEnum() {

        assertEquals(10, Maison.ConsommationType.BASSE.getValeur());
        assertEquals(20, Maison.ConsommationType.NORMAL.getValeur());
        assertEquals(40, Maison.ConsommationType.FORTE.getValeur());
    }

    /**
     * Vérifie que la méthode {@code toString()}
     * retourne une représentation textuelle correcte
     * de la maison, incluant son nom et sa consommation.
     *
     * <p>
     * Format attendu :
     * </p>
     * <pre>
     * Nom (consommation kW)
     * </pre>
     *
     * <p>
     * Exemple :
     * </p>
     * <pre>
     * Villa (20 kW)
     * </pre>
     */
    @Test
    void testToString() {

        Maison maison = new Maison("Villa", Maison.ConsommationType.NORMAL);
        String resultatAttendu = "Villa (20 kW)";

        String resultatObtenu = maison.toString();

        assertEquals(resultatAttendu, resultatObtenu);
    }
}
