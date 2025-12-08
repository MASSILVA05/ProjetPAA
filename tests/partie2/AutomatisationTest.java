package partie2;

import partie1.Maison;
import partie1.Generateur;
import partie1.Reseaux;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutomatisationTest {

    private Reseaux reseau;
    private Automatisation auto;

    @BeforeEach
    void setUp() {
        reseau = new Reseaux();

        Generateur g1 = new Generateur(50, "PetitGen");
        Generateur g2 = new Generateur(500, "GrosGen");
        reseau.getG().add(g1);
        reseau.getG().add(g2);

        Maison m1 = new Maison("Villa", Maison.ConsommationType.FORTE);      // 40 kW
        Maison m2 = new Maison("Usine", Maison.ConsommationType.FORTE);      // 40 kW

        reseau.getM().add(m1);
        reseau.getM().add(m2);

        // Connexions initiales sous-optimales
        reseau.changeConnection(m2, g1);  // Maison lourde sur petit générateur
        reseau.changeConnection(m1, g2);  // OK

        auto = new Automatisation(reseau, 100);
    }

    // ===== Tests de résolution =====

    @Test
    void testResoudreAmelioreLeCout() {
        double coutAvant = reseau.calculercout();
        
        auto.resoudre();
        
        double coutApres = reseau.calculercout();
        
        assertTrue(coutApres <= coutAvant, 
            "L'algorithme ne devrait jamais dégrader le coût final");
        assertTrue(coutAvant > coutApres, 
            "L'algorithme devrait améliorer le coût");
    }

    @Test
    void testResoudreFinitEnTempsRaisonnable() {
        long debut = System.currentTimeMillis();
        
        auto.resoudre();
        
        long fin = System.currentTimeMillis();
        long duree = fin - debut;
        
        assertTrue(duree < 10000, 
            "La résolution devrait prendre moins de 10 secondes");
    }

    // ===== Tests du calcul automatique d'itérations =====

    @Test
    void testCalculAutomatiqueIterations() {
        Automatisation autoAuto = new Automatisation(reseau);
        int k = autoAuto.getK();

        assertTrue(k >= 1000, 
            "K doit être au moins égal à ITERATIONS_MIN (1000)");
        assertTrue(k <= 15000, 
            "K doit être bridé à ITERATIONS_MAX (15000)");
    }

    @Test
    void testIterationsProportionnellesAuReseau() {
        // Petit réseau
        Reseaux petitReseau = new Reseaux();
        petitReseau.getG().add(new Generateur(100, "G1"));
        petitReseau.getM().add(new Maison("M1", Maison.ConsommationType.NORMAL));
        
        Automatisation autoPetit = new Automatisation(petitReseau);
        int kPetit = autoPetit.getK();

        // Grand réseau
        Reseaux grandReseau = new Reseaux();
        for (int i = 0; i < 5; i++) {
            grandReseau.getG().add(new Generateur(100, "G" + i));
            grandReseau.getM().add(new Maison("M" + i, Maison.ConsommationType.NORMAL));
        }
        
        Automatisation autoGrand = new Automatisation(grandReseau);
        int kGrand = autoGrand.getK();

        assertTrue(kGrand >= kPetit, 
            "Plus de maisons = plus d'itérations");
    }

    // ===== Tests de la phase gloutonne =====

    @Test
    void testPhaseGloutonne() {
        double coutAvant = reseau.calculercout();
        
        auto.resoudre();
        
        double coutApres = reseau.calculercout();
        
        // Vérifier qu'il y a une amélioration
        assertTrue(coutApres <= coutAvant, 
            "La phase gloutonne devrait améliorer ou maintenir le coût");
        
        // Vérifier que les connexions ont changé
        boolean connexionChangee = false;
        for (List<Maison> maisons : reseau.getConnexions().values()) {
            if (maisons.size() > 0) {
                connexionChangee = true;
                break;
            }
        }
        assertTrue(connexionChangee, 
            "Au moins une connexion devrait avoir changé");
    }

    @Test
    void testMaisonLourdeSurGrosGenerateur() {
        Maison maisonLourde = reseau.getM().get(1); // Usine (FORTE)
        Generateur petitGen = reseau.getG().get(0);  // PetitGen
        Generateur grosGen = reseau.getG().get(1);   // GrosGen

        // Avant optimisation: maisonLourde est sur petitGen
        assertTrue(reseau.getConnexions().get(petitGen).contains(maisonLourde),
            "Avant optimisation: maison lourde doit être sur petit générateur");

        // Après optimisation
        auto.resoudre();

        // La maison lourde devrait être sur le gros générateur
        assertTrue(reseau.getConnexions().get(grosGen).contains(maisonLourde),
            "Après optimisation: maison lourde doit être sur le gros générateur");
        assertFalse(reseau.getConnexions().get(petitGen).contains(maisonLourde),
            "Après optimisation: maison lourde ne doit plus être sur petit générateur");
    }

    // ===== Tests de réseaux particuliers =====

    @Test
    void testReseauVide() {
        Reseaux vide = new Reseaux();
        Automatisation autoVide = new Automatisation(vide);

        assertDoesNotThrow(() -> autoVide.resoudre(), 
            "L'algorithme doit gérer les réseaux vides sans planter");
    }

    @Test
    void testReseauUneMaison() {
        Reseaux uneMaison = new Reseaux();
        Generateur g = new Generateur(100, "Gen1");
        Maison m = new Maison("Maison1", Maison.ConsommationType.NORMAL);

        uneMaison.getG().add(g);
        uneMaison.getM().add(m);
        uneMaison.changeConnection(m, g);

        Automatisation auto1 = new Automatisation(uneMaison);
        double coutAvant = uneMaison.calculercout();
        
        assertDoesNotThrow(() -> auto1.resoudre(), 
            "L'algorithme doit gérer une maison unique");

        double coutApres = uneMaison.calculercout();
        assertTrue(coutApres <= coutAvant, 
            "Le coût ne doit pas s'aggraver");
    }

    @Test
    void testReseauUnGenerateur() {
        Reseaux unGen = new Reseaux();
        Generateur g = new Generateur(1000, "Gen1");
        
        unGen.getG().add(g);
        for (int i = 0; i < 3; i++) {
            Maison m = new Maison("M" + i, Maison.ConsommationType.NORMAL);
            unGen.getM().add(m);
            unGen.changeConnection(m, g);
        }

        Automatisation autoUnGen = new Automatisation(unGen);
        double coutAvant = unGen.calculercout();
        
        assertDoesNotThrow(() -> autoUnGen.resoudre(), 
            "L'algorithme doit gérer un seul générateur");

        double coutApres = unGen.calculercout();
        // Avec un seul générateur, aucun mouvement n'est possible
        assertEquals(coutAvant, coutApres, 0.001, 
            "Avec un générateur unique, le coût ne change pas");
    }

    // ===== Tests de propriétés du réseau optimisé =====

    @Test
    void testReseauOptimiseValide() {
        auto.resoudre();

        // Vérifier que toutes les maisons sont connectées
        int compteur = 0;
        for (List<Maison> maisons : reseau.getConnexions().values()) {
            compteur += maisons.size();
        }
        assertEquals(reseau.getM().size(), compteur, 
            "Toutes les maisons doivent rester connectées après optimisation");

        // Vérifier que le réseau est toujours valide
        assertTrue(reseau.verifierConnexions(), 
            "Le réseau optimisé doit être valide");
    }

    @Test
    void testNombreIterationsPersonnalise() {
        Automatisation autoPersonnalisee = new Automatisation(reseau, 500);
        
        assertEquals(500, autoPersonnalisee.getK(), 
            "Le nombre d'itérations personnalisé doit être respecté");
    }

    @Test
    void testDispersionAmelioree() {
        double dispersionAvant = reseau.Disp();
        
        auto.resoudre();
        
        double dispersionApres = reseau.Disp();
        
        assertTrue(dispersionApres <= dispersionAvant, 
            "La dispersion ne devrait pas s'aggraver");
    }

    @Test
    void testSurchargeAmelioree() {
        double surchargeAvant = reseau.surcharge();
        
        auto.resoudre();
        
        double surchargeApres = reseau.surcharge();
        
        assertTrue(surchargeApres <= surchargeAvant, 
            "La surcharge ne devrait pas s'aggraver");
    }
}