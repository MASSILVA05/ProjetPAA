package partie1;
import partie2.FichierLoader;
import partie2.Automatisation;

import java.util.*; // ← couvre List, Map, HashMap, ArrayList, Scanner, etc.
/**
 * Classe principale pour gérer un réseau électrique.
 * 
 * Cette classe permet à l'utilisateur de :
 * <ul>
 *     <li>Ajouter des générateurs</li>
 *     <li>Ajouter des maisons</li>
 *     <li>Ajouter ou supprimer des connexions</li>
 *     <li>Vérifier le réseau et calculer le coût</li>
 * </ul>
 * Elle contient deux menus : le menu principal et le menu réseau.
 * 
 * @author Massilva Djennadi
 * @author Ines Meslem
 * @author Lizaveta Dzemchankova
 */


public class TestReseaux {
	private static Reseaux reseau = new Reseaux(new ArrayList<>(), new ArrayList<>(), new HashMap<>());

	/**
     * Scanner utilisé pour lire les entrées utilisateur.
     */
	private static Scanner sc = new Scanner(System.in);
	/**
	 * Constructeur par défaut de la classe TestReseaux.
	 * Initialise le scanner et prépare le programme pour exécuter le menu.
	 */
	public TestReseaux() {
	    // Aucun champ à initialiser ici, le scanner est statique
	}
	/**
     * Point d'entrée du programme.
     * <p>
     * Affiche le menu principal et exécute les actions selon le choix de l'utilisateur.
     * </p>
     * 
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
    	// Si arguments passés, mode automatique classique
        if (args.length == 2) {
            String fichier = args[0];
            double lambda;

            try {
                lambda = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Erreur : λ doit être un nombre.");
                return;
            }

            System.out.println("Chargement du fichier : " + fichier);
            System.out.println("λ = " + lambda);

            try {
               reseau = FichierLoader.chargerDepuisFichier(fichier);
                System.out.println("Fichier chargé avec succès !");
                reseau.afficherReseau();
                double cout = reseau.calculercout();
                System.out.println("Coût total du réseau : " + cout);
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement du fichier : " + e.getMessage());
            }
            return;
        }
        boolean quitter = false;
        /*Le premier menu:*/
        while (!quitter) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1) Ajouter un générateur");
            System.out.println("2) Ajouter une maison");
            System.out.println("3) Ajouter une connexion");
            System.out.println("4) supprimer une connexion");

            System.out.println("5) Terminer et vérifier le réseau");
            System.out.println("6) Charger un fichier et exécuter automatiquement");
            System.out.println("7) Tester l'automatisation");

            System.out.print("Choix : ");
            
            int choix = -1;
            try {
                choix = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Erreur : veuillez entrer un nombre entier !");
                sc.nextLine(); // vide le buffer
                continue;
            }

            /* Appel des méthodes selon le choix */
            switch (choix) {
                case 1 -> reseau.ajoutergenerateur();
                case 2 -> reseau.ajouterMaison();
                case 3 -> reseau.ajouterconnexion();
                case 4 -> {
                	reseau.supprimerConnexion();
                  
                    
                }case 5 -> {
                    if (reseau.verifierConnexions()) {
                        menuReseau();
                        quitter = true;
                    }
                }
                case 6 -> {
                    sc.nextLine(); // consommer la fin de ligne
                    System.out.print("Nom du fichier : ");
                    String fichier = sc.nextLine().trim();
                    System.out.print("Valeur de lambda : ");
                    double lambdaInput;
                    try {
                        lambdaInput = Double.parseDouble(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Erreur : λ doit être un nombre.");
                        break;
                    } Reseaux.setLambda(lambdaInput);
                    // Mettre à jour la lambda dans Reseaux
                    // Ici, on suppose que lambda est statique
                    try {
                        reseau = FichierLoader.chargerDepuisFichier(fichier);
                        System.out.println("Fichier chargé avec succès !");
                        reseau.afficherReseau();
                        double cout = reseau.calculercout();
                        System.out.println("Coût total du réseau : " + cout);
                    } catch (Exception e) {
                        System.out.println("Erreur lors du chargement du fichier : " + e.getMessage());
                    }
                }
                case 7 -> {
                    if (reseau.getM().isEmpty() || reseau.getG().isEmpty()) {
                        System.out.println("Erreur : Le réseau doit contenir des maisons et des générateurs !");
                        break;
                    }
                    System.out.print("Nombre de tentatives pour l'automatisation : ");
                    int k = 0;
                    try {
                        k = sc.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Erreur : nombre entier attendu !");
                        sc.nextLine();
                        break;
                    }
                    
                    // Créer et exécuter l'automatisation
                    Automatisation auto = new Automatisation(reseau, k);
                    auto.resoudre();
                    reseau = auto.getReseau();
                    
                    System.out.println("Automatisation terminée !");
                    reseau.afficherReseau();
                    System.out.println("Coût total : " + reseau.calculercout());
                }

                default -> System.out.println("Option invalide !");
            }
        }

        System.out.println("Programme terminé.");
    }
    /**
     * Affiche le menu réseau et exécute les actions liées au réseau.
     *
     * Permet à l'utilisateur de :
     * <ul>
     *     <li>Calculer le coût du réseau</li>
     *     <li>Modifier une connexion</li>
     *     <li>Afficher le réseau</li>
     *     <li>Supprimer une connexion</li>
     *     <li>Quitter le menu</li>
     * </ul>
     * 
     */

    private static void menuReseau() {
        boolean quitterMenu = false;

        while (!quitterMenu) {
            System.out.println("\n=== MENU RESEAU ===");
            System.out.println("1) Calculer le coût du réseau");
            System.out.println("2) Modifier une connexion");
            
            System.out.println("3) Afficher le réseau");
            System.out.println("4) supprimer une connexion");

            System.out.println("5) Fin");
            System.out.print("Choix : ");
            
            //int choix = sc.nextInt();
            int choix = -1;
            try {
                choix = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Erreur : veuillez entrer un nombre entier !");
                sc.nextLine(); // vide le buffer
                continue;
            }

            switch (choix) {
            case 1 :{
                double cout = reseau.calculercout();
                System.out.println("Coût total du réseau : " + cout);
                break;
            }
            case 2 :reseau.modification();
            break;
            case 3 : reseau.afficherReseau();
            break;
            case 4 : reseau.supprimerConnexion();
            break;
            case 5 : quitterMenu = true;
            break;
            default : System.out.println("Option invalide !");
            }
        }
    }
}
