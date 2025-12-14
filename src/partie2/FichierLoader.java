package partie2;
import java.io.*;
import java.util.*;
import partie1.Maison;
import partie1.Generateur;
import partie1.Reseaux;
/**
 * FichierLoader est une classe utilitaire pour lire et créer un réseau électrique
 * à partir d’un fichier texte formaté. 
 * 
 * Le fichier doit respecter les règles suivantes :
 * 1. Déclaration des générateurs : generateur(Nom,Capacite). 
 *    - Nom : chaîne de caractères, le nom du générateur.
 *    - Capacite : entier représentant la capacité en kW.
 * 2. Déclaration des maisons : maison(Nom,TypeConsommation). 
 *    - Nom : chaîne de caractères, le nom de la maison.
 *    - TypeConsommation : BASSE, NORMAL, FORTE (définit la consommation en kW).
 * 3. Déclaration des connexions : connexion(Generateur,Maison). 
 *    - Associe chaque maison à un générateur.
 * 
 * Les règles importantes : 
 * - Les générateurs doivent être déclarés avant les maisons.
 * - Les maisons doivent être déclarées avant les connexions.
 * - Chaque maison doit avoir exactement une connexion à un générateur.
 * - Chaque ligne doit se terminer par un point '.'.
 */
public class FichierLoader {
	 /**
     * Charge un réseau depuis un fichier texte.
     * 
     * @param chemin Le chemin du fichier texte à lire.
     * @return Un objet Reseaux représentant les générateurs, maisons et connexions.
     * @throws Exception Si le fichier est mal formé ou si les règles ne sont pas respectées.
     */
    public static Reseaux chargerDepuisFichier(String chemin) throws Exception {

        List<Maison> maisons = new ArrayList<>();
        List<Generateur> generateurs = new ArrayList<>();
        Map<Maison, Generateur> connexions = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(chemin));
        String ligne;
        int numeroLigne = 0;

        boolean partieGenerateursTerminee = false;
        boolean partieMaisonsTerminee = false;

        while ((ligne = br.readLine()) != null) {
            numeroLigne++;

            // Supprimer espaces et retours chariot éventuels
            ligne = ligne.trim().replace("\r", "");

            if (ligne.isEmpty()) continue;

            // vérifier point final
            if (!ligne.endsWith(".")) {
                throw new Exception("Erreur ligne " + numeroLigne + " : ligne sans '.' final.");
            }

            // retirer le point final
            ligne = ligne.substring(0, ligne.length() - 1);

            // Détecter le type de ligne
            if (ligne.startsWith("generateur(")) {

                if (partieMaisonsTerminee || !connexions.isEmpty()) {
                    throw new Exception("Erreur ligne " + numeroLigne +
                            " : generateur() doit apparaître avant maisons et connexions.");
                }

                partieGenerateursTerminee = false;

                // parse generateur(gen1,60)
                String contenu = ligne.substring("generateur(".length());
                contenu = contenu.substring(0, contenu.length() - 1);
                String[] args = contenu.split(",");

                if (args.length != 2) {
                    throw new Exception("Erreur ligne " + numeroLigne + " : generateur() doit avoir 2 arguments.");
                }

                String nom = args[0].trim();
                int cap;
                try {
                    cap = Integer.parseInt(args[1].trim());
                } catch (Exception e) {
                    throw new Exception("Erreur ligne " + numeroLigne + " : capacité invalide.");
                }

                generateurs.add(new Generateur(cap, nom));
            }

            else if (ligne.startsWith("maison(")) {

                partieGenerateursTerminee = true;

                if (!connexions.isEmpty()) {
                    throw new Exception("Erreur ligne " + numeroLigne +
                            " : maison() doit apparaître avant connexion().");
                }

                // parse maison(maison2,BASSE)
                String contenu = ligne.substring("maison(".length());
                contenu = contenu.substring(0, contenu.length() - 1);
                String[] args = contenu.split(",");

                if (args.length != 2) {
                    throw new Exception("Erreur ligne " + numeroLigne + " : maison() doit avoir 2 arguments.");
                }

                String nom = args[0].trim();
                String typeStr = args[1].trim();

                // Déclaration unique de la variable 'type'
                Maison.ConsommationType type;
                try {
                	type = Maison.ConsommationType.valueOf(typeStr.trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new Exception("Erreur ligne " + numeroLigne + " : type de consommation invalide (" + typeStr + ")");
                }
                maisons.add(new Maison(nom, type));
            }

            else if (ligne.startsWith("connexion(")) {

                partieMaisonsTerminee = true;

                String contenu = ligne.substring("connexion(".length());
                contenu = contenu.substring(0, contenu.length() - 1);
                String[] args = contenu.split(",");

                if (args.length != 2) {
                    throw new Exception("Erreur ligne " + numeroLigne + " : connexion() doit avoir 2 arguments.");
                }

                String a = args[0].trim();
                String b = args[1].trim();

                Maison maison = null;
                Generateur gene = null;

                // Vérifier si 'a' est une maison ou un générateur
                for (Maison m : maisons) {
                    if (m.getnom().equals(a)) {
                        maison = m;
                        break;
                    }
                }
                if (maison == null) {
                    for (Generateur g : generateurs) {
                        if (g.getnom().equals(a)) {
                            gene = g;
                            break;
                        }
                    }
                }

                // Vérifier si 'b' est une maison ou un générateur
                if (maison == null) { // si maison pas encore trouvée
                    for (Maison m : maisons) {
                        if (m.getnom().equals(b)) {
                            maison = m;
                            break;
                        }
                    }
                }
                if (gene == null) { // si générateur pas encore trouvé
                    for (Generateur g : generateurs) {
                        if (g.getnom().equals(b)) {
                            gene = g;
                            break;
                        }
                    }
                }

                // Vérification finale
                if (maison == null || gene == null) {
                    throw new Exception("Erreur ligne " + numeroLigne +
                            " : connexion() référence un élément non défini.");
                }



                if (maison == null || gene == null) {
                    throw new Exception("Erreur ligne " + numeroLigne +
                            " : connexion() référence un élément non défini.");
                }

                if (connexions.containsKey(maison)) {
                    throw new Exception("Erreur ligne " + numeroLigne +
                            " : maison " + maison.getnom() + " a déjà une connexion.");
                }

                connexions.put(maison, gene);
            }

            else {
                throw new Exception("Erreur ligne " + numeroLigne +
                        " : mot-clé inconnu (pas generateur/maison/connexion).");
            }
        }

        br.close();

        // Vérification finale : chaque maison connectée exactement une fois
        for (Maison m : maisons) {
            if (!connexions.containsKey(m)) {
                throw new Exception("Maison " + m.getnom() + " non connectée.");
            }
        }

        Map<Generateur, List<Maison>> connexionsReseau = new HashMap<Generateur, List<Maison>>();
        for (Map.Entry<Maison, Generateur> entry : connexions.entrySet()) {
            Generateur g = entry.getValue();
            Maison m = entry.getKey();
            if (!connexionsReseau.containsKey(g)) {
                connexionsReseau.put(g, new ArrayList<Maison>());
            }
            connexionsReseau.get(g).add(m);
        }

        return new Reseaux(maisons, generateurs, connexionsReseau);
    }
}
