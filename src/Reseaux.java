import java.util.Scanner ;
 
import java.util.List;
import java.util.Map;
import java.util.*;

public class Reseaux {
	private static Scanner sc=new Scanner(System.in);
	private static List<Maison> M= new ArrayList<>();
	private static List<Generateur> G= new ArrayList<>();
	private static Map <Maison,Generateur> connexion=new HashMap<>();
	
	
	public Reseaux (List<Maison> M, List<Generateur> G,Map <Maison,Generateur> connexion) {
		this.M=M;
		this.G=G;
		this.connexion=connexion;
	}
	
	public static void ajoutergenerateur() {
		
		System.out.println("Donner le nom du generateur et sa capacité");
		String nom=sc.next();
		int cap=sc.nextInt();
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
	public static void ajouterMaison() {
		
		System.out.println("Donner le nom de la maison et sa consommation(BASSE/NORMALE/FORTE) : ");
		String nom=sc.next();
		String type=sc.next();
		int cons;
		
		switch (type) {
        case "BASSE": cons = 10; break;
        case "NORMALE": cons = 20; break;
        case "FORTE": cons = 40; break;
        default:
            System.out.println("Type inconnu. Choisissez entre BASSE, NORMALE ou FORTE.");
            return;
		}
		 for (Maison mai : M) {
	            if (mai.getnom().equals(nom)) {
	                System.out.println("Générateur déjà existant. Capacité mise à jour !");
	                mai.setCons(cons);
	                return;
	            }
		 }
		Maison nouvMaison=new Maison(cons,nom);
		M.add(nouvMaison);
		
	}
	public static void ajouterconnexion() {
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
	            System.out.println("Erreur : maison ou générateur introuvable !");
	            return;
	     }

	        connexion.put(maison, generateur);
	        System.out.println("Connexion créée : " + maison.getnom() + " -> " + generateur.getnom());
	    

		
	}
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
	/*on va calculer le taux d'utilisation d'un generateur pour ensuite 
	 * calculer le cout d'un reseau */
	public static double tauxutilisation(Generateur g) {
		double lg=0;
		double cg=0;
		double u=0;
		for (Map.Entry<Maison, Generateur> entry:connexion.entrySet()){
			if(entry.getValue().equals(g)) {
				lg=lg+entry.getKey().getcons();
				cg=entry.getValue().getcap();
			}
			
		}
		u=lg/cg;
		return u;
	}
	public static double Disp(Reseaux S) {
		if (G.isEmpty()) 
			return 0;
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
	public static double surcharge(Reseaux S) {
		double surcharge = 0;
		for (Generateur g : G) {
	        int lg = 0;
	        int cg = g.getcap();

	        /*je calcule lg comme j'ai deja fait dans le calcul du taux */
	        for (Map.Entry<Maison, Generateur> entry : connexion.entrySet()) {
	            if (entry.getValue().equals(g)) {
	                lg += entry.getKey().getcons();
	            }
	        }

	        /* Ajouter à la surcharge uniquement si le générateur est dépassé*/
	        surcharge += Math.max(0, (double)(lg - cg) / cg);
	    }

	    return surcharge;
	}
	
	
	public static double calculercout(Reseaux S) {
		double dispersion=Disp(S);
		double Surcharge=surcharge(S);
		System.out.println("La somme des écarts de chaque générateur par rapport à la moyenne est : "+dispersion+"\n");
		System.out.println("La penalisation des Surcharge est : "+Surcharge+"\n");
		return dispersion+10*Surcharge;
	}
	
	public static void modification() {
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
	        System.out.println("Erreur : la connexion indiquée n'existe pas !");
	        return;
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
	        System.out.println("Erreur : la nouvelle connexion est invalide !");
	        return;
	    }

	    /*on met à jour la connexion*/
	    connexion.put(maisonNouvelle, genNouvelle);
	    System.out.println("Connexion modifiée : " + maisonNouvelle.getnom() + " -> " + genNouvelle.getnom());
	}
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
	
}







