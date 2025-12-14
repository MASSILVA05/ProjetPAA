package partie1;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.*;

/**
 * Classe representant un reseau electrique compose de generateurs et de maisons.
 * 
 * Cette classe permet de :
 * - Ajouter des generateurs et des maisons
 * - Creer, modifier et supprimer des connexions entre maisons et generateurs
 * - Verifier la validite des connexions
 * - Calculer le cout du reseau, la dispersion et la surcharge
 * - Afficher l'etat actuel du reseau
 * 
 * @author Massilva Djennadi
 * @author Ines Meslem
 * @author Lizaveta Dzemchankova
 */
public class Reseaux {
    
    /**
     * Parametre de severite de la penalisation pour les surcharges.
     */
    private static double lambda = 10;
    
    /**
     * Scanner utilise pour la saisie utilisateur.
     */
    public static Scanner sc = new Scanner(System.in);
    
    /**
     * Liste des maisons presentes dans le reseau.
     */
    private List<Maison> M = new ArrayList<>();
    
    /**
     * Liste des generateurs presents dans le reseau.
     */
    private List<Generateur> G = new ArrayList<>();
    
    /**
     * Map representant les connexions entre maisons et generateurs.
     * Cle : Generateur
     * Valeur : Liste de maisons connectees a ce generateur
     */
    private Map<Generateur, List<Maison>> connexion = new HashMap<>();
    
    /**
     * Constructeur pour initialiser un reseau avec des listes et des connexions existantes.
     * 
     * @param M la liste des maisons
     * @param G la liste des generateurs
     * @param connexion la map des connexions generateur -> liste de maisons
     */
    public Reseaux(List<Maison> M, List<Generateur> G, Map<Generateur, List<Maison>> connexion) {
        this.M = M;
        this.G = G;
        this.connexion = connexion;
    }
    
    /**
     * Constructeur par defaut qui initialise un reseau vide.
     * Cree des listes et une map vides.
     */
    public Reseaux() {
        this.M = new ArrayList<>();
        this.G = new ArrayList<>();
        this.connexion = new HashMap<>();
    }
    
    /**
     * Ajoute un generateur au reseau.
     * 
     * Si le generateur avec le meme nom existe deja, sa capacite est mise a jour.
     * Sinon, un nouveau generateur est cree et ajoute a la liste.
     * 
     * Le programme demande a l'utilisateur :
     * - Le nom du generateur
     * - Sa capacite en kW
     * 
     * Gere les exceptions :
     * - InputMismatchException : si la capacite n'est pas un entier
     * - IllegalArgumentException : si la capacite est negative ou zero
     */
    public void ajoutergenerateur() {
        System.out.println("Donner le nom du generateur et sa capacite");
        String nom = sc.next();
        int cap;
        
        try {
            cap = sc.nextInt();
            if (cap <= 0) {
                throw new IllegalArgumentException("La capacite doit etre positive !");
            }
        } catch (InputMismatchException e) {
            System.out.println("Erreur : la capacite doit etre un entier !");
            sc.nextLine();
            return;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        
        for (Generateur gen : G) {
            if (gen.getnom().equals(nom)) {
                System.out.println("Generateur deja existant. Capacite mise a jour !");
                gen.setCap(cap);
                return;
            }
        }
        
        Generateur nouveauGen = new Generateur(cap, nom);
        G.add(nouveauGen);
        System.out.println("Generateur ajoute avec succes : " + nom);
    }
    
    /**
     * Ajoute une maison au reseau.
     * 
     * Si la maison avec le meme nom existe deja, sa consommation est mise a jour.
     * Sinon, une nouvelle maison est creee et ajoutee a la liste.
     * 
     * Le programme demande a l'utilisateur :
     * - Le nom de la maison
     * - Le type de consommation : BASSE (10 kW), NORMAL (20 kW) ou FORTE (40 kW)
     * 
     * Gere les exceptions :
     * - InputMismatchException : si l'entree est invalide
     * - IllegalArgumentException : si le type de consommation est inconnu
     */
    public void ajouterMaison() {
        try {
            System.out.println("Donner le nom de la maison et sa consommation (BASSE / NORMAL / FORTE) : ");
            String nom = sc.next();
            String type = sc.next();
            Maison.ConsommationType consoType;
            
            try {
                consoType = Maison.ConsommationType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Erreur : Type inconnu. Choisissez entre BASSE, NORMAL ou FORTE.");
                return;
            }
            
            // Verifier si la maison existe deja
            for (Maison mai : this.M) {
                if (mai.getnom().equals(nom)) {
                    System.out.println("Maison deja existante. Consommation mise a jour !");
                    mai.setCons(consoType);
                    return;
                }
            }
            
            // Creer une nouvelle maison
            Maison nouvMaison = new Maison(nom, consoType);
            this.M.add(nouvMaison);
            System.out.println("Maison ajoutee avec succes : " + nom);
            
        } catch (InputMismatchException e) {
            System.out.println("Erreur : entree invalide. Format attendu: M1 NORMAL");
            sc.nextLine();
        }
    }
    
    /**
     * Cree une connexion entre une maison et un generateur.
     * 
     * Une maison ne peut etre connectee qu'a un seul generateur.
     * 
     * Le programme demande a l'utilisateur :
     * - Le nom de la maison et le nom du generateur (dans n'importe quel ordre)
     * 
     * Gere les exceptions :
     * - IllegalArgumentException : si la maison ou le generateur n'existe pas
     * - InputMismatchException : si l'entree est invalide
     */
    public void ajouterconnexion() {
        try {
            System.out.println("Donner le nom d'une maison et d'un generateur (ex: M1 G1) :");
            String nom1 = sc.next();
            String nom2 = sc.next();
            Maison maison = null;
            Generateur generateur = null;
            
            // Chercher la maison
            for (Maison m : M) {
                if (m.getnom().equals(nom1)) maison = m;
                if (m.getnom().equals(nom2)) maison = (maison == null ? m : maison);
            }
            
            // Chercher le generateur
            for (Generateur g : G) {
                if (g.getnom().equals(nom1)) generateur = g;
                if (g.getnom().equals(nom2)) generateur = (generateur == null ? g : generateur);
            }
            
            if (maison == null || generateur == null) {
                throw new IllegalArgumentException("Erreur : maison ou generateur introuvable !");
            }
            
            if (!connexion.containsKey(generateur)) {
                connexion.put(generateur, new ArrayList<Maison>());
            }
            
            connexion.get(generateur).add(maison);
            System.out.println("Connexion creee : " + maison.getnom() + " -> " + generateur.getnom());
            
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Erreur de saisie !");
            sc.nextLine();
        }
    }
    
    /**
     * Verifie que chaque maison est connectee exactement a un generateur.
     * 
     * Affiche les problemes eventuels :
     * - Maison non connectee a aucun generateur
     * - Maison connectee a plusieurs generateurs
     * 
     * Verifie egalement que le reseau contient au moins une maison et un generateur.
     * 
     * @return true si toutes les maisons sont correctement connectees, false sinon
     */
    public boolean verifierConnexions() {
        if (M.isEmpty() && G.isEmpty()) {
            System.out.println("Le reseau est vide : aucune maison et aucun generateur n'ont ete ajoutes.");
            return false;
        }
        
        if (M.isEmpty()) {
            System.out.println("Aucune maison n'a ete ajoutee !");
            return false;
        }
        
        if (G.isEmpty()) {
            System.out.println("Aucun generateur n'a ete ajoute !");
            return false;
        }
        
        boolean probleme = false;
        
        // Verifier les connexions de chaque maison
        for (Maison maison : M) {
            int count = 0;
            
            for (List<Maison> maisonsDuGen : connexion.values()) {
                if (maisonsDuGen.contains(maison)) {
                    count++;
                }
            }
            
            if (count == 0) {
                System.out.println("La maison " + maison.getnom() + " n'est connectee a aucun generateur !");
                probleme = true;
            } else if (count > 1) {
                System.out.println("La maison " + maison.getnom() + " est connectee a plusieurs generateurs !");
                probleme = true;
            }
        }
        
        if (!probleme) {
            System.out.println("Toutes les maisons sont correctement connectees !");
        }
        
        return !probleme;
    }
    
    /**
     * Calcule le taux d'utilisation d'un generateur.
     * 
     * Le taux d'utilisation est le rapport entre la charge actuelle et la capacite maximale :
     * taux = charge / capacite
     * 
     * @param g le generateur
     * @return le taux d'utilisation (entre 0 et l'infini si surcharge)
     */
    public double tauxutilisation(Generateur g) {
        double lg = 0;
        double cg = g.getcap();
        
        if (cg == 0) {
            return 0;
        }
        
        if (connexion.containsKey(g)) {
            List<Maison> maisonsDuGen = connexion.get(g);
            for (Maison m : maisonsDuGen) {
                lg += m.getcons();
            }
        }
        
        return lg / cg;
    }
    
    /**
     * Calcule la dispersion du reseau.
     * 
     * La dispersion mesure l'ecart entre le taux d'utilisation de chaque generateur et la moyenne.
     * Formule : Disp = somme(|u_g - u_moyenne|)
     * 
     * @return la dispersion totale (0 si aucune connexion)
     */
    public double Disp() {
        if (G.isEmpty() || connexion.isEmpty()) {
            System.out.println("Aucune connexion active - dispersion = 0");
            return 0;
        }
        
        double uMoyenne = 0;
        Map<Generateur, Double> taux = new HashMap<>();
        
        // Calculer le taux d'utilisation de chaque generateur
        for (Generateur g : this.G) {
            double u = tauxutilisation(g);
            taux.put(g, u);
            uMoyenne += u;
        }
        
        uMoyenne /= this.G.size();
        
        // Calculer la dispersion
        double dispersion = 0;
        for (Generateur g : this.G) {
            dispersion += Math.abs(taux.get(g) - uMoyenne);
        }
        
        return dispersion;
    }
    
    /**
     * Calcule la surcharge du reseau.
     * 
     * La surcharge mesure le depassement de capacite de chaque generateur.
     * Pour chaque generateur : surcharge = max(0, (charge - capacite) / capacite)
     * La surcharge totale est la somme de toutes les surcharges.
     * 
     * @return la surcharge totale (0 si aucun generateur n'est surcharge)
     */
    public double surcharge() {
        double surcharge = 0.0;
        
        // Parcourir tous les generateurs
        for (Generateur g : this.G) {
            double totalConsommation = 0.0;
            double capaciteMax = g.getcap();
            
            // Recuperer les maisons connectees a ce generateur
            List<Maison> maisonsConnectees = connexion.get(g);
            if (maisonsConnectees != null) {
                for (Maison m : maisonsConnectees) {
                    totalConsommation += m.getcons();
                }
            }
            
            // Ajouter la surcharge uniquement si le generateur est depasse
            surcharge += Math.max(0.0, (totalConsommation - capaciteMax) / capaciteMax);
        }
        
        return surcharge;
    }
    
    /**
     * Calcule le cout total du reseau.
     * 
     * Le cout combine deux criteres :
     * - La dispersion : mesure de l'equilibre entre les generateurs
     * - La surcharge : penalisation des depassements de capacite
     * 
     * Formule : Cout = Disp + lambda * Surcharge
     * 
     * @return le cout total du reseau (0 si aucune connexion)
     */
    public double calculercout() {
        if (connexion.isEmpty()) {
            System.out.println("Aucune connexion existante. Le cout ne peut pas etre calcule !");
            return 0;
        }
        
        double dispersion = Disp();
        double Surcharge = surcharge();
        return dispersion + lambda * Surcharge;
    }
    
    /**
     * Modifie une connexion existante entre une maison et un generateur.
     * 
     * Le programme demande :
     * 1. La connexion actuelle a modifier (maison et generateur)
     * 2. La nouvelle connexion (maison et nouveau generateur)
     * 
     * La maison reste la meme, seul le generateur change.
     * 
     * Gere les exceptions :
     * - IllegalArgumentException : si la connexion n'existe pas ou si la nouvelle est invalide
     * - InputMismatchException : si l'entree est invalide
     */
    public void modification() {
        try {
            System.out.println("Veuillez saisir la connexion a modifier (ex: M1 G1) :");
            String nom1 = sc.next();
            String nom2 = sc.next();
            
            Maison maisonExistante = null;
            Generateur genExistante = null;
            
            // Identifier la maison et le generateur dans la saisie
            for (Maison m : M) {
                if (m.getnom().equals(nom1) || m.getnom().equals(nom2)) maisonExistante = m;
            }
            for (Generateur g : G) {
                if (g.getnom().equals(nom1) || g.getnom().equals(nom2)) genExistante = g;
            }
            
            // Verifier que la connexion existe
            if (maisonExistante == null || genExistante == null || !connexion.containsKey(genExistante) || 
                !connexion.get(genExistante).contains(maisonExistante)) {
                throw new IllegalArgumentException("Erreur : la connexion indiquee n'existe pas !");
            }
            
            System.out.println("Veuillez saisir la nouvelle connexion (ex: M1 G2) :");
            String nnom1 = sc.next();
            String nnom2 = sc.next();
            
            Maison maisonNouvelle = null;
            Generateur genNouvelle = null;
            
            for (Maison m : this.M) {
                if (m.getnom().equals(nnom1) || m.getnom().equals(nnom2)) maisonNouvelle = m;
            }
            for (Generateur g : this.G) {
                if (g.getnom().equals(nnom1) || g.getnom().equals(nnom2)) genNouvelle = g;
            }
            
            if (maisonNouvelle == null || genNouvelle == null) {
                throw new IllegalArgumentException("Erreur : la nouvelle connexion est invalide !");
            }
            
            // Mettre a jour la connexion
            connexion.get(genExistante).remove(maisonExistante);
            connexion.putIfAbsent(genNouvelle, new ArrayList<>());
            connexion.get(genNouvelle).add(maisonExistante);
            System.out.println("Connexion modifiee : " + maisonNouvelle.getnom() + " -> " + genNouvelle.getnom());
            
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Erreur de saisie !");
            sc.nextLine();
        }
    }
    
    /**
     * Retourne la map des connexions du reseau.
     * 
     * @return la map generateur -> liste de maisons
     */
    public Map<Generateur, List<Maison>> getConnexions() {
        return connexion;
    }
    
    /**
     * Retourne la liste des maisons du reseau.
     * 
     * @return liste des maisons
     */
    public List<Maison> getM() {
        return M;
    }
    
    /**
     * Retourne la liste des generateurs du reseau.
     * 
     * @return liste des generateurs
     */
    public List<Generateur> getG() {
        return G;
    }
    
    /**
     * Affiche l'etat actuel du reseau.
     * 
     * Affiche dans l'ordre :
     * 1. La liste des generateurs avec leurs capacites
     * 2. La liste des maisons avec leurs consommations
     * 3. La liste des connexions
     */
    public void afficherReseau() {
        System.out.println("\n=== RESEAU ELECTRIQUE ACTUEL ===\n");
        
        // Affichage des generateurs
        System.out.println("Generateurs :");
        if (G.isEmpty()) {
            System.out.println("  Aucun generateur defini.");
        } else {
            for (Generateur g : this.G) {
                System.out.println("  - " + g.getnom() + " : capacite = " + g.getcap() + " kW");
            }
        }
        
        // Affichage des maisons
        System.out.println("\nMaisons :");
        if (M.isEmpty()) {
            System.out.println("  Aucune maison definie.");
        } else {
            for (Maison m : this.M) {
                System.out.println("  - " + m.getnom() + " : consommation = " + m.getcons() + " kW");
            }
        }
        
        // Affichage des connexions
        System.out.println("\nConnexions :");
        if (connexion.isEmpty()) {
            System.out.println("  Aucune connexion definie.");
        } else {
            for (Map.Entry<Generateur, List<Maison>> entry : connexion.entrySet()) {
                Generateur g = entry.getKey();
                List<Maison> maisons = entry.getValue();
                for (Maison m : maisons) {
                    System.out.println("  - " + m.getnom() + " -> " + g.getnom());
                }
            }
        }
        
        System.out.println("\n=== FIN DU RESEAU ===\n");
    }
    
    /**
     * Supprime une connexion existante entre une maison et un generateur.
     * 
     * Le programme demande a l'utilisateur :
     * - Le nom de la maison et du generateur a deconnecter
     * 
     * Gere les exceptions :
     * - IllegalArgumentException : si la maison, le generateur ou la connexion n'existe pas
     * - InputMismatchException : si l'entree est invalide
     */
    public void supprimerConnexion() {
        try {
            System.out.println("Donner le nom d'une maison et d'un generateur a deconnecter (ex: M1 G1) :");
            String nom1 = sc.next();
            String nom2 = sc.next();
            
            Maison maison = null;
            Generateur generateur = null;
            
            // Identifier la maison et le generateur (peu importe l'ordre)
            for (Maison m : this.M) {
                if (m.getnom().equals(nom1) || m.getnom().equals(nom2)) maison = m;
            }
            for (Generateur g : this.G) {
                if (g.getnom().equals(nom1) || g.getnom().equals(nom2)) generateur = g;
            }
            
            // Verifier que la maison et le generateur existent
            if (maison == null || generateur == null) {
                throw new IllegalArgumentException("Erreur : maison ou generateur introuvable !");
            }
            
            // Verifier que la connexion existe
            if (!connexion.containsKey(generateur) || !connexion.get(generateur).contains(maison)) {
                throw new IllegalArgumentException("Erreur : la connexion entre " 
                        + maison.getnom() + " et " + generateur.getnom() + " n'existe pas !");
            }
            
            // Supprimer la connexion
            connexion.get(generateur).remove(maison);
            System.out.println("Connexion supprimee : " + maison.getnom() + " - " + generateur.getnom());
            
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Erreur de saisie !");
            sc.nextLine();
        }
    }
    
    /**
     * Cree une copie profonde du reseau actuel.
     * 
     * Les listes et la map sont copiees de maniere independante.
     * 
     * @return une nouvelle instance de Reseaux avec les memes donnees
     */
    public Reseaux copier() {
        List<Maison> nouvellesMaisons = new ArrayList<>(this.M);
        List<Generateur> nouveauxGen = new ArrayList<>(this.G);
        
        // Copier la Map<Generateur, List<Maison>>
        Map<Generateur, List<Maison>> nouvellesConnexions = new HashMap<>();
        for (Map.Entry<Generateur, List<Maison>> entry : this.connexion.entrySet()) {
            nouvellesConnexions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        
        return new Reseaux(nouvellesMaisons, nouveauxGen, nouvellesConnexions);
    }
    
    /**
     * Change la connexion d'une maison vers un nouveau generateur.
     * 
     * La maison est deconnectee de son generateur actuel et connectee au nouveau generateur.
     * 
     * @param m la maison dont on change la connexion
     * @param nouveauGen le nouveau generateur
     * @throws IllegalArgumentException si la maison ou le generateur n'existe pas
     */
    public void changeConnection(Maison m, Generateur nouveauGen) {
        if (!M.contains(m) || !G.contains(nouveauGen)) {
            throw new IllegalArgumentException("Maison ou generateur non existant");
        }
        
        // Chercher l'ancien generateur de cette maison et retirer la maison
        for (Map.Entry<Generateur, List<Maison>> entry : connexion.entrySet()) {
            List<Maison> maisons = entry.getValue();
            if (maisons.contains(m)) {
                maisons.remove(m);
                break;
            }
        }
        
        // Verifier si le nouveau generateur a deja une liste, sinon creer
        if (!connexion.containsKey(nouveauGen)) {
            connexion.put(nouveauGen, new ArrayList<Maison>());
        }
        
        // Ajouter la maison au nouveau generateur
        connexion.get(nouveauGen).add(m);
    }
    
    /**
     * Modifie le parametre lambda (severite de la penalisation).
     * 
     * Lambda controle l'importance relative de la surcharge dans le calcul du cout.
     * Un lambda eleve penalise davantage les surcharges.
     * 
     * @param l la nouvelle valeur de lambda
     */
    public static void setLambda(double l) {
        lambda = l;
    }
}






