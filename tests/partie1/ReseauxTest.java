package partie1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ReseauxTest {

    private Reseaux reseau;

    @BeforeEach
    void setUp() {
        reseau = new Reseaux();
    }

    // ===== Tests d'initialisation =====
    
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
    
    @Test
    void testAjouterMaisonViaScanner() {
        String input = "Villa\nNORMAL\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        Reseaux.sc = new Scanner(in);

        reseau.ajouterMaison();

        assertEquals(1, reseau.getM().size(), "Une maison aurait dû être ajoutée");
        assertEquals("Villa", reseau.getM().get(0).getnom());
        assertEquals(20, reseau.getM().get(0).getcons(), "NORMAL vaut 20");
    }

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

    @Test
    void testAjouterPlusieursGenerateurs() {
        String input1 = "Gen1\n100\n";
        ByteArrayInputStream in1 = new ByteArrayInputStream(input1.getBytes());
        System.setIn(in1);
        Reseaux.sc = new Scanner(in1);
        reseau.ajoutergenerateur();

        String input2 = "Gen2\n200\n";
        ByteArrayInputStream in2 = new ByteArrayInputStream(input2.getBytes());
        System.setIn(in2);
        Reseaux.sc = new Scanner(in2);
        reseau.ajoutergenerateur();

        assertEquals(2, reseau.getG().size());
        assertEquals("Gen1", reseau.getG().get(0).getnom());
        assertEquals("Gen2", reseau.getG().get(1).getnom());
    }

    // ===== Tests de changement de connexion =====
    
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

        assertFalse(reseau.getConnexions().get(gen1).contains(maison),
            "La maison doit être retirée de l'ancien générateur");

        assertTrue(reseau.getConnexions().get(gen2).contains(maison),
            "La maison doit être ajoutée au nouveau générateur");
    }

    // ===== Tests de vérification des connexions =====
    
    @Test
    void testVerifierConnexionsVide() {
        assertFalse(reseau.verifierConnexions(), "Les connexions doivent être invalides si le réseau est vide");
    }

    @Test
    void testVerifierConnexionsValides() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.NORMAL);

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);

        assertTrue(reseau.verifierConnexions(), "Les connexions doivent être valides");
    }

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

        assertFalse(reseau.verifierConnexions(), "Les connexions doivent être invalides si une maison n'est pas connectée");
    }

    // ===== Tests de taux d'utilisation =====
    
    @Test
    void testTauxUtilisation() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.NORMAL); // 20 kW

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);

        double taux = reseau.tauxutilisation(gen);
        assertEquals(0.2, taux, 0.01, "Le taux d'utilisation devrait être 20/100 = 0.2");
    }

    @Test
    void testTauxUtilisationGenerateurVide() {
        Generateur gen = new Generateur(100, "Gen1");
        reseau.getG().add(gen);

        double taux = reseau.tauxutilisation(gen);
        assertEquals(0.0, taux, 0.01, "Le taux d'utilisation devrait être 0 si aucune maison n'est connectée");
    }

    // ===== Tests de dispersion =====
    
    @Test
    void testDispersionReseauVide() {
        double disp = reseau.Disp();
        assertEquals(0.0, disp, 0.01, "La dispersion doit être 0 pour un réseau vide");
    }

    @Test
    void testDispersionUnGenerateur() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.NORMAL);

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);

        double disp = reseau.Disp();
        assertEquals(0.0, disp, 0.01, "La dispersion doit être 0 avec un seul générateur");
    }

    // ===== Tests de surcharge =====
    
    @Test
    void testSurchargeReseauVide() {
        double surcharge = reseau.surcharge();
        assertEquals(0.0, surcharge, 0.01, "La surcharge doit être 0 pour un réseau vide");
    }

    @Test
    void testSurchargeNormale() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.NORMAL); // 20 kW

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);

        double surcharge = reseau.surcharge();
        assertEquals(0.0, surcharge, 0.01, "La surcharge doit être 0 si consommation < capacité");
    }

    @Test
    void testSurchargeDepassement() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.FORTE); // 30 kW
        Maison maison2 = new Maison("Maison2", Maison.ConsommationType.FORTE); // 30 kW
        Maison maison3 = new Maison("Maison3", Maison.ConsommationType.FORTE); // 30 kW
        Maison maison4 = new Maison("Maison4", Maison.ConsommationType.FORTE); // 30 kW (total 120)

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getM().add(maison2);
        reseau.getM().add(maison3);
        reseau.getM().add(maison4);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);
        reseau.getConnexions().get(gen).add(maison2);
        reseau.getConnexions().get(gen).add(maison3);
        reseau.getConnexions().get(gen).add(maison4);

        double surcharge = reseau.surcharge();
        assertTrue(surcharge > 0.0, "La surcharge doit être positive si consommation > capacité");
    }

    // ===== Tests de coût =====
    
    @Test
    void testCalculerCoutReseauVide() {
        double cout = reseau.calculercout();
        assertEquals(0.0, cout, 0.01, "Le coût doit être 0 pour un réseau sans connexions");
    }

    @Test
    void testCalculerCoutNormal() {
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.NORMAL);

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);

        double cout = reseau.calculercout();
        assertTrue(cout >= 0.0, "Le coût ne doit pas être négatif");
    }

    // ===== Tests de copie =====
    
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

    // ===== Tests du setter Lambda =====
    
    @Test
    void testSetLambda() {
        Reseaux.setLambda(5.0);
        
        Generateur gen = new Generateur(100, "Gen1");
        Maison maison = new Maison("Maison1", Maison.ConsommationType.FORTE);
        Maison maison2 = new Maison("Maison2", Maison.ConsommationType.FORTE);
        Maison maison3 = new Maison("Maison3", Maison.ConsommationType.FORTE);
        Maison maison4 = new Maison("Maison4", Maison.ConsommationType.FORTE);

        reseau.getG().add(gen);
        reseau.getM().add(maison);
        reseau.getM().add(maison2);
        reseau.getM().add(maison3);
        reseau.getM().add(maison4);
        reseau.getConnexions().put(gen, new ArrayList<>());
        reseau.getConnexions().get(gen).add(maison);
        reseau.getConnexions().get(gen).add(maison2);
        reseau.getConnexions().get(gen).add(maison3);
        reseau.getConnexions().get(gen).add(maison4);

        double coutAvec5 = reseau.calculercout();

        Reseaux.setLambda(10.0);
        Reseaux reseau2 = new Reseaux();
        reseau2.getG().add(gen);
        reseau2.getM().add(maison);
        reseau2.getM().add(maison2);
        reseau2.getM().add(maison3);
        reseau2.getM().add(maison4);
        reseau2.getConnexions().put(gen, new ArrayList<>());
        reseau2.getConnexions().get(gen).add(maison);
        reseau2.getConnexions().get(gen).add(maison2);
        reseau2.getConnexions().get(gen).add(maison3);
        reseau2.getConnexions().get(gen).add(maison4);

        double coutAvec10 = reseau2.calculercout();

        assertTrue(coutAvec10 > coutAvec5, "Le coût doit augmenter avec lambda");

        Reseaux.setLambda(10.0); // Restaure la valeur par défaut
    }
}