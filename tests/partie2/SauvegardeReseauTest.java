package partie2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import partie1.Generateur;
import partie1.Maison;
import partie1.Reseaux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests unitaires pour la classe {@link SauvegardeReseau}.
 *
 * <p>
 * Ces tests vérifient le bon fonctionnement de la sauvegarde d'un réseau électrique
 * vers un fichier texte, en respectant le format attendu par l'énoncé.
 * </p>
 *
 * <p>
 * Les points testés sont :
 * </p>
 * <ul>
 *   <li>Le format exact des lignes générées (générateurs, maisons, connexions)</li>
 *   <li>La prise en compte correcte des différents types de consommation</li>
 *   <li>La gestion d'un réseau vide</li>
 *   <li>La gestion des chemins de fichiers invalides</li>
 * </ul>
 *
 * <p>
 * Les tests utilisent l'annotation {@link TempDir} afin de créer des fichiers temporaires
 * sans impacter le système de fichiers de l'utilisateur.
 * </p>
 */
class SauvegardeReseauTest {

    /**
     * Répertoire temporaire fourni automatiquement par JUnit.
     * <p>
     * Chaque test dispose d'un dossier isolé garantissant
     * l'absence d'effets de bord entre les tests.
     * </p>
     */
    @TempDir
    Path tempDir;

    /**
     * Vérifie que la sauvegarde d'un réseau simple
     * respecte strictement le format de sortie attendu.
     *
     * <p>
     * Le réseau contient :
     * </p>
     * <ul>
     *   <li>Un générateur</li>
     *   <li>Une maison</li>
     *   <li>Une connexion entre les deux</li>
     * </ul>
     *
     * <p>
     * Le test contrôle :
     * </p>
     * <ul>
     *   <li>Le nombre exact de lignes générées</li>
     *   <li>L'ordre des lignes</li>
     *   <li>Le format textuel précis de chaque ligne</li>
     * </ul>
     *
     * @throws IOException si une erreur d'accès au fichier survient
     */
    @Test
    void sauvegardeFormatCorrect() throws IOException {
        Path fichier = tempDir.resolve("reseau.txt");

        Generateur g = new Generateur(100, "GenA");
        Maison m = new Maison("Villa", Maison.ConsommationType.BASSE);

        Reseaux reseau = new Reseaux();

        reseau.getG().add(g);
        reseau.getM().add(m);

        Map<Generateur, List<Maison>> connexions = reseau.getConnexions();
        connexions.put(g, new ArrayList<>());
        connexions.get(g).add(m);

        SauvegardeReseau.sauvegarder(reseau, fichier.toString());

        List<String> lignes = Files.readAllLines(fichier);

        assertEquals(3, lignes.size());
        assertEquals("generateur(GenA,100).", lignes.get(0));
        assertEquals("maison(Villa,BASSE).", lignes.get(1));
        assertEquals("connexion(Villa,GenA).", lignes.get(2));
    }

    /**
     * Vérifie que tous les types de consommation des maisons
     * sont correctement sauvegardés dans le fichier.
     *
     * <p>
     * Les types testés sont :
     * </p>
     * <ul>
     *   <li>{@code BASSE}</li>
     *   <li>{@code NORMAL}</li>
     *   <li>{@code FORTE}</li>
     * </ul>
     *
     * <p>
     * Aucune connexion n'est nécessaire pour ce test :
     * seules les maisons sont sauvegardées.
     * </p>
     *
     * @throws IOException si une erreur d'accès au fichier survient
     */
    @Test
    void sauvegardeTypesConsommation() throws IOException {
        Path fichier = tempDir.resolve("maisons.txt");

        Reseaux reseau = new Reseaux();

        reseau.getM().add(new Maison("M1", Maison.ConsommationType.BASSE));
        reseau.getM().add(new Maison("M2", Maison.ConsommationType.NORMAL));
        reseau.getM().add(new Maison("M3", Maison.ConsommationType.FORTE));

        SauvegardeReseau.sauvegarder(reseau, fichier.toString());

        List<String> lignes = Files.readAllLines(fichier);

        assertEquals(3, lignes.size());
        assertEquals("maison(M1,BASSE).", lignes.get(0));
        assertEquals("maison(M2,NORMAL).", lignes.get(1));
        assertEquals("maison(M3,FORTE).", lignes.get(2));
    }

    /**
     * Vérifie que la sauvegarde d'un réseau vide
     * ne génère aucune ligne dans le fichier.
     *
     * <p>
     * Le test garantit que la méthode :
     * </p>
     * <ul>
     *   <li>Ne plante pas</li>
     *   <li>Crée bien un fichier</li>
     *   <li>N'écrit aucune donnée inutile</li>
     * </ul>
     *
     * @throws IOException si une erreur d'accès au fichier survient
     */
    @Test
    void sauvegardeReseauVide() throws IOException {
        Path fichier = tempDir.resolve("vide.txt");

        Reseaux reseau = new Reseaux();
        SauvegardeReseau.sauvegarder(reseau, fichier.toString());

        List<String> lignes = Files.readAllLines(fichier);
        assertTrue(lignes.isEmpty());
    }

    /**
     * Vérifie qu'un nom de fichier invalide
     * provoque une exception explicite.
     *
     * <p>
     * Le test s'assure que la méthode ne tente pas
     * d'écrire sur un chemin incorrect ou vide.
     * </p>
     */
    @Test
    void nomFichierInvalide() {
        Reseaux reseau = new Reseaux();

        assertThrows(IllegalArgumentException.class, () ->
                SauvegardeReseau.sauvegarder(reseau, " ")
        );
    }
}
