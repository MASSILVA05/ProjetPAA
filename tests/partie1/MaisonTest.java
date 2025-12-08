package partie1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MaisonTest {

   
    @Test
    void testConstructeurAvecEnum() {
    
        String nom = "Maison de Campagne";
        Maison.ConsommationType type = Maison.ConsommationType.BASSE; 
        Maison maison = new Maison(nom, type);

   
        assertEquals("Maison de Campagne", maison.getnom());
        assertEquals(10, maison.getcons(), "Le constructeur avec Enum BASSE devrait initialiser la conso à 10");
    }

    @Test
    void testConstructeurAvecInt() {
       
        int consoManuelle = 85;
        String nom = "Usine";

       
        Maison maison = new Maison(consoManuelle, nom);

        
        assertEquals("Usine", maison.getnom());
        assertEquals(85, maison.getcons(), "Le constructeur avec int devrait définir la conso exactement");
    }

    @Test
    void testSetCons() {
    
        Maison maison = new Maison("Appartement", Maison.ConsommationType.NORMAL);
    
        maison.setCons(Maison.ConsommationType.FORTE);

        assertEquals(40, maison.getcons(), "setCons devrait mettre à jour la valeur numérique de la consommation");
    }

   
    @Test
    void testValeursEnum() {
        assertEquals(10, Maison.ConsommationType.BASSE.getValeur());
        assertEquals(20, Maison.ConsommationType.NORMAL.getValeur());
        assertEquals(40, Maison.ConsommationType.FORTE.getValeur());
    }

    @Test
    void testToString() {
      
        Maison maison = new Maison("Villa", Maison.ConsommationType.NORMAL); 
        String resultatAttendu = "Villa (20 kW)";
        String resultatObtenu = maison.toString();
        assertEquals(resultatAttendu, resultatObtenu);
    }
}