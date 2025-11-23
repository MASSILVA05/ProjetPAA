import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SauvegardeReseau {

    /**
     * Sauvegarde l'état actuel du réseau dans un fichier texte.
     * Respecte le format strict : Générateurs -> Maisons -> Connexions.
     *
     * @param nomFichier  Le chemin ou nom du fichier.
     * @param generateurs La liste complète des générateurs (G).
     * @param maisons     La liste complète des maisons (M).
     * @param connexions  La map des connexions (Generateur -> Liste de Maisons).
     */
    public static void sauvegarder(String nomFichier, List<Generateur> generateurs, List<Maison> maisons, Map<Generateur, List<Maison>> connexions) {
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier))) {
            // ecrire les generateurs sous format : generateur(nom,capacite).
            for (Generateur g : generateurs) {
                writer.write("generateur(" + g.getnom() + "," + (int)g.getcap() + ").");
                writer.newLine();
            }

            // maisons sous format : maison(nom, TYPE).
            for (Maison m : maisons) {
                String type = convertirPuissanceEnType(m.getcons());
                writer.write("maison(" + m.getnom() + ", " + type + ").");
                writer.newLine();
            }

            // connexion(nomGen, nomMaison).
            // On parcourt la Map pour trouver qui est connecté à qui
            for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
                Generateur gen = entry.getKey();
                List<Maison> listeMaisons = entry.getValue();

                for (Maison m : listeMaisons) {
                    writer.write("connexion(" + gen.getnom() + ", " + m.getnom() + ").");
                    writer.newLine();
                }
            }

            System.out.println("Sauvegarde réussie dans le fichier : " + nomFichier);

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Convertit la puissance (double/int) en Type (String) pour le fichier.
     * 10 -> BASSE, 20 -> NORMAL, 40 -> FORTE
     */
    private static String convertirPuissanceEnType(double puissance) {
        int p = (int) puissance;
        switch (p) {
            case 10: return "BASSE";
            case 20: return "NORMAL";
            case 40: return "FORTE";
            default: return "NORMAL";
        }
    }
}