package partie2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import partie1.Generateur;
import partie1.Maison;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SauvegardeReseauTest {

   
    @TempDir
    Path tempDir;

    @Test
    void testSauvegardeFormatCorrect() throws IOException {
       
        Path cheminFichier = tempDir.resolve("sauvegarde_test.txt");
        String cheminAbsolu = cheminFichier.toString();

       
        Generateur g1 = new Generateur(100, "GenA");
        Maison m1 = new Maison("Villa", Maison.ConsommationType.BASSE); 

        List<Generateur> generateurs = new ArrayList<>(Collections.singletonList(g1));
        List<Maison> maisons = new ArrayList<>(Collections.singletonList(m1));
    
        Map<Generateur, List<Maison>> connexions = new HashMap<>();
        connexions.put(g1, new ArrayList<>(Collections.singletonList(m1)));

        SauvegardeReseau.sauvegarder(cheminAbsolu, generateurs, maisons, connexions);

      
        assertTrue(Files.exists(cheminFichier), "Le fichier de sauvegarde doit être créé.");
        
        List<String> lignes = Files.readAllLines(cheminFichier);
        
        
        assertEquals(3, lignes.size(), "Le fichier devrait contenir 3 lignes.");

       
        assertTrue(lignes.contains("generateur(GenA,100)."), "Ligne générateur incorrecte.");
        assertTrue(lignes.contains("maison(Villa, BASSE)."), "Ligne maison incorrecte.");
        assertTrue(lignes.contains("connexion(GenA, Villa)."), "Ligne connexion incorrecte.");
    }

    @Test
    void testConversionTypesConsommation() throws IOException {
        
        Path cheminFichier = tempDir.resolve("types_test.txt");
        
     
        Maison mBasse = new Maison("M1", Maison.ConsommationType.BASSE);   
        Maison mNormale = new Maison("M2", Maison.ConsommationType.NORMAL); 
        Maison mForte = new Maison("M3", Maison.ConsommationType.FORTE);  
        
       
        Maison mInconnue = new Maison(99, "M4"); 

        List<Maison> maisons = Arrays.asList(mBasse, mNormale, mForte, mInconnue);
        List<Generateur> gens = new ArrayList<>(); 
        Map<Generateur, List<Maison>> connexions = new HashMap<>(); 

        SauvegardeReseau.sauvegarder(cheminFichier.toString(), gens, maisons, connexions);

   
        List<String> lignes = Files.readAllLines(cheminFichier);
    
        assertTrue(lignes.stream().anyMatch(l -> l.contains("maison(M1, BASSE).")));
        assertTrue(lignes.stream().anyMatch(l -> l.contains("maison(M2, NORMAL).")));
        assertTrue(lignes.stream().anyMatch(l -> l.contains("maison(M3, FORTE).")));
        

        assertTrue(lignes.stream().anyMatch(l -> l.contains("maison(M4, NORMAL).")), 
            "Une consommation inconnue doit être sauvegardée comme NORMAL.");
    }

    @Test
    void testSauvegardeListesVides() throws IOException {
    
        Path cheminFichier = tempDir.resolve("vide.txt");
     
        SauvegardeReseau.sauvegarder(cheminFichier.toString(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());

        assertTrue(Files.exists(cheminFichier));
        List<String> lignes = Files.readAllLines(cheminFichier);
        assertTrue(lignes.isEmpty(), "Le fichier doit être vide si les listes sont vides.");
    }
}