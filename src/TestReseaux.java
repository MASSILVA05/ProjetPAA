import java.util.Scanner;


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
            System.out.println("4) Terminer et vérifier le réseau");
            System.out.print("Choix : ");
            
            int choix = sc.nextInt();
            /* Ici on appelle les methodes selon le choix de l'utilisateur*/
            switch (choix) {
                case 1 -> Reseaux.ajoutergenerateur();
                case 2 -> Reseaux.ajouterMaison();
                case 3 -> Reseaux.ajouterconnexion();
                case 4 -> {
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
            System.out.println("4) Fin");
            System.out.print("Choix : ");
            
            int choix = sc.nextInt();

            switch (choix) {
                case 1 -> {
                    double cout = Reseaux.calculercout(null); // null car on utilise les listes statiques
                    System.out.println("Coût total du réseau : " + cout);
                }
                case 2 -> Reseaux.modification();
                case 3 -> Reseaux.afficherReseau();
                case 4 -> quitterMenu = true;
                default -> System.out.println("Option invalide !");
            }
        }
    }
}
