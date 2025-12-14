package partie2;

import partie1.Maison;
import partie1.Generateur;
import partie1.Reseaux;
import java.util.*;

/**
 * Classe d'automatisation pour l'optimisation du reseau electrique.
 * 
 * Cette classe implementa un algorithme d'optimisation multi-phases sophistique
 * qui combine plusieurs strategies pour minimiser le cout total du reseau.
 * 
 * L'objectif est de minimiser la fonction de cout :
 * Cout(S) = Dispersion(S) + lambda * Surcharge(S)
 * 
 * Ou :
 * - Dispersion : mesure l'equilibre entre les taux d'utilisation des generateurs
 * - Surcharge : penalise les generateurs qui depassent leur capacite
 * - Lambda : parametre controle la severite de la penalisation
 * 
 * Phases d'optimisation implementees :
 * 1. Initialisation optimale : connecte chaque maison au meilleur generateur initial
 * 2. Optimisation gloutonne : traite les maisons par consommation decroissante
 * 3. Recherche locale intensive : exploration aleatoire et exhaustive
 * 4. Echanges de paires (2-opt) : echange simultanement 2 maisons pour sortir des minima locaux
 * 5. Passes exhaustives : garantit l'atteinte d'un optimum local
 * 
 * Nombre d'iterations automatique :
 * Le nombre d'iterations est calcule dynamiquement selon la taille du reseau :
 * k = nbMaisons * 300 * nbGenerateurs
 * Bornes : minimum 2000, maximum 50000
 * 
 * @author Massilva Djennadi
 * @author Ines Meslem
 * @author Lizaveta Dzemchankova
 */

public class Automatisation {
	
	/**
     * Le reseau electrique a optimiser.
     * Contient les listes de maisons et de generateurs, ainsi que les connexions.
     */
    private Reseaux reseau;
    
    /**
     * Nombre d'iterations pour l'algorithme d'optimisation.
     * Controle la duree et l'intensite de la recherche.
     * Peut etre fixe ou calcule automatiquement selon la taille du reseau.
     */
    private int k;
    
    /**
     * Generateur de nombres aleatoires.
     * Utilise une seed fixe (42) pour garantir la reproductibilite des resultats.
     */
    private Random random;
    
    /**
     * Nombre minimum d'iterations pour les petits reseaux.
     * Garantit une exploration minimale meme pour les instances triviales.
     */
    private static final int ITERATIONS_MIN = 2000;
    
    /**
     * Coefficient multiplicateur pour le calcul d'iterations.
     * Plus il y a de maisons, plus on a besoin d'iterations pour bien les explorer.
     */
    private static final int ITERATIONS_PAR_MAISON = 300;
    
    /**
     * Nombre maximum d'iterations pour eviter des temps de calcul trop longs.
     * Protege contre les reseaux tres grands qui necessiteraient trop d'iterations.
     */
    private static final int ITERATIONS_MAX = 50000;
    
    /**
     * Constructeur avec nombre d'iterations explicite.
     * 
     * Utilise quand l'utilisateur veut avoir un controle precis sur le nombre d'iterations
     * de l'algorithme d'optimisation.
     * 
     * Exemple :
     * Automatisation auto = new Automatisation(reseau, 5000);
     * // Lance l'optimisation avec exactement 5000 iterations
     * 
     * @param reseau le reseau electrique a optimiser.
     *               Ne peut pas etre null. Doit contenir au moins une maison
     *               et un generateur, avec des connexions valides.
     * 
     * @param k le nombre d'iterations souhaite.
     *          Doit etre strictement positif. Plus k est grand, plus la recherche
     *          sera intensive et plus le resultat devrait etre bon,
     *          au detriment du temps de calcul.
     *          Valeurs recommandees : 1000 a 10000.
     */
    public Automatisation(Reseaux reseau, int k) {
        this.reseau = reseau;
        this.k = k;
        this.random = new Random(42);
    }
    
    /**
     * Constructeur avec calcul automatique du nombre d'iterations.
     * 
     * Le nombre d'iterations est adapte a la taille du reseau pour un bon compromis
     * entre qualite de la solution et temps de calcul.
     * 
     * Formule de calcul : k = nbMaisons * 300 * nbGenerateurs
     * Cette formule garantit que :
     * - Plus il y a de maisons, plus d'iterations (plus d'elements a optimiser)
     * - Plus il y a de generateurs, plus d'iterations (plus de choix possibles)
     * - Le nombre reste borne entre 2000 et 50000
     * 
     * Exemple :
     * - Reseau de 5 maisons et 2 generateurs : k = 5 * 300 * 2 = 3000 iterations
     * - Reseau de 10 maisons et 5 generateurs : k = 10 * 300 * 5 = 15000 iterations
     * 
     * @param reseau le reseau electrique a optimiser.
     *               Ne peut pas etre null. Doit contenir au moins une maison
     *               et un generateur, avec des connexions valides.
     */
    public Automatisation(Reseaux reseau) {
        this.reseau = reseau;
        this.k = calculerNombreIterations(reseau);
        this.random = new Random(42);
    }
    
    /**
     * Calcule automatiquement le nombre d'iterations optimal en fonction de la taille du reseau.
     * 
     * Cette methode determine combien d'iterations l'algorithme doit effectuer
     * pour explorer l'espace de solutions de maniere adequate.
     * 
     * Logique :
     * - Chaque maison doit etre consideree plusieurs fois (300 fois)
     * - Plus il y a de generateurs, plus l'espace de recherche grandit
     * - Donc le nombre d'iterations croit avec nbMaisons * nbGenerateurs
     * 
     * Formule : iterations = nbMaisons * 300 * nbGenerateurs
     * 
     * Exemple de calcul :
     * - 3 maisons, 2 generateurs : 3 * 300 * 2 = 1800 -> arrondi a min 2000
     * - 5 maisons, 3 generateurs : 5 * 300 * 3 = 4500
     * - 20 maisons, 5 generateurs : 20 * 300 * 5 = 30000
     * 
     * Les bornes (min et max) garantissent que :
     * - Les petits reseaux ont suffisamment d'iterations pour etre bien explores
     * - Les grands reseaux ne prennent pas un temps infini a optimiser
     * 
     * @param reseau le reseau electrique contenant les maisons et generateurs
     * 
     * @return le nombre d'iterations recommande pour ce reseau
     *         Toujours entre ITERATIONS_MIN (2000) et ITERATIONS_MAX (50000)
     */
    private int calculerNombreIterations(Reseaux reseau) {
        int nbMaisons = reseau.getM().size();
        int nbGenerateurs = reseau.getG().size();
        
        int iterations = nbMaisons * ITERATIONS_PAR_MAISON * nbGenerateurs;
        iterations = Math.max(ITERATIONS_MIN, iterations);
        iterations = Math.min(ITERATIONS_MAX, iterations);
        
        return iterations;
    }
    
    /**
     * Methode principale de resolution du probleme d'optimisation.
     * 
     * Cette methode execute l'algorithme multi-phases complet pour optimiser le reseau.
     * Elle affiche des informations detaillees pendant et apres l'optimisation.
     * 
     * Execution complete :
     * 
     * 1. PHASE 0 - Initialisation optimale :
     *    Pour chaque maison, trouve le meilleur generateur possible.
     *    Cela fournit un bon point de depart pour les phases suivantes.
     * 
     * 2. PHASE 1 - Optimisation gloutonne :
     *    Traite les maisons dans l'ordre decroissant de consommation.
     *    Les maisons les plus consommatrices sont affectees en premier,
     *    ce qui tend a creer une bonne repartition initiale.
     * 
     * 3. PHASE 2 - Recherche locale intensive :
     *    Explore aleatoirement le voisinage de chaque maison.
     *    Choisit un generateur au hasard et teste tous les autres.
     *    Permet d'echapper aux premieres pentes locales.
     * 
     * 4. PHASE 3 - Echanges de paires (2-opt) :
     *    Echange simultanement 2 maisons entre leurs generateurs.
     *    Peut debloquer des situations ou aucun mouvement simple n'ameliore.
     * 
     * 5. PHASE 4 - Passes exhaustives multi-niveaux :
     *    Teste systematiquement chaque maison avec chaque generateur.
     *    S'arrete quand une passe complete ne trouve aucune amelioration.
     *    Garantit l'atteinte d'un optimum local.
     * 
     * Affichage :
     * - Information sur le reseau (nombre de maisons et generateurs)
     * - Cout initial et couts intermediaires
     * - Progression de chaque phase
     * - Resultats finaux avec amelioration en pourcentage
     * - Statistiques detaillees par generateur
     * 
     * Preconditions :
     * - Le reseau doit contenir au moins une maison et un generateur
     * - Toutes les maisons doivent etre connectees a un generateur
     * 
     * Postconditions :
     * - Le reseau a ete modifie avec les nouvelles connexions optimales
     * - Le cout final est inferieur ou egal au cout initial
     * - Aucun mouvement simple ne peut ameliorer le resultat (optimum local)
     */

    public void resoudre() {
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();
        
        if (maisons.isEmpty() || generateurs.isEmpty()) {
            System.out.println("Erreur : le reseau est vide.");
            return;
        }
        
        System.out.println("       OPTIMISATION AUTOMATIQUE DU RESEAU");
        System.out.println("Reseau : " + maisons.size() + " maisons, " + generateurs.size() + " generateurs");
        System.out.println("Nombre d'iterations : " + k);
        
        double coutInitial = reseau.calculercout();
        System.out.println("Cout initial : " + String.format("%.15f", coutInitial));
        
        int ameliorationsTotal = 0;
        
        // PHASE 0 : Initialisation optimale
        System.out.println("\nPhase 0 : Initialisation optimale");
        ameliorationsTotal += phaseInitialisationOptimale();
        
        // PHASE 1 : Optimisation gloutonne
        System.out.println("\nPhase 1 : Optimisation gloutonne");
        ameliorationsTotal += phaseGloutonne();
        
        // PHASE 2 : Recherche locale intensive
        System.out.println("\nPhase 2 : Recherche locale intensive");
        ameliorationsTotal += rechercheLocaleIntensive(k / 2);
        
        // PHASE 3 : Echanges de paires (2-opt)
        System.out.println("\nPhase 3 : Echanges de paires (2-opt)");
        ameliorationsTotal += echangesPaires(k / 2);
        
        // PHASE 4 : Passes exhaustives multi-niveaux
        System.out.println("\nPhase 4 : Passes exhaustives multi-niveaux");
        ameliorationsTotal += passesExhaustivesMultiNiveaux(100);
        
        double coutFinal = reseau.calculercout();
        
        System.out.println("\n              RESULTATS DE L'OPTIMISATION");
        System.out.printf("Cout initial    : %.15f\n", coutInitial);
        System.out.printf("Cout final      : %.15f\n", coutFinal);
        System.out.printf("Reduction       : %.15f\n", coutInitial - coutFinal);
        
        if (coutInitial > 0) {
            double ameliorationPct = ((coutInitial - coutFinal) / coutInitial) * 100;
            System.out.printf("Amelioration    : %.2f%%\n", ameliorationPct);
        }
        
        System.out.printf("Modifications   : %d changements de connexion\n", ameliorationsTotal);
        
        afficherStatistiquesReseau();
    }
    
    /**
     * PHASE 0 : Initialisation optimale du reseau.
     * 
     * Cette phase initialise chaque maison en la connectant au generateur
     * qui minimise le cout total du reseau.
     * 
     * Processus :
     * Pour chaque maison :
     *   1. Enregistrer son generateur actuel (peut etre null)
     *   2. Calculer le cout initial
     *   3. Tester chaque generateur possible
     *   4. Garder le generateur qui donne le cout minimal
     *   5. Si c'est different du generateur initial, effectuer le changement
     * 
     * Interet :
     * - Fournit un bon point de depart pour les autres phases
     * - Evite les configurations aberrantes initiales
     * - Peu couteux en comparaison a d'autres phases
     * 
     * Complexite :
     * O(nbMaisons * nbGenerateurs * coutCalcul)
     * Couteux mais ne se fait qu'une fois au debut
     * 
     * @return le nombre de connexions qui ont ete changees
     */
    private int phaseInitialisationOptimale() {
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();
        int ameliorations = 0;
        
        // Pour chaque maison, trouver le meilleur generateur initial
        for (Maison m : maisons) {
            Generateur ancienGen = trouverGenerateurDeMaison(m);
            double coutActuel = reseau.calculercout();
            
            Generateur meilleurGen = ancienGen;
            double meilleurCout = coutActuel;
            
            for (Generateur g : generateurs) {
                if (ancienGen != null && g.equals(ancienGen)) continue;
                
                reseau.changeConnection(m, g);
                double nouveauCout = reseau.calculercout();
                
                if (nouveauCout < meilleurCout) {
                    meilleurCout = nouveauCout;
                    meilleurGen = g;
                }
                
                if (ancienGen != null) {
                    reseau.changeConnection(m, ancienGen);
                } else {
                    // Retirer la maison du generateur temporaire
                    for (Generateur gen : generateurs) {
                        if (gen.equals(g)) {
                            Map<Generateur, List<Maison>> connexions = reseau.getConnexions();
                            if (connexions.containsKey(g)) {
                                connexions.get(g).remove(m);
                            }
                        }
                    }
                }
            }
            
            if (meilleurGen != ancienGen && meilleurCout < coutActuel) {
                reseau.changeConnection(m, meilleurGen);
                ameliorations++;
            }
        }
        
        System.out.println("  Initialisation : " + ameliorations + " connexions optimisees");
        return ameliorations;
    }
    
    /**
     * PHASE 1 : Optimisation gloutonne.
     * 
     * Cette phase utilise une strategie gloutonne : traiter les elements les plus
     * "difficiles" en premier tends a creer une bonne solution initiale.
     * 
     * Strategie :
     * 1. Trier les maisons par consommation decroissante
     *    (Les plus grandes consommatrices d'abord)
     * 2. Pour chaque maison dans cet ordre :
     *    - Tester tous les generateurs disponibles
     *    - Choisir celui qui minimise le cout global
     * 
     * Intuition :
     * Les maisons with forte consommation sont plus difficiles a placer
     * car elles impactent beaucoup plus le cout. Les placer en premier
     * avec le meilleur choix possible tend a equilibrer le reseau rapidement.
     * 
     * Avantages :
     * - Obtient une bonne solution rapidement
     * - Solution mieux que l'initialisation aleatoire
     * - Fournit un bon point de depart pour les phases suivantes
     * 
     * Complexite :
     * O(nbMaisons * nbGenerateurs * coutCalcul)
     * Similar a Phase 0 mais avec un ordre intelligent
     * 
     * @return le nombre de connexions qui ont ete ameliorees
     */
    private int phaseGloutonne() {
        List<Maison> maisons = new ArrayList<>(reseau.getM());
        List<Generateur> generateurs = reseau.getG();
        
        // Trier par consommation decroissante
        maisons.sort((m1, m2) -> Double.compare(m2.getcons(), m1.getcons()));
        
        int ameliorations = 0;
        
        for (Maison m : maisons) {
            Generateur ancienGen = trouverGenerateurDeMaison(m);
            double coutActuel = reseau.calculercout();
            
            Generateur meilleurGen = ancienGen;
            double meilleurCout = coutActuel;
            
            for (Generateur g : generateurs) {
                if (ancienGen != null && g.equals(ancienGen)) continue;
                
                reseau.changeConnection(m, g);
                double nouveauCout = reseau.calculercout();
                
                if (nouveauCout < meilleurCout) {
                    meilleurCout = nouveauCout;
                    meilleurGen = g;
                }
                
                if (ancienGen != null) {
                    reseau.changeConnection(m, ancienGen);
                }
            }
            
            if (meilleurGen != ancienGen && meilleurCout < coutActuel) {
                reseau.changeConnection(m, meilleurGen);
                ameliorations++;
            }
        }
        
        System.out.println("  Gloutonne : " + ameliorations + " ameliorations");
        return ameliorations;
    }
    
    /**
     * PHASE 2 : Recherche locale intensive avec exploration aleatoire.
     * 
     * Cette phase explore le voisinage de chaque maison de maniere intensive
     * en choisissant des maisons aleatoirement.
     * 
     * Strategie :
     * Pour chaque iteration :
     *   1. Choisir une maison aleatoirement
     *   2. Tester tous les generateurs pour cette maison
     *   3. Si une amelioration est trouvee, l'appliquer
     *   4. Continuer jusqu'a k/2 iterations ou 100 iterations sans amelioration
     * 
     * Aspect aleatoire :
     * L'aspect aleatoire permet d'explorer differentes parties du reseau
     * sans biais d'ordre, ce qui ameliore la qualite de la solution.
     * Cela aide aussi a echapper aux minima locaux en explorant des directions
     * que les phases precedentes n'auraient pas considerees.
     * 
     * Arret anticipé :
     * La phase s'arrete apres 100 iterations sans amelioration,
     * ce qui evite de gaspiller du temps quand la recherche devient stagnante.
     * 
     * Complexite :
     * O(k/2 * nbGenerateurs * coutCalcul)
     * Moins couteux que les phases precedentes car on teste moins de maisons
     * 
     * @param iterations le nombre maximum d'iterations a effectuer
     * @return le nombre de connexions qui ont ete ameliorees
     */
    private int rechercheLocaleIntensive(int iterations) {
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();
        int ameliorations = 0;
        int nonAmeliorations = 0;
        
        for (int i = 0; i < iterations && nonAmeliorations < 100; i++) {
            Maison m = maisons.get(random.nextInt(maisons.size()));
            Generateur ancienGen = trouverGenerateurDeMaison(m);
            
            if (ancienGen == null) continue;
            
            double coutActuel = reseau.calculercout();
            Generateur meilleurGen = ancienGen;
            double meilleurCout = coutActuel;
            
            for (Generateur g : generateurs) {
                if (g.equals(ancienGen)) continue;
                
                reseau.changeConnection(m, g);
                double nouveauCout = reseau.calculercout();
                
                if (nouveauCout < meilleurCout) {
                    meilleurCout = nouveauCout;
                    meilleurGen = g;
                }
                
                reseau.changeConnection(m, ancienGen);
            }
            
            if (meilleurCout < coutActuel) {
                reseau.changeConnection(m, meilleurGen);
                ameliorations++;
                nonAmeliorations = 0;
            } else {
                nonAmeliorations++;
            }
        }
        
        System.out.println("  Recherche locale : " + ameliorations + " ameliorations");
        return ameliorations;
    }
    
    /**
     * PHASE 3 : Echanges de paires (2-opt).
     * 
     * Cette phase explore les mouvements de type 2-opt : echanger simultanement
     * deux maisons entre leurs generateurs respectifs.
     * 
     * Principe du 2-opt :
     * Parfois, aucun mouvement simple (changer une seule maison) n'ameliore le cout,
     * MAIS echanger simultanement 2 maisons peut debloquer la situation.
     * 
     * Exemple :
     * Avant :  maison1 -> gen1,  maison2 -> gen2
     * Apres :  maison1 -> gen2,  maison2 -> gen1
     * 
     * Cet echange peut ameliorer l'equilibre global meme si changer une seule
     * maison degraderait le cout.
     * 
     * Strategie :
     * Pour chaque iteration :
     *   1. Choisir deux maisons aleatoirement (differentes)
     *   2. Echanger leurs generateurs
     *   3. Si c'est une amelioration, garder l'echange
     *   4. Sinon, annuler l'echange et essayer la prochaine iteration
     * 
     * Arret anticipé :
     * S'arrete apres 500 iterations sans amelioration.
     * Cela economise du temps quand les echanges de paires ne sont plus utiles.
     * 
     * Complexite :
     * O(k/2 * coutCalcul)
     * Moins couteux car on ne teste que 2 mouvements par iteration
     * 
     * @param iterations le nombre maximum d'iterations a effectuer
     * @return le nombre d'echanges de paires reussis
     */
    private int echangesPaires(int iterations) {
        List<Maison> maisons = reseau.getM();
        int ameliorations = 0;
        int nonAmeliorations = 0;
        
        for (int i = 0; i < iterations && maisons.size() >= 2 && nonAmeliorations < 500; i++) {
            Maison m1 = maisons.get(random.nextInt(maisons.size()));
            Maison m2;
            do {
                m2 = maisons.get(random.nextInt(maisons.size()));
            } while (m1.equals(m2));
            
            Generateur gen1 = trouverGenerateurDeMaison(m1);
            Generateur gen2 = trouverGenerateurDeMaison(m2);
            
            if (gen1 == null || gen2 == null || gen1.equals(gen2)) continue;
            
            double coutActuel = reseau.calculercout();
            
            reseau.changeConnection(m1, gen2);
            reseau.changeConnection(m2, gen1);
            
            double nouveauCout = reseau.calculercout();
            
            if (nouveauCout < coutActuel) {
                ameliorations++;
                nonAmeliorations = 0;
            } else {
                reseau.changeConnection(m1, gen1);
                reseau.changeConnection(m2, gen2);
                nonAmeliorations++;
            }
        }
        
        System.out.println("  Echanges de paires : " + ameliorations + " ameliorations");
        return ameliorations;
    }
    
    /**
     * PHASE 4 : Passes exhaustives multi-niveaux.
     * 
     * Cette phase est la finale et la plus intensive. Elle effectue des passes
     * completes du reseau en testant systematiquement tous les mouvements possibles.
     * 
     * Objectif :
     * Garantir que le reseau a atteint un optimum local, c'est-a-dire qu'aucun
     * mouvement simple ne peut ameliorer le cout.
     * 
     * Strategie :
     * Pour chaque passe jusqu'a maxPasses :
     *   1. Pour chaque maison :
     *      - Tester chaque generateur
     *      - Appliquer immediatement toute amelioration trouvee
     *   2. Si la passe n'a trouve aucune amelioration, s'arreter
     *      (l'optimum local a ete atteint)
     * 
     * Pourquoi exhaustif ?
     * Les phases precedentes sont heuristiques et peuvent manquer des mouvements
     * simples. Cette phase systematique garantit que tous les mouvements
     * simples ont ete explores.
     * 
     * Arret automatique :
     * La phase s'arrete des qu'une passe complete ne trouve aucune amelioration.
     * C'est le signal que l'optimum local a ete atteint.
     * 
     * Complexite :
     * O(nbPasses * nbMaisons * nbGenerateurs * coutCalcul)
     * Tres couteux mais garantit l'optimum local
     * 
     * @param maxPasses le nombre maximum de passes a effectuer
     * @return le nombre total d'ameliorations effectuees dans toutes les passes
     */
    private int passesExhaustivesMultiNiveaux(int maxPasses) {
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();
        int ameliorationsTotal = 0;
        
        for (int passe = 1; passe <= maxPasses; passe++) {
            int ameliorationsCePasse = 0;
            
            // Mouvements simples exhaustifs
            for (Maison m : maisons) {
                Generateur ancienGen = trouverGenerateurDeMaison(m);
                if (ancienGen == null) continue;
                
                double coutActuel = reseau.calculercout();
                Generateur meilleurGen = ancienGen;
                double meilleurCout = coutActuel;
                
                for (Generateur g : generateurs) {
                    if (g.equals(ancienGen)) continue;
                    
                    reseau.changeConnection(m, g);
                    double nouveauCout = reseau.calculercout();
                    
                    if (nouveauCout < meilleurCout) {
                        meilleurCout = nouveauCout;
                        meilleurGen = g;
                    }
                    
                    reseau.changeConnection(m, ancienGen);
                }
                
                if (meilleurCout < coutActuel) {
                    reseau.changeConnection(m, meilleurGen);
                    ameliorationsCePasse++;
                    ameliorationsTotal++;
                }
            }
            
            if (ameliorationsCePasse == 0) {
                System.out.println("  Passes exhaustives : optimum atteint apres " + passe + " passes");
                break;
            }
        }
        
        return ameliorationsTotal;
    }
    
    // === Methode utilitaire ===
    /**
     * Recherche et identifie le générateur qui alimente actuellement une maison donnée.
     * <p>
     * Cette méthode parcourt la carte des connexions actuelles du réseau.
     * </p>
     *
     * @param m La maison dont on cherche la source d'énergie.
     * @return L'objet {@link Generateur} auquel la maison est connectée, ou {@code null} si la maison est orpheline.
     */
    private Generateur trouverGenerateurDeMaison(Maison m) {
        Map<Generateur, List<Maison>> connexions = reseau.getConnexions();
        
        for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
            if (entry.getValue().contains(m)) {
                return entry.getKey();
            }
        }
        
        return null;
    }
    /**
     * Affiche dans la console un rapport détaillé sur l'état de charge de chaque générateur.
     * <p>
     * Pour chaque générateur, affiche :
     * <ul>
     * <li>Son nom/identifiant.</li>
     * <li>La consommation actuelle cumulée (charge) vs sa capacité maximale.</li>
     * <li>Le pourcentage d'utilisation de sa capacité.</li>
     * <li>Le nombre de maisons connectées.</li>
     * </ul>
     * Utile pour vérifier l'équilibrage de charge ou la saturation du réseau.
     * </p>
     */
    private void afficherStatistiquesReseau() {
        List<Generateur> generateurs = reseau.getG();
        Map<Generateur, List<Maison>> connexions = reseau.getConnexions();
        
        System.out.println("\nStatistiques detaillees du reseau optimise :");
        
        for (Generateur g : generateurs) {
            List<Maison> maisons = connexions.get(g);
            double charge = 0;
            int nbMaisons = 0;
            
            if (maisons != null) {
                nbMaisons = maisons.size();
                for (Maison m : maisons) {
                    charge += m.getcons();
                }
            }
            
            double capacite = g.getcap();
            double taux = capacite > 0 ? (charge / capacite) * 100 : 0;
            
            System.out.printf("  %s : %.0f/%.0f kW (%.1f%%) - %d maisons\n",
                g.getnom(), charge, capacite, taux, nbMaisons);
        }
    }
    
    /**
     * @return L'instance du réseau ({@link Reseaux}) actuellement manipulée par cette classe.
     */
    public Reseaux getReseau() {
        return reseau;
    }
    
    /**
     * Définit le paramètre K (souvent utilisé pour les algorithmes type k-means ou k-nearest).
     * La valeur n'est mise à jour que si elle est strictement positive.
     *
     * @param k La nouvelle valeur de k (doit être > 0).
     */
    public void setK(int k) {
        if (k > 0) {
            this.k = k;
        }
    }
    
    /**
     * @return La valeur actuelle du paramètre k.
     */
    public int getK() {
        return k;
    }
}