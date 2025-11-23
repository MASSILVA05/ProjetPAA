
import java.util.Scanner;

import java.util.*; // ← couvre List, Map, HashMap, ArrayList, Scanner, etc.
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
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
        boolean quitter = false;
        /*Le premier menu:*/
        while (!quitter) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1) Ajouter un générateur");
            System.out.println("2) Ajouter une maison");
            System.out.println("3) Ajouter une connexion");
            System.out.println("4) supprimer une connexion");

            System.out.println("5) Terminer et vérifier le réseau");
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
                case 1 -> Reseaux.ajoutergenerateur();
                case 2 -> Reseaux.ajouterMaison();
                case 3 -> Reseaux.ajouterconnexion();
                case 4 -> {
                	Reseaux.supprimerConnexion();;
                  
                    
                }
                case 5 -> {
                    Reseaux.verifierConnexions();
                    menuReseau();/*on passe au menu des calculs*/
                    quitter = true;
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
                double cout = Reseaux.calculercout(null);
                System.out.println("Coût total du réseau : " + cout);
                break;
            }
            case 2 : Reseaux.modification();
            break;
            case 3 : Reseaux.afficherReseau();
            break;
            case 4 : Reseaux.supprimerConnexion();
            break;
            case 5 : quitterMenu = true;
            break;
            default : System.out.println("Option invalide !");
            }
        }
    }
}
