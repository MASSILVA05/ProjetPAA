

import java.util.Scanner ;
 
import java.util.List;
import java.util.Map;
import java.util.*;
/**
 * Classe représentant un réseau électrique composé de générateurs et de maisons.
 * <p>
 * Cette classe permet de :
 * <ul>
 *     <li>Ajouter des générateurs et des maisons</li>
 *     <li>Créer, modifier et supprimer des connexions entre maisons et générateurs</li>
 *     <li>Vérifier la validité des connexions</li>
 *     <li>Calculer le coût du réseau, la dispersion et la surcharge</li>
 *     <li>Afficher l’état actuel du réseau</li>
 * </ul>
 * 
 * 
 * @author Massilva Djennadi
 *@author Ines Meslem
 *	@author Lizaveta Dzemchankova
 */
public class Reseaux {
	private static double  lambda=10;
	/**
     * Scanner utilisé pour la saisie utilisateur.
     */

	private static Scanner sc=new Scanner(System.in);
	/**
     * Liste des maisons présentes dans le réseau.
     */
	private static List<Maison> M= new ArrayList<>();
	/**
     * Liste des générateurs présents dans le réseau.
     */
    private static List<Generateur> G = new ArrayList<>();

    /**
     * Map représentant les connexions entre maisons et générateurs.
     * 
     * Clé : Maison ; Valeur : Generateur
     * 
     */
    private static Map<Maison, Generateur> connexion = new HashMap<>();
	
    /**
     * Constructeur pour initialiser un réseau avec des listes et des connexions existantes.
     * 
     * @param M la liste des maisons
     * @param G la liste des générateurs
     * @param connexion la map des connexions maison → générateur
     */
	public Reseaux (List<Maison> M, List<Generateur> G,Map <Maison,Generateur> connexion) {
		this.M=M;
		this.G=G;
		this.connexion=connexion;
	}
	/**
     * Ajoute un générateur au réseau.
     * 
     * Si le générateur existe déjà, sa capacité est mise à jour.
     * Sinon, un nouveau générateur est créé et ajouté à la liste.
     * 
     */

	public static void ajoutergenerateur() {
		
		System.out.println("Donner le nom du generateur et sa capacité");
		String nom=sc.next();
		//int cap=sc.nextInt();
		int cap;
		try {
		    cap = sc.nextInt();
		    if (cap <= 0) throw new IllegalArgumentException("La capacité doit être positive !");
		} catch (InputMismatchException e) {
		    System.out.println("Erreur : la capacité doit être un entier !");
		    sc.nextLine();
		    return;
		} catch (IllegalArgumentException e) {
		    System.out.println(e.getMessage());
		    return;
		}
		for (Generateur gen : G) {
	            if (gen.getnom().equals(nom)) {
	                System.out.println(" Générateur déjà existant. Capacité mise à jour !");
	                gen.setCap(cap);
	                return;
	            }
		 }
		Generateur nouveauGen=new Generateur(cap,nom);
		G.add(nouveauGen);
		
	}
	/**
     * Ajoute une maison au réseau.
     *
     * Si la maison existe déjà, sa consommation est mise à jour.
     * Sinon, une nouvelle maison est créée et ajoutée à la liste.
     * 
     */

	public static void ajouterMaison() {
	    try {
	        System.out.println("Donner le nom de la maison et sa consommation (BASSE / NORMALE / FORTE) : ");
	        String nom = sc.next();
	        String type = sc.next();
	        int cons;

	        switch (type.toUpperCase()) {
	            case "BASSE":
	                cons = 10;
	                break;
	            case "NORMALE":
	                cons = 20;
	                break;
	            case "FORTE":
	                cons = 40;
	                break;
	            default:
	                throw new IllegalArgumentException("Type inconnu. Choisissez entre BASSE, NORMALE ou FORTE.");
	        }

	        for (Maison mai : M) {
	            if (mai.getnom().equals(nom)) {
	                System.out.println("Maison déjà existante. Consommation mise à jour !");
	                mai.setCons(cons);
	                return;
	            }
	        }

	        Maison nouvMaison = new Maison(cons, nom);
	        M.add(nouvMaison);
	        System.out.println("Maison ajoutée avec succès : " + nom);

	    } catch (InputMismatchException e) {
	        System.out.println("Erreur : entrée invalide. Veuillez entrer le bon format (ex : M1 NORMALE).");
	        sc.nextLine(); // vide le buffer du scanner
	    } catch (IllegalArgumentException e) {
	        System.out.println("Erreur : " + e.getMessage());
	    }
	}

	/**
     * Crée une connexion entre une maison et un générateur.
     * 
     * Si la maison ou le générateur n’existe pas, affiche une erreur.
     *
     */
	public static void ajouterconnexion() {
		try {
			System.out.println("Donner le nom d'une maison et d'un générateur (ex: M1 G1) :");
		    String nom1 = sc.next();
		    String nom2 = sc.next();
		    Maison maison = null;
		    Generateur generateur = null;

		    for (Maison m : M) {
		           if (m.getnom().equals(nom1)) maison = m;
		           if (m.getnom().equals(nom2)) maison = (maison == null ? m : maison);
		    }
		    for (Generateur g : G) {
		           if (g.getnom().equals(nom1)) generateur = g;
	            if (g.getnom().equals(nom2)) generateur = (generateur == null ? g : generateur);
		    }
		    if (maison == null || generateur == null) {
		    	throw new IllegalArgumentException("Erreur : maison ou générateur introuvable !");
            
		    }
		    connexion.put(maison, generateur);
		    System.out.println("Connexion créée : " + maison.getnom() + " → " + generateur.getnom());
		    

			
		}catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        }catch (InputMismatchException e) {
            System.out.println("Erreur de saisie !");
            sc.nextLine();
        }
		
		
	}
	/**
     * Vérifie que chaque maison est connectée exactement à un générateur.
     * 
     * Affiche les problèmes éventuels (maison non connectée ou connectée à plusieurs générateurs)
     * 
     */
	public static void verifierConnexions() {
	    boolean probleme = false;

	    // on vérifie pour chaque maison
	    for (Maison maison : M) {
	        int count = 0;
	        /*on fait a loop qui compte how many times a specific house (maison) 
	         * appears as a key (i.e., is connected to a generator) 
	         * in the connexion map.*/
	        for (Map.Entry<Maison, Generateur> entry : connexion.entrySet()) {
	        	/* For each entry (a key–value pair) inside the map called connexion, do something */
	            if (entry.getKey().equals(maison)) {
	                count++;
	            }
	        }
	        /* ici on verifie si y'a une maison qui n'a pas de generateur et on renvoie une erreur
	         * on initialise probleme a true si oui
	         */
	        if (count == 0) {
	            System.out.println("La maison " + maison.getnom() + " n’est connectée à aucun générateur !");
	            probleme = true;
	        }
	        /*ici on verifie si y'a une maison qui a plusieurs generateurs et on initialise 
	         * probleme a true si oui  */
	        else if (count > 1) {
	            System.out.println("La maison " + maison.getnom() + " est connectée à plusieurs générateurs !");
	            probleme = true;
	        }
	    }
	    /*ici on fait la verification si y 'a probleme on retourne que tout est bon */
	    if (!probleme) {
	        System.out.println("Toutes les maisons sont correctement connectées !");
	    }
	}
	/**
     * Calcule le taux d’utilisation d’un générateur.
     * 
     * @param g le générateur
     * @return le taux d’utilisation (consommation totale des maisons connectées / capacité du générateur)
     */

	public static double tauxutilisation(Generateur g) {
		double lg=0;
		double cg = g.getcap();
	    if(cg == 0) 
	    	return 0; 
		double u=0;
		for (Map.Entry<Maison, Generateur> entry:connexion.entrySet()){
			if(entry.getValue().equals(g)) {
				lg=lg+entry.getKey().getcons();
				
			}
			
		}
		u=lg/cg;
		return u;
	}
	/**
     * Calcule la dispersion du réseau (écart entre l’utilisation de chaque générateur et la moyenne).
     * 
     * @param S le réseau
     * @return la dispersion totale
     */

	public static double Disp(Reseaux S) {
		if (G.isEmpty()|| connexion.isEmpty()) {
	        System.out.println("Aucune connexion active — dispersion = 0");
	        return 0;
		}
			
		double uMoyenne = 0;
	    Map<Generateur, Double> taux = new HashMap<>();/*key c'est le generateur et value c'est son taux d'uti*/
	    for (Generateur g : G) {
	        double u = tauxutilisation(g);
	        taux.put(g, u);/*on crée le couple generateur et son taux */
	        uMoyenne += u;
	    }
	    uMoyenne /= G.size();

	    double dispersion = 0;
	    for (Generateur g : G) {
	        dispersion += Math.abs(taux.get(g) - uMoyenne);/*on calcule Disp*/
	    }

	    return dispersion;
	}
	  /**
     * Calcule la surcharge du réseau.
     * 
     * La surcharge est la somme des excédents de consommation par rapport à la capacité des générateurs.
     * 
     * 
     * @param S le réseau
     * @return la surcharge totale
     */
	public static double surcharge(Reseaux S) {
		double surcharge = 0;
		for (Generateur g : G) {
	        double lg = 0;
	        double cg = g.getcap();

	        /*je calcule lg comme j'ai deja fait dans le calcul du taux */
	        for (Map.Entry<Maison, Generateur> entry : connexion.entrySet()) {
	            if (entry.getValue().equals(g)) {
	                lg += entry.getKey().getcons();
	            }
	        }

	        /* Ajouter à la surcharge uniquement si le générateur est dépassé*/
	        surcharge += Math.max(0, (lg - cg) / cg);
	    }

	    return surcharge;
	}
	
	/**
     * Calcule le coût total du réseau en combinant dispersion et surcharge.
     * 
     * @param S le réseau
     * @return le coût total
     */

	public static double calculercout(Reseaux S) {
		if (connexion.isEmpty()) {
	        System.out.println("Aucune connexion existante. Le coût ne peut pas être calculé !");
	        return 0;
	    }

		double dispersion=Disp(S);
		double Surcharge=surcharge(S);
		System.out.println("La somme des écarts de chaque générateur par rapport à la moyenne est : "+dispersion+"\n");
		System.out.println("La penalisation des Surcharge est : "+Surcharge+"\n");
		return dispersion+lambda*Surcharge;
	}
	/**
     * Modifie une connexion existante entre une maison et un générateur.
     * 
     * L’utilisateur saisit la connexion à modifier puis la nouvelle connexion.
     * 
     */
	public static void modification() {
		try {
			System.out.println("Veuillez saisir la connexion à modifier (ex: M1 G1) :");
		    String nom1 = sc.next();
		    String nom2 = sc.next();

		    Maison maisonExistante = null;
		    Generateur genExistante = null;

		    /*Identifier la maison et le générateur dans la saisie mm si les noms sont inversés*/
		    for (Maison m : M) {
		        if (m.getnom().equals(nom1) || m.getnom().equals(nom2)) maisonExistante = m;
		    }
		    for (Generateur g : G) {
		        if (g.getnom().equals(nom1) || g.getnom().equals(nom2)) genExistante = g;
		    }

		    /* on vérifie que la connexion existe */
		    if (maisonExistante == null || genExistante == null || !connexion.containsKey(maisonExistante) || 
		        !connexion.get(maisonExistante).equals(genExistante)) {
                throw new IllegalArgumentException("Erreur : la connexion indiquée n'existe pas !");

		    }

		    System.out.println("Veuillez saisir la nouvelle connexion (ex: M1 G2) :");
		    String nnom1 = sc.next();
		    String nnom2 = sc.next();

		    Maison maisonNouvelle = null;
		    Generateur genNouvelle = null;

		    for (Maison m : M) {
		        if (m.getnom().equals(nnom1) || m.getnom().equals(nnom2)) maisonNouvelle = m;
		    }
		    for (Generateur g : G) {
		        if (g.getnom().equals(nnom1) || g.getnom().equals(nnom2)) genNouvelle = g;
		    }

		    if (maisonNouvelle == null || genNouvelle == null) {
                throw new IllegalArgumentException("Erreur : la nouvelle connexion est invalide !");

		    }

		    /*on met à jour la connexion*/
		    connexion.put(maisonNouvelle, genNouvelle);
		    System.out.println("Connexion modifiée : " + maisonNouvelle.getnom() + " → " + genNouvelle.getnom());

		}catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Erreur de saisie !");
            sc.nextLine();
        }
	}
	


    /**
     * Retourne la liste des maisons du réseau.
     * 
     * @return liste des maisons
     */


	public static List<Maison> getM() {
		return M;
	}
	/**
     * Retourne la liste des générateurs du réseau.
     * 
     * @return liste des générateurs
     */
	

	public static List<Generateur> getG() {
		return G;
	}

	/**
     * Affiche l’état actuel du réseau : générateurs, maisons et connexions.
     */

	public static void afficherReseau() {
		System.out.println("\n=== RÉSEAU ÉLECTRIQUE ACTUEL ===\n");

	   /*on fait l'affichage des génerateurs (nom et capacité)*/
	    System.out.println("Générateurs :");
	    if (G.isEmpty()) {
	        System.out.println("  Aucun générateur défini.");
	    } else {
	        for (Generateur g : G) {
	            System.out.println("  - " + g.getnom() + " : capacité = " + g.getcap() + " kW");
	        }
	    }

	    /*on affiche les maison(nom et consommation)*/
	    System.out.println("\nMaisons :");
	    if (M.isEmpty()) {
	        System.out.println("  Aucune maison définie.");
	    } else {
	        for (Maison m : M) {
	            System.out.println("  - " + m.getnom() + " : consommation = " + m.getcons() + " kW");
	        }
	    }

	    /*maintenant aprés avoir fait l'affichage des maison et generateurs
	     * on va afficher les connexions qui existent entre eux*/
	    System.out.println("\nConnexions :");
	    if (connexion.isEmpty()) {
	        System.out.println("  Aucune connexion définie.");
	    } else {
	        for (Map.Entry<Maison, Generateur> entry : connexion.entrySet()) {
	            System.out.println("  - " + entry.getKey().getnom() + " -> " + entry.getValue().getnom());
	        }
	    }

	    System.out.println("\n=== FIN DU RÉSEAU ===\n");
	}
	/**
     * Supprime une connexion existante entre une maison et un générateur.
     * 
     * Vérifie que la maison et le générateur existent et que la connexion est valide.
     * 
     */
	public static void supprimerConnexion() {
	    try {
	        System.out.println("Donner le nom d'une maison et d'un générateur à déconnecter (ex: M1 G1) :");
	        String nom1 = sc.next();
	        String nom2 = sc.next();

	        Maison maison = null;
	        Generateur generateur = null;

	        // Identifier la maison et le générateur (peu importe l'ordre)
	        for (Maison m : M) {
	            if (m.getnom().equals(nom1) || m.getnom().equals(nom2)) maison = m;
	        }
	        for (Generateur g : G) {
	            if (g.getnom().equals(nom1) || g.getnom().equals(nom2)) generateur = g;
	        }

	        // Vérifier que la maison et le générateur existent
	        if (maison == null || generateur == null) {
	            throw new IllegalArgumentException("Erreur : maison ou générateur introuvable !");
	        }

	        // Vérifier que la connexion existe
	        if (!connexion.containsKey(maison) || !connexion.get(maison).equals(generateur)) {
	            throw new IllegalArgumentException("Erreur : la connexion entre " 
	                    + maison.getnom() + " et " + generateur.getnom() + " n'existe pas !");
	        }

	        // Supprimer la connexion
	        connexion.remove(maison);
	        System.out.println("Connexion supprimée : " + maison.getnom() + " ✕ " + generateur.getnom());

	    } catch (IllegalArgumentException e) {
	        System.out.println("Erreur : " + e.getMessage());
	    } catch (InputMismatchException e) {
	        System.out.println("Erreur de saisie !");
	        sc.nextLine();
	    }
	}

}





