package partie2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import partie1.Reseaux;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FichierLoaderTest {

    @TempDir
    Path tempDir;

    private String creerFichierTest(String nomFichier, String contenu) throws IOException {
        File fichier = tempDir.resolve(nomFichier).toFile();
        try (FileWriter writer = new FileWriter(fichier)) {
            writer.write(contenu);
        }
        return fichier.getAbsolutePath();
    }

    @Test
    void testChargementFichierValideSimple() throws Exception {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, BASSE).\nconnexion(GenA, Villa).";
        String chemin = creerFichierTest("valide_simple.txt", contenu);
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertNotNull(reseau);
        assertEquals(1, reseau.getG().size());
        assertEquals(1, reseau.getM().size());
    }

    @Test
    void testChargementFichierValideComplet() throws Exception {
        String contenu = "generateur(GenA, 100).\ngenerateur(GenB, 200).\nmaison(Villa, BASSE).\nmaison(Usine, FORTE).\nconnexion(GenA, Villa).\nconnexion(GenB, Usine).";
        String chemin = creerFichierTest("valide_complet.txt", contenu);
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertEquals(2, reseau.getG().size());
        assertEquals(2, reseau.getM().size());
    }

    @Test
    void testChargementAvecEspacesSupplementaires() throws Exception {
        String contenu = "generateur(  GenA  ,  100  ).\nmaison(  Villa  ,  BASSE  ).\nconnexion(  GenA  ,  Villa  ).";
        String chemin = creerFichierTest("espaces.txt", contenu);
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertEquals(1, reseau.getG().size());
        assertEquals(1, reseau.getM().size());
    }

    @Test
    void testChargementAvecLignesVides() throws Exception {
        String contenu = "generateur(GenA, 100).\n\nmaison(Villa, BASSE).\n\nconnexion(GenA, Villa).";
        String chemin = creerFichierTest("lignes_vides.txt", contenu);
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertEquals(1, reseau.getG().size());
        assertEquals(1, reseau.getM().size());
    }

    @Test
    void testErreurPointFinalManquant() throws IOException {
        String contenu = "generateur(GenA, 100)";
        String chemin = creerFichierTest("sans_point.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("'.' final"));
    }

    @Test
    void testErreurGenerateurSansArguments() throws IOException {
        String contenu = "generateur().\n";
        String chemin = creerFichierTest("gen_sans_args.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("2 arguments"));
    }

    @Test
    void testErreurGenerateurUnArgument() throws IOException {
        String contenu = "generateur(GenA).\n";
        String chemin = creerFichierTest("gen_un_arg.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("2 arguments"));
    }

    @Test
    void testErreurCapaciteInvalide() throws IOException {
        String contenu = "generateur(GenA, beaucoup).\n";
        String chemin = creerFichierTest("cap_invalide.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("capacité invalide"));
    }

    @Test
    void testErreurCapaciteNegative() throws IOException {
        String contenu = "generateur(GenA, abc).\n";
        String chemin = creerFichierTest("cap_negative.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("capacité invalide"));
    }

    @Test
    void testErreurMaisonTypeInvalide() throws IOException {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, TRES_FORTE).\n";
        String chemin = creerFichierTest("type_invalide.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("type de consommation invalide"));
    }

    @Test
    void testErreurMaisonSansArguments() throws IOException {
        String contenu = "generateur(GenA, 100).\nmaison().\n";
        String chemin = creerFichierTest("maison_sans_args.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("2 arguments"));
    }

    @Test
    void testErreurOrdreDeclarations() throws IOException {
        String contenu = "connexion(GenA, Villa).\ngenerateur(GenA, 100).\nmaison(Villa, BASSE).\n";
        String chemin = creerFichierTest("ordre_invalide.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("non défini"));
    }

    @Test
    void testErreurConnexionAvantMaison() throws IOException {
        String contenu = "generateur(GenA, 100).\nconnexion(GenA, Villa).\nmaison(Villa, BASSE).\n";
        String chemin = creerFichierTest("connexion_avant_maison.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("avant") || exception.getMessage().contains("non défini"));
    }

    @Test
    void testErreurMaisonOrpheline() throws IOException {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, BASSE).\n";
        String chemin = creerFichierTest("orpheline.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("non connectée"));
    }

    @Test
    void testErreurConnexionFantome() throws IOException {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, BASSE).\nconnexion(GenA, MaisonInexistante).";
        String chemin = creerFichierTest("fantome.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("non défini"));
    }

    @Test
    void testErreurGenerateurInexistant() throws IOException {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, BASSE).\nconnexion(GenInexistant, Villa).";
        String chemin = creerFichierTest("gen_inexistant.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("non défini"));
    }

    @Test
    void testErreurDoubleConnexion() throws IOException {
        String contenu = "generateur(GenA, 100).\ngenerateur(GenB, 100).\nmaison(Villa, BASSE).\nconnexion(GenA, Villa).\nconnexion(GenB, Villa).";
        String chemin = creerFichierTest("double_connexion.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("connexion"));
    }

    @Test
    void testChargementMaisonBasse() throws Exception {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, BASSE).\nconnexion(GenA, Villa).";
        String chemin = creerFichierTest("maison_basse.txt", contenu);
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertEquals(10, reseau.getM().get(0).getcons());
    }

    @Test
    void testChargementMaisonNormal() throws Exception {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, NORMAL).\nconnexion(GenA, Villa).";
        String chemin = creerFichierTest("maison_normal.txt", contenu);
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertEquals(20, reseau.getM().get(0).getcons());
    }

    @Test
    void testChargementMaisonForte() throws Exception {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, FORTE).\nconnexion(GenA, Villa).";
        String chemin = creerFichierTest("maison_forte.txt", contenu);
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertEquals(40, reseau.getM().get(0).getcons());
    }

    @Test
    void testErreurMotCleInconnu() throws IOException {
        String contenu = "generateur(GenA, 100).\ninconnu(Villa, BASSE).\n";
        String chemin = creerFichierTest("mot_cle_inconnu.txt", contenu);
        Exception exception = assertThrows(Exception.class, () -> FichierLoader.chargerDepuisFichier(chemin));
        assertTrue(exception.getMessage().contains("inconnu"));
    }

    @Test
    void testChargementGrandReseau() throws Exception {
        StringBuilder contenu = new StringBuilder();
        contenu.append("generateur(Gen0, 1000).\n");
        for (int i = 1; i <= 20; i++) {
            contenu.append("generateur(Gen").append(i).append(", ").append(100 * i).append(").\n");
        }
        for (int i = 1; i <= 20; i++) {
            contenu.append("maison(Maison").append(i).append(", NORMAL).\n");
        }
        for (int i = 1; i <= 20; i++) {
            contenu.append("connexion(Gen").append((i % 20) + 1).append(", Maison").append(i).append(").\n");
        }
        String chemin = creerFichierTest("grand_reseau.txt", contenu.toString());
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertEquals(21, reseau.getG().size());
        assertEquals(20, reseau.getM().size());
    }

    @Test
    void testConnexionOrdreFlexible() throws Exception {
        String contenu = "generateur(GenA, 100).\nmaison(Villa, BASSE).\nconnexion(GenA, Villa).";
        String chemin = creerFichierTest("connexion_flexible.txt", contenu);
        Reseaux reseau = FichierLoader.chargerDepuisFichier(chemin);
        assertEquals(1, reseau.getG().size());
        assertEquals(1, reseau.getM().size());
    }
}