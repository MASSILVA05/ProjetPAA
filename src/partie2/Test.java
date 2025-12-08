package partie2;
import partie1.Reseaux;
import partie2.FichierLoader;
import partie2.Automatisation;
import java.util.*;

import partie2.SauvegardeReseau;

import java.util.*;

/**
 * Classe principale pour gérer un réseau électrique.
 * Permet à l'utilisateur de construire le réseau manuellement ou via fichier,
 * puis de lancer une automatisation pour optimiser le coût.
 */
public class Test {
    private static Reseaux reseau = new Reseaux(new ArrayList<>(), new ArrayList<>(), new HashMap<>());
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        boolean quitter = false;

        while (!quitter) {
            System.out.println("\n=== PROGRAMME DE RESEAU ELECTRIQUE ===");
            System.out.println("Voulez-vous :");
            System.out.println("1) Charger un réseau depuis un fichier");
            System.out.println("2) Construire le réseau manuellement");
            System.out.println("3) Quitter");
            System.out.print("Choix : ");

            int choix = lireEntier();
            switch (choix) {
                case 1 -> chargerDepuisFichier();
                case 2 -> construireManuellement();
                case 3 -> quitter = true;
                default -> System.out.println("Option invalide !");
            }
        }

        System.out.println("Programme terminé.");
    }

    private static void chargerDepuisFichier() {
        try {
            System.out.print("Nom du fichier : ");
            String fichier = sc.nextLine().trim();
            System.out.print("Valeur de lambda : ");
            double lambda = Double.parseDouble(sc.nextLine().trim());
            Reseaux.setLambda(lambda); // si lambda est statique

            reseau = FichierLoader.chargerDepuisFichier(fichier);
            System.out.println("Fichier chargé avec succès !");
            reseau.afficherReseau();

            System.out.println("Coût total du réseau : " + reseau.calculercout());
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement du fichier : " + e.getMessage());
            return;
        }

        // Mini-menu après le chargement
        boolean quitter = false;
        while (!quitter) {
            System.out.println("\nQue voulez-vous faire ?");
            System.out.println("1) Lancer l'automatisation");
            System.out.println("2) Sauvegarder / afficher le réseau");
            System.out.println("3) Quitter et revenir au menu principal");
            System.out.print("Choix : ");

            int choix = lireEntier();
            switch (choix) {
                case 1 -> {
                    lancerAutomatisation();
                }
                case 2 -> {
                    reseau.afficherReseau();
                    System.out.println("Coût total : " + reseau.calculercout());
                }
                case 3 -> quitter = true; // revient au menu principal
                default -> System.out.println("Option invalide !");
            }
        }
    }



    private static void construireManuellement() {
        boolean terminer = false;

        while (!terminer) {
            System.out.println("\n=== CONSTRUCTION MANUELLE DU RESEAU ===");
            System.out.println("1) Ajouter un générateur");
            System.out.println("2) Ajouter une maison");
            System.out.println("3) Ajouter une connexion");
            System.out.println("4) Supprimer une connexion");
            System.out.println("5) Terminer et vérifier le réseau");
            System.out.print("Choix : ");

            int choix = lireEntier();
            switch (choix) {
                case 1 -> reseau.ajoutergenerateur();
                case 2 -> reseau.ajouterMaison();
                case 3 -> reseau.ajouterconnexion();
                case 4 -> reseau.supprimerConnexion();
                case 5 -> {
                    if (!reseau.verifierConnexions()) {
                        System.out.println("Erreur : certaines maisons ne sont pas connectées !");
                    } else {
                        terminer = true;
                        reseau.afficherReseau();
                        menuReseau(); // menu secondaire après construction manuelle
                    }
                }
                default -> System.out.println("Option invalide !");
            }
        }
    }

    private static void menuReseau() {
        boolean quitterMenu = false;

        while (!quitterMenu) {
            System.out.println("\n=== MENU RESEAU ===");
            System.out.println("1) Calculer le coût du réseau");
            System.out.println("2) Modifier une connexion");
            System.out.println("3) Afficher le réseau");
            System.out.println("4) Supprimer une connexion");
            System.out.println("5) Lancer l'automatisation");
            System.out.println("7) Sauvegarder le reseau dans un fichier");
            System.out.println("6) Quitter le menu réseau");
            System.out.print("Choix : ");

            int choix = lireEntier();
            switch (choix) {
                case 1 -> {
                    double cout = reseau.calculercout();
                    System.out.println("Coût total du réseau : " + cout);
                }
                case 2 -> reseau.modification();
                case 3 -> reseau.afficherReseau();
                case 4 -> reseau.supprimerConnexion();
                case 5 -> lancerAutomatisation();
                case 7 -> SauvegardeReseau.sauvegarder("Reseaux", reseau);
                case 6 -> quitterMenu = true;
                default -> System.out.println("Option invalide !");
            }
        }
    }

    private static void lancerAutomatisation() {
        // Vérifier que le réseau est valide
        if (reseau == null || reseau.getM().isEmpty() || reseau.getG().isEmpty()) {
            System.out.println("Erreur : Le réseau doit contenir des maisons et des générateurs !");
            return;
        }

        int k = -1;
        while (k <= 0) {
            System.out.print("Nombre de tentatives pour l'automatisation (entier > 0) : ");
            k = lireEntier();
            if (k <= 0) {
                System.out.println("Erreur : veuillez entrer un entier positif.");
            }
        }

        // Automatisation sans coût cible, optimisation maximale
        Automatisation auto = new Automatisation(reseau, k);
        auto.resoudre();

        // Mettre à jour le réseau après automatisation
        reseau = auto.getReseau();

        System.out.println("\n=== Automatisation terminée ===");
        reseau.afficherReseau();
        System.out.println("Coût total optimisé : " + reseau.calculercout());
    }

    



    private static int lireEntier() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

