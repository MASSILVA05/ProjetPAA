package partie2;

import partie1.Generateur;
import partie1.Maison;
import partie1.Reseaux;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Classe utilitaire pour sauvegarder un réseau électrique dans un fichier texte.
 * 
 * Le format de sauvegarde respecte la syntaxe définie :
 * - Générateurs : generateur nom capacite.
 * - Maisons : maison nom type.
 * - Connexions : connexion maison generateur.
 * 
 * @author Massilva Djennadi
 * @author Ines Meslem
 * @author Lizaveta Dzemchankova
 */
public class SauvegardeReseau {

    /**
     * Sauvegarde l'état actuel du réseau dans un fichier texte.
     * 
     * Format de sortie (respect strict du format Section I) :
     * - Générateurs : generateur(nom,capacite).
     * - Maisons : maison(nom,type).
     * - Connexions : connexion(maison,generateur).
     * 
     * L'ordre est important : d'abord les générateurs, puis les maisons, puis les connexions.
     * 
     * @param reseau le réseau à sauvegarder (contient générateurs, maisons et connexions)
     * @param nomFichier le chemin ou nom du fichier de sortie
     * @throws IOException si une erreur d'écriture se produit
     */
    public static void sauvegarder(Reseaux reseau, String nomFichier) throws IOException {
        if (nomFichier == null || nomFichier.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du fichier ne peut pas etre vide !");
        }

        List<Generateur> generateurs = reseau.getG();
        List<Maison> maisons = reseau.getM();
        Map<Generateur, List<Maison>> connexions = reseau.getConnexions();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier))) {
            
            // === Écrire les générateurs ===
            // Format : generateur(nom,capacite).
            for (Generateur g : generateurs) {
                writer.write("generateur(" + g.getnom() + "," + (int)g.getcap() + ").");
                writer.newLine();
            }

            // === Écrire les maisons ===
            // Format : maison(nom,type).
            for (Maison m : maisons) {
                String type = m.getcons_type();
                writer.write("maison(" + m.getnom() + "," + type + ").");
                writer.newLine();
            }

            // === Écrire les connexions ===
            // Format : connexion(maison,generateur).
            for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
                Generateur gen = entry.getKey();
                List<Maison> listeMaisons = entry.getValue();
                for (Maison m : listeMaisons) {
                    writer.write("connexion(" + m.getnom() + "," + gen.getnom() + ").");
                    writer.newLine();
                }
            }

        }
    }
}