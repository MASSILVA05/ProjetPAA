/*Membre du groupe : Massilva Djennadi
 * Ines Meslem
 * Lizaveta Dzemchankova*/

import java.util.Scanner;

import java.util.*; // ← couvre List, Map, HashMap, ArrayList, Scanner, etc.
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
public class TestReseaux {
	private static Scanner sc = new Scanner(System.in);

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
            
            /*int choix = sc.nextInt();*/
            int choix = -1;
            try {
                choix = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Erreur : veuillez entrer un nombre entier !");
                sc.nextLine(); // vide le buffer
                continue;
            }

            /* Ici on appelle les methodes selon le choix de l'utilisateur*/
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
            }
            case 2 : Reseaux.modification();
            case 3 : Reseaux.afficherReseau();
            case 4 : Reseaux.supprimerConnexion();
            case 5 : quitterMenu = true;
            default : System.out.println("Option invalide !");
            }
        }
    }
}
