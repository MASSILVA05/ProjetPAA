package partie1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GenerateurTest {


    @Test
    void testConstructeurEtGetters() {
   
        int capaciteAttendue = 100;
        String nomAttendu = "Générateur Principal";

        
        Generateur gen = new Generateur(capaciteAttendue, nomAttendu);

        assertEquals(capaciteAttendue, gen.getcap(), "La capacité initiale doit correspondre à celle passée au constructeur");
        assertEquals(nomAttendu, gen.getnom(), "Le nom doit correspondre à celui passé au constructeur");
    }

    @Test
    void testSetCap() {
      
        Generateur gen = new Generateur(50, "Générateur Secours");
        int nouvelleCapacite = 75;

     
        gen.setCap(nouvelleCapacite);
        assertEquals(nouvelleCapacite, gen.getcap(), "La capacité doit être mise à jour après l'appel à setCap");
    }

 
    @Test
    void testToString() {
       
        Generateur gen = new Generateur(120, "Générateur Solaire");
        String resultatAttendu = "Générateur Solaire (120 kW)";
        String resultatObtenu = gen.toString();
        assertEquals(resultatAttendu, resultatObtenu, "La méthode toString ne retourne pas le format attendu");
    }
}