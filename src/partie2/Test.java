package partie2;

import partie1.Reseaux;
import java.util.*;
import java.io.*;

/**
 * Classe principale pour gérer un réseau électrique.
 *
 * Cette classe permet à l'utilisateur de :
 * - Charger un réseau depuis un fichier texte avec validation complète
 * - Construire le réseau manuellement
 * - Optimiser automatiquement le réseau via un algorithme d'optimisation
 * - Sauvegarder la solution dans un fichier
 *
 * @author Massilva Djennadi
 * @author Ines Meslem
 * @author Lizaveta Dzemchankova
 */
public class Test {
	/** Réseau courant */
    private static Reseaux reseau = new Reseaux(new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    /** Scanner pour la saisie utilisateur */
    private static Scanner sc = new Scanner(System.in);
    
    /**
     * Point d'entrée de l'application.
     * Si un fichier est fourni en argument, il est chargé et validé, sinon on passe en mode manuel.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        if (args.length >= 1) {
            chargerFichierEtOptimiser(args);
        } else {
            System.out.println("Pas d'argument fourni. Lancement de la construction manuelle...\n");
            construireManuellement();
        }
    }
    /**
     * Charge un fichier réseau, le valide, puis lance le menu post-chargement.
     * Permet également de configurer lambda via args[1].
     *
     * @param args arguments de la ligne de commande
     */
    private static void chargerFichierEtOptimiser(String[] args) {
        String cheminFichier = args[0];
        double lambda = 10.0;

        if (args.length >= 2) {
            try {
                lambda = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Erreur : lambda doit etre un nombre decimal !");
                System.exit(1);
                return;
            }
        }

        Reseaux.setLambda(lambda);
        System.out.println("Lambda configure a : " + lambda);
        System.out.println();

        try {
            System.out.println("Validation du fichier : " + cheminFichier);
            List<String> erreurs = validerFichier(cheminFichier);

            if (!erreurs.isEmpty()) {
                System.out.println("\nERREURS DETECTEES DANS LE FICHIER :");
                for (String erreur : erreurs) {
                    System.out.println("  - " + erreur);
                }
                System.exit(1);
                return;
            }

            System.out.println("Fichier valide !\n");

            System.out.println("Chargement du reseau...");
            reseau = FichierLoader.chargerDepuisFichier(cheminFichier);
            System.out.println("Reseau charge avec succes !\n");

            reseau.afficherReseau();
            System.out.println("Cout actuel du reseau : " + String.format("%.15f", reseau.calculercout()));

            menuPostChargement();

        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du fichier : " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
    * Valide un fichier réseau sans le charger.
    * Vérifie la syntaxe et la cohérence (generateurs, maisons, connexions).
    *
    * @param cheminFichier chemin du fichier réseau
    * @return liste des erreurs détectées (vide si aucun problème)
    */
    private static List<String> validerFichier(String cheminFichier) {
        List<String> erreurs = new ArrayList<>();
        File fichier = new File(cheminFichier);

        if (!fichier.exists()) {
            erreurs.add("Le fichier n'existe pas : " + cheminFichier);
            return erreurs;
        }

        if (!fichier.canRead()) {
            erreurs.add("Le fichier n'est pas lisible : " + cheminFichier);
            return erreurs;
        }

        Set<String> generateurs = new HashSet<>();
        Set<String> maisons = new HashSet<>();
        Map<String, String> connexions = new HashMap<>();
        int numeroLigne = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fichier))) {
            String ligne;

            while ((ligne = br.readLine()) != null) {
                numeroLigne++;
                ligne = ligne.trim();

                if (ligne.isEmpty()) continue;

                if (!ligne.endsWith(".")) {
                    erreurs.add("Ligne " + numeroLigne + " : pas de point a la fin");
                    continue;
                }

                ligne = ligne.substring(0, ligne.length() - 1).trim();

                // === Validation des generateurs ===
                if (ligne.startsWith("generateur(") && ligne.endsWith(")")) {
                    String contenu = ligne.substring("generateur(".length(), ligne.length() - 1);
                    String[] parties = contenu.split(",");

                    if (parties.length != 2) {
                        erreurs.add("Ligne " + numeroLigne + " : generateur() doit avoir 2 parametres (nom,capacite)");
                        continue;
                    }

                    String nomGen = parties[0].trim();
                    String capaciteStr = parties[1].trim();

                    if (nomGen.isEmpty()) {
                        erreurs.add("Ligne " + numeroLigne + " : nom du generateur vide");
                        continue;
                    }

                    try {
                        int capacite = Integer.parseInt(capaciteStr);
                        if (capacite <= 0) {
                            erreurs.add("Ligne " + numeroLigne + " : la capacite doit etre > 0. Trouve : " + capacite);
                        } else {
                            generateurs.add(nomGen);
                        }
                    } catch (NumberFormatException e) {
                        erreurs.add("Ligne " + numeroLigne + " : capacite invalide '" + capaciteStr + "'");
                    }
                }
                // === Validation des maisons ===
                else if (ligne.startsWith("maison(") && ligne.endsWith(")")) {
                    String contenu = ligne.substring("maison(".length(), ligne.length() - 1);
                    String[] parties = contenu.split(",");

                    if (parties.length != 2) {
                        erreurs.add("Ligne " + numeroLigne + " : maison() doit avoir 2 parametres (nom,type)");
                        continue;
                    }

                    String nomMaison = parties[0].trim();
                    String typeConso = parties[1].trim().toUpperCase();

                    if (nomMaison.isEmpty()) {
                        erreurs.add("Ligne " + numeroLigne + " : nom de maison vide");
                        continue;
                    }

                    if (!typeConso.matches("BASSE|NORMAL|FORTE")) {
                        erreurs.add("Ligne " + numeroLigne + " : type invalide '" + typeConso + "' (BASSE, NORMAL, FORTE)");
                    } else {
                        maisons.add(nomMaison);
                    }
                }
                // === Validation des connexions ===
                else if (ligne.startsWith("connexion(") && ligne.endsWith(")")) {
                    String contenu = ligne.substring("connexion(".length(), ligne.length() - 1);
                    String[] parties = contenu.split(",");

                    if (parties.length != 2) {
                        erreurs.add("Ligne " + numeroLigne + " : connexion() doit avoir 2 parametres (maison,generateur)");
                        continue;
                    }

                    String nom1 = parties[0].trim();
                    String nom2 = parties[1].trim();

                    String nomMaison = null;
                    String nomGen = null;

                    // Determiner lequel est la maison et le generateur
                    if (maisons.contains(nom1) && generateurs.contains(nom2)) {
                        nomMaison = nom1;
                        nomGen = nom2;
                    } else if (maisons.contains(nom2) && generateurs.contains(nom1)) {
                        nomMaison = nom2;
                        nomGen = nom1;
                    } else {
                        if (!maisons.contains(nom1) && !maisons.contains(nom2)) {
                            erreurs.add("Ligne " + numeroLigne + " : aucune maison trouvee dans connexion");
                        }
                        if (!generateurs.contains(nom1) && !generateurs.contains(nom2)) {
                            erreurs.add("Ligne " + numeroLigne + " : aucun generateur trouve dans connexion");
                        }
                        continue;
                    }

                    if (connexions.containsKey(nomMaison)) {
                        erreurs.add("Ligne " + numeroLigne + " : maison '" + nomMaison + "' connectee plusieurs fois");
                    } else {
                        connexions.put(nomMaison, nomGen);
                    }
                }
                // === Type d'element invalide ===
                else {
                    erreurs.add("Ligne " + numeroLigne + " : format invalide (attendu generateur(), maison(), connexion())");
                }
            }

            // Verifier que toutes les maisons sont connectees
            for (String maison : maisons) {
                if (!connexions.containsKey(maison)) {
                    erreurs.add("Maison '" + maison + "' n'est connectee a aucun generateur");
                }
            }

        } catch (IOException e) {
            erreurs.add("Erreur de lecture du fichier : " + e.getMessage());
        }

        return erreurs;
    }
    /**
     * Affiche le menu principal après chargement d'un fichier réseau.
     * Permet de lancer l'automatisation, sauvegarder ou quitter.
     */
    private static void menuPostChargement() {
        boolean quitter = false;

        while (!quitter) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1) Lancer l'automatisation");
            System.out.println("2) Sauvegarder la solution");
            System.out.println("3) Quitter");
            System.out.print("Choix : ");

            Integer choix = lireEntierSafe();

            if (choix == null) {
                continue;
            }

            switch (choix) {
                case 1:
                    lancerAutomatisation();
                    break;
                case 2:
                    sauvegarderReseau();
                    break;
                case 3:
                    quitter = true;
                    break;
                default:
                    System.out.println("Option invalide ! Veuillez entrer 1, 2 ou 3.");
            }
        }

        System.out.println("\nProgramme termine. Au revoir !");
    }
    /**
     * Permet à l'utilisateur de construire manuellement le réseau.
     * <p>
     * Le menu propose les actions suivantes :
     * <ul>
     *   <li>Ajouter un générateur</li>
     *   <li>Ajouter une maison</li>
     *   <li>Ajouter une connexion</li>
     *   <li>Supprimer une connexion</li>
     *   <li>Terminer la construction et vérifier le réseau</li>
     * </ul>
     * Chaque option est répétée tant que l'utilisateur ne choisit pas de terminer.
     * Les méthodes du réseau sont appelées pour effectuer les ajouts/modifications.
     */

    private static void construireManuellement() {
        boolean terminer = false;

        while (!terminer) {
            System.out.println("\n=== CONSTRUCTION MANUELLE DU RESEAU ===");
            System.out.println("1) Ajouter un generateur");
            System.out.println("2) Ajouter une maison");
            System.out.println("3) Ajouter une connexion");
            System.out.println("4) Supprimer une connexion");
            System.out.println("5) Terminer et verifier le reseau");
            System.out.print("Choix : ");

            Integer choix = lireEntierSafe();

            if (choix == null) {
                continue;
            }

            switch (choix) {
                case 1:
                    reseau.ajoutergenerateur();
                    break;
                case 2:
                    reseau.ajouterMaison();
                    break;
                case 3:
                    reseau.ajouterconnexion();
                    break;
                case 4:
                    reseau.supprimerConnexion();
                    break;
                case 5:
                    if (reseau.getG().isEmpty() && reseau.getM().isEmpty()) {
                        System.out.println("Le reseau est vide : aucune maison et aucun generateur n'ont ete ajoutes.");
                    } else if (reseau.getM().isEmpty()) {
                        System.out.println("Aucune maison n'a ete ajoutee !");
                    } else if (!reseau.verifierConnexions()) {
                        System.out.println("Erreur : certaines maisons ne sont pas correctement connectees !");
                    } else {
                        terminer = true;
                        reseau.afficherReseau();
                        menuReseau();
                    }


                    break;
                default:
                    System.out.println("Option invalide ! Veuillez entrer entre 1 et 5.");
            }
        }
    }
    /**
     * Affiche le menu principal après la construction ou le chargement du réseau.
     * <p>
     * Les options disponibles :
     * <ul>
     *   <li>Calculer le coût du réseau</li>
     *   <li>Modifier une connexion</li>
     *   <li>Afficher le réseau</li>
     *   <li>Supprimer une connexion</li>
     *   <li>Lancer l'automatisation</li>
     *   <li>Sauvegarder et quitter</li>
     *   <li>Quitter sans sauvegarder</li>
     * </ul>
     */

    private static void menuReseau() {
        boolean quitterMenu = false;

        while (!quitterMenu) {
            System.out.println("\n=== MENU RESEAU ===");
            System.out.println("1) Calculer le cout du reseau");
            System.out.println("2) Modifier une connexion");
            System.out.println("3) Afficher le reseau");
            System.out.println("4) Supprimer une connexion");
            System.out.println("5) Lancer l'automatisation");
            System.out.println("6) Sauvegarder et quitter");
            System.out.println("7) Quitter sans sauvegarder");
            System.out.print("Choix : ");

            Integer choix = lireEntierSafe();

            if (choix == null) {
                continue;
            }

            switch (choix) {
                case 1:
                    double cout = reseau.calculercout();
                    System.out.println("Cout total du reseau : " + String.format("%.15f", cout));
                    break;
                case 2:
                    reseau.modification();
                    break;
                case 3:
                    reseau.afficherReseau();
                    break;
                case 4:
                    reseau.supprimerConnexion();
                    break;
                case 5:
                    lancerAutomatisation();
                    break;
                case 6:
                    sauvegarderReseau();
                    quitterMenu = true;
                    break;
                case 7:
                    quitterMenu = true;
                    break;
                default:
                    System.out.println("Option invalide ! Veuillez entrer entre 1 et 7.");
            }
        }
    }
    /**
     * Lance l'algorithme d'automatisation pour optimiser le réseau.
     * <p>
     * Vérifie que le réseau contient au moins une maison et un générateur.
     * Affiche le nombre d'itérations prévues, effectue l'optimisation
     * et affiche le réseau et le coût total optimisé.
     */

    private static void lancerAutomatisation() {
        if (reseau == null || reseau.getM().isEmpty() || reseau.getG().isEmpty()) {
            System.out.println("Erreur : Le reseau doit contenir des maisons et des generateurs !");
            return;
        }

        Automatisation auto = new Automatisation(reseau);
        System.out.println("   -> " + auto.getK() + " iterations prevues\n");
        auto.resoudre();

        reseau = auto.getReseau();

        System.out.println("\n=== Automatisation terminee ===");
        reseau.afficherReseau();
        System.out.println("Cout total optimise : " + String.format("%.15f", reseau.calculercout()));
    }
    /**
     * Sauvegarde le réseau courant dans un fichier texte.
     * <p>
     * Demande le nom du fichier à l'utilisateur. Si le fichier existe déjà,
     * propose de l'écraser. Utilise la classe SauvegardeReseau pour effectuer
     * la sauvegarde.
     */
    private static void sauvegarderReseau() {
        System.out.print("Nom du fichier de sortie : ");
        String nomFichier = sc.nextLine().trim();

        if (nomFichier.isEmpty()) {
            System.out.println("Erreur : nom de fichier vide !");
            return;
        }

        File f = new File(nomFichier);
        if (f.exists()) {
            System.out.print("Le fichier '" + nomFichier + "' existe deja. Voulez-vous l'ecraser ? (O/N) : ");
            String reponse = sc.nextLine().trim().toUpperCase();
            if (!reponse.equals("O") && !reponse.equals("OUI")) {
                System.out.println("Sauvegarde annulee.");
                return;
            }
        }

        try {
            SauvegardeReseau.sauvegarder(reseau, nomFichier);
            System.out.println("Reseau sauvegarde avec succes dans : " + nomFichier);
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }
    /**
     * Lit un entier depuis l'utilisateur avec gestion des erreurs.
     * @return entier saisi ou null si invalide
     */

    private static Integer lireEntierSafe() {
        try {
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Erreur : veuillez entrer une valeur !");
                return null;
            }

            if (input.contains(".")) {
                System.out.println("Erreur : '" + input + "' n'est pas un nombre entier !");
                return null;
            }

            return Integer.parseInt(input);

        } catch (NumberFormatException e) {
            System.out.println("Erreur : veuillez entrer un nombre entier valide !");
            return null;
        }
    }
}