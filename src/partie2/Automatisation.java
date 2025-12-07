package partie2;

import partie1.Maison;
import partie1.Generateur;
import partie1.Reseaux;
import java.util.*;

/**
 * Classe d'automatisation pour l'optimisation du réseau électrique.
 * 
 * Cette classe implémente un algorithme d'optimisation multi-phases combinant :
 * - Optimisation gloutonne (priorité aux maisons les plus consommatrices)
 * - Recherche locale intensive (test systématique de toutes les affectations)
 * - Échanges de paires (2-opt) pour sortir des minima locaux
 * - Échanges de triplets (3-opt) pour explorer des configurations complexes
 * - Passes exhaustives multi-niveaux pour garantir l'optimum local
 * 
 * L'objectif est de minimiser le coût total du réseau :
 * Coût = Dispersion + λ × Surcharge
 * 
 * @author Massilva Djennadi
 * @author Ines Meslem
 * @author Lizaveta Dzemchankova
 */
public class Automatisation {
    // === Attributs de la classe ===
    
    /** Le réseau électrique à optimiser */
    private Reseaux reseau;
    
    /** Nombre maximum d'itérations pour l'algorithme */
    private int k;
    
    /** Générateur de nombres aléatoires pour l'exploration stochastique */
    private Random random;
    
    // === Constantes pour le calcul automatique des itérations ===
    
    /** Nombre minimum d'itérations (pour les petits réseaux) */
    private static final int ITERATIONS_MIN = 1000;
    
    /** Coefficient multiplicateur : nombre d'itérations par maison */
    private static final int ITERATIONS_PAR_MAISON = 150;
    
    /** Nombre maximum d'itérations (pour éviter des temps de calcul trop longs) */
    private static final int ITERATIONS_MAX = 15000;
    
    // === Constructeurs ===
    
    /**
     * Constructeur avec nombre d'itérations explicite.
     * Utilisé quand l'utilisateur veut contrôler précisément le nombre d'itérations.
     * 
     * @param reseau le réseau électrique à optimiser
     * @param k le nombre d'itérations souhaité
     */
    public Automatisation(Reseaux reseau, int k) {
        this.reseau = reseau;
        this.k = k;
        this.random = new Random();
    }
    
    /**
     * Constructeur avec calcul automatique du nombre d'itérations.
     * Le nombre d'itérations est adapté à la taille du réseau pour un bon compromis
     * entre qualité de la solution et temps de calcul.
     * 
     * @param reseau le réseau électrique à optimiser
     */
    public Automatisation(Reseaux reseau) {
        this.reseau = reseau;
        this.k = calculerNombreIterations(reseau);
        this.random = new Random();
    }
    
    /**
     * Calcule automatiquement le nombre d'itérations optimal en fonction de la taille du réseau.
     * 
     * Formule : k = nbMaisons × 150 × log(nbGenerateurs + 1)
     * - Plus il y a de maisons, plus on a besoin d'itérations
     * - Plus il y a de générateurs, plus l'espace de solutions est grand
     * - Le logarithme évite une croissance trop rapide
     * 
     * Bornes : entre 1000 et 15000 itérations
     * 
     * @param reseau le réseau électrique
     * @return le nombre d'itérations recommandé
     */
    private int calculerNombreIterations(Reseaux reseau) {
        int nbMaisons = reseau.getM().size();
        int nbGenerateurs = reseau.getG().size();
        
        // Formule : nb_maisons × 150 × log(nb_generateurs + 1)
        int iterations = nbMaisons * ITERATIONS_PAR_MAISON * nbGenerateurs;
        
        // Application des bornes min et max
        iterations = Math.max(ITERATIONS_MIN, iterations);
        iterations = Math.min(ITERATIONS_MAX, iterations);
        
        return iterations;
    }
    
    // === Méthode principale de résolution ===
    
    /**
     * Méthode principale de résolution du problème d'optimisation.
     * 
     * Algorithme multi-phases :
     * 1. Optimisation gloutonne (amélioration rapide initiale)
     * 2. Recherche locale intensive (exploration systématique)
     * 3. Échanges de paires - 2-opt (sortir des minima locaux)
     * 4. Échanges de triplets - 3-opt (configurations complexes)
     * 5. Passes exhaustives multi-niveaux (garantir l'optimum)
     * 
     * Chaque phase améliore progressivement la solution jusqu'à ce qu'aucun
     * mouvement local ne puisse plus réduire le coût.
     */
    public void resoudre() {
        // Récupération des éléments du réseau
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();
        
        // Vérification que le réseau n'est pas vide
        if (maisons.isEmpty() || generateurs.isEmpty()) {
            System.out.println("Erreur : le reseau est vide.");
            return;
        }
        
        // Affichage de l'en-tête
        System.out.println("       OPTIMISATION AUTOMATIQUE DU RESEAU");
        System.out.println("Reseau : " + maisons.size() + " maisons, " + generateurs.size() + " generateurs");
        System.out.println("Nombre d'iterations : " + k);
        
        // Calcul et affichage du coût initial
        double coutInitial = reseau.calculercout();
        System.out.println("Cout initial : " + String.format("%.3f", coutInitial));
        
        // Compteur d'améliorations total
        int ameliorationsTotal = 0;
        
        // === PHASE 1 : Optimisation gloutonne ===
        // Objectif : Obtenir rapidement une bonne solution de départ
        // Stratégie : Placer d'abord les maisons les plus consommatrices
        System.out.println("Phase 1 : Optimisation gloutonne");
        ameliorationsTotal += phaseGloutonne();
        
        // === PHASE 2 : Recherche locale intensive ===
        // Objectif : Améliorer la solution en testant de nombreuses affectations
        // Stratégie : Pour chaque maison choisie aléatoirement, tester tous les générateurs
        System.out.println("\nPhase 2 : Recherche locale intensive");
        ameliorationsTotal += rechercheLocaleIntensive(k / 4);
        
        // === PHASE 3 : Échanges de paires (2-opt) ===
        // Objectif : Sortir des minima locaux en échangeant 2 maisons
        // Stratégie : Échanger les générateurs de 2 maisons simultanément
        System.out.println("\nPhase 3 : Echanges de paires (2-opt)");
        ameliorationsTotal += echangesPaires(k / 3);
        
        // === PHASE 4 : Échanges de triplets (3-opt) ===
        // Objectif : Explorer des configurations plus complexes
        // Stratégie : Rotation des générateurs de 3 maisons
        System.out.println("\nPhase 4 : Echanges de triplets (3-opt)");
        ameliorationsTotal += echangesTriplets(k / 6);
        
        // === PHASE 5 : Passes exhaustives multi-niveaux ===
        // Objectif : Garantir qu'on a atteint l'optimum local
        // Stratégie : Passer exhaustivement sur toutes les maisons et paires jusqu'à stabilisation
        System.out.println("\nPhase 5 : Passes exhaustives multi-niveaux");
        ameliorationsTotal += passesExhaustivesMultiNiveaux(50);
        
        // Calcul du coût final
        double coutFinal = reseau.calculercout();
        
        // === Affichage des résultats ===
        System.out.println("              RESULTATS DE L'OPTIMISATION");
        System.out.printf("Cout initial    : %.15f\n", coutInitial);
        System.out.printf("Cout final      : %.15f\n", coutFinal);
        System.out.printf("Reduction       : %.15f\n", coutInitial - coutFinal);
        
        // Calcul du pourcentage d'amélioration
        if (coutInitial > 0) {
            double ameliorationPct = ((coutInitial - coutFinal) / coutInitial) * 100;
            System.out.printf("Amelioration    : %.2f%%\n", ameliorationPct);
        }
        
        System.out.printf("Modifications   : %d changements de connexion\n", ameliorationsTotal);
        
        // Affichage des statistiques détaillées du réseau final
        afficherStatistiquesReseau();
    }
    
    // === PHASE 1 : Optimisation gloutonne ===
    
    /**
     * Phase d'optimisation gloutonne initiale.
     * 
     * Stratégie :
     * 1. Trier les maisons par consommation décroissante
     * 2. Pour chaque maison (de la plus consommatrice à la moins consommatrice) :
     *    - Tester tous les générateurs
     *    - Affecter la maison au générateur qui minimise le coût
     * 
     * Cette approche permet d'obtenir rapidement une solution de bonne qualité
     * en plaçant intelligemment les maisons les plus "problématiques" en premier.
     * 
     * @return le nombre d'améliorations effectuées
     */
    private int phaseGloutonne() {
        // Copie de la liste pour ne pas modifier l'originale
        List<Maison> maisons = new ArrayList<>(reseau.getM());
        List<Generateur> generateurs = reseau.getG();
        
        // Tri des maisons par consommation décroissante
        // Les maisons les plus consommatrices sont traitées en premier
        maisons.sort((m1, m2) -> Double.compare(m2.getcons(), m1.getcons()));
        
        int ameliorations = 0;
        
        // Pour chaque maison (dans l'ordre décroissant de consommation)
        for (Maison m : maisons) {
            // Trouver le générateur actuel de cette maison
            Generateur ancienGen = trouverGenerateurDeMaison(m);
            double coutActuel = reseau.calculercout();
            
            // Initialisation : le meilleur générateur est l'actuel
            Generateur meilleurGen = ancienGen;
            double meilleurCout = coutActuel;
            
            // Tester tous les générateurs disponibles
            for (Generateur g : generateurs) {
                // Ignorer le générateur actuel (pas de changement inutile)
                if (g.equals(ancienGen)) continue;
                
                // Essayer ce générateur
                reseau.changeConnection(m, g);
                double nouveauCout = reseau.calculercout();
                
                // Si c'est mieux, on garde en mémoire
                if (nouveauCout < meilleurCout) {
                    meilleurCout = nouveauCout;
                    meilleurGen = g;
                }
                
                // Restaurer la connexion originale pour tester les autres
                reseau.changeConnection(m, ancienGen);
            }
            
            // Si on a trouvé un meilleur générateur, on applique le changement
            if (!meilleurGen.equals(ancienGen)) {
                reseau.changeConnection(m, meilleurGen);
                ameliorations++;
                System.out.printf("  %s : %s -> %s (cout: %.15f)\n", 
                    m.getnom(), ancienGen.getnom(), meilleurGen.getnom(), meilleurCout);
            }
        }
        
        return ameliorations;
    }
    
    // === PHASE 2 : Recherche locale intensive ===
    
    /**
     * Recherche locale intensive avec exploration aléatoire.
     * 
     * Stratégie :
     * - Choisir une maison aléatoire
     * - Tester tous les générateurs pour cette maison
     * - Appliquer le meilleur changement trouvé
     * - Répéter pendant un nombre d'itérations donné
     * 
     * L'aspect aléatoire permet d'explorer différentes parties du réseau
     * sans biais d'ordre, ce qui améliore la qualité de la solution.
     * 
     * @param iterations nombre d'itérations à effectuer
     * @return le nombre d'améliorations effectuées
     */
    private int rechercheLocaleIntensive(int iterations) {
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();
        int ameliorations = 0;
        
        // Boucle d'exploration
        for (int i = 0; i < iterations; i++) {
            // Choix aléatoire d'une maison
            Maison m = maisons.get(random.nextInt(maisons.size()));
            Generateur ancienGen = trouverGenerateurDeMaison(m);
            
            // Si la maison n'est pas connectée, passer à la suivante
            if (ancienGen == null) continue;
            
            double coutActuel = reseau.calculercout();
            Generateur meilleurGen = ancienGen;
            double meilleurCout = coutActuel;
            
            // Tester tous les générateurs
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
            
            // Appliquer le meilleur changement trouvé
            if (meilleurCout < coutActuel) {
                reseau.changeConnection(m, meilleurGen);
                ameliorations++;
                
                // Afficher seulement les premières améliorations ou tous les 5
                // pour ne pas surcharger la sortie
                if (ameliorations <= 10 || ameliorations % 5 == 0) {
                    System.out.printf("  [%4d] %s : %s -> %s (cout: %.15f)\n", 
                        i + 1, m.getnom(), ancienGen.getnom(), meilleurGen.getnom(), meilleurCout);
                }
            }
        }
        
        return ameliorations;
    }
    
    // === PHASE 3 : Échanges de paires (2-opt) ===
    
    /**
     * Échanges de paires (2-opt) pour sortir des minima locaux.
     * 
     * Principe du 2-opt :
     * Parfois, aucun mouvement simple (changer une seule maison) n'améliore le coût,
     * MAIS échanger simultanément 2 maisons peut débloquer la situation.
     * 
     * Exemple :
     *   Avant : maison1 → gen1, maison2 → gen2
     *   Après : maison1 → gen2, maison2 → gen1
     * 
     * Cet échange peut améliorer l'équilibre global même si changer
     * une seule maison dégraderait le coût.
     * 
     * @param iterations nombre d'échanges à tenter
     * @return le nombre d'améliorations effectuées
     */
    private int echangesPaires(int iterations) {
        List<Maison> maisons = reseau.getM();
        int ameliorations = 0;
        
        // Effectuer plusieurs tentatives d'échanges
        for (int i = 0; i < iterations && maisons.size() >= 2; i++) {
            // Choisir deux maisons différentes aléatoirement
            Maison m1 = maisons.get(random.nextInt(maisons.size()));
            Maison m2;
            do {
                m2 = maisons.get(random.nextInt(maisons.size()));
            } while (m1.equals(m2)); // S'assurer que les deux maisons sont différentes
            
            // Récupérer leurs générateurs actuels
            Generateur gen1 = trouverGenerateurDeMaison(m1);
            Generateur gen2 = trouverGenerateurDeMaison(m2);
            
            // Vérifications : les deux maisons doivent être connectées
            // et à des générateurs différents (sinon l'échange est inutile)
            if (gen1 == null || gen2 == null || gen1.equals(gen2)) continue;
            
            double coutActuel = reseau.calculercout();
            
            // Effectuer l'échange : m1 ↔ m2
            reseau.changeConnection(m1, gen2);
            reseau.changeConnection(m2, gen1);
            
            double nouveauCout = reseau.calculercout();
            
            // Si l'échange améliore le coût, on le garde
            if (nouveauCout < coutActuel) {
                ameliorations++;
                if (ameliorations <= 10 || ameliorations % 5 == 0) {
                    System.out.printf("  [%4d] Echange : (%s,%s) <-> (%s,%s) (cout: %.15f)\n", 
                        i + 1, m1.getnom(), gen2.getnom(), m2.getnom(), gen1.getnom(), nouveauCout);
                }
            } else {
                // Sinon, annuler l'échange
                reseau.changeConnection(m1, gen1);
                reseau.changeConnection(m2, gen2);
            }
        }
        
        return ameliorations;
    }
    
    // === PHASE 4 : Échanges de triplets (3-opt) ===
    
    /**
     * Échanges de triplets (3-opt) pour explorer des configurations complexes.
     * 
     * Principe du 3-opt :
     * Extension du 2-opt à 3 maisons. On effectue une rotation circulaire :
     *   maison1 → gen2
     *   maison2 → gen3
     *   maison3 → gen1
     * 
     * Ce type de mouvement peut débloquer des situations où ni les mouvements
     * simples ni les échanges de paires n'améliorent le coût.
     * 
     * C'est particulièrement utile pour des réseaux complexes où l'optimum
     * nécessite de réorganiser plusieurs connexions simultanément.
     * 
     * @param iterations nombre d'échanges à tenter
     * @return le nombre d'améliorations effectuées
     */
    private int echangesTriplets(int iterations) {
        List<Maison> maisons = reseau.getM();
        int ameliorations = 0;
        
        // Effectuer plusieurs tentatives d'échanges de triplets
        for (int i = 0; i < iterations && maisons.size() >= 3; i++) {
            // Choisir trois maisons différentes aléatoirement
            Maison m1 = maisons.get(random.nextInt(maisons.size()));
            Maison m2, m3;
            
            // S'assurer que m2 est différente de m1
            do {
                m2 = maisons.get(random.nextInt(maisons.size()));
            } while (m1.equals(m2));
            
            // S'assurer que m3 est différente de m1 et m2
            do {
                m3 = maisons.get(random.nextInt(maisons.size()));
            } while (m3.equals(m1) || m3.equals(m2));
            
            // Récupérer les générateurs actuels
            Generateur gen1 = trouverGenerateurDeMaison(m1);
            Generateur gen2 = trouverGenerateurDeMaison(m2);
            Generateur gen3 = trouverGenerateurDeMaison(m3);
            
            // Vérifications : toutes connectées et à des générateurs différents
            if (gen1 == null || gen2 == null || gen3 == null) continue;
            if (gen1.equals(gen2) || gen1.equals(gen3) || gen2.equals(gen3)) continue;
            
            double coutActuel = reseau.calculercout();
            
            // Effectuer la rotation : m1→gen2, m2→gen3, m3→gen1
            reseau.changeConnection(m1, gen2);
            reseau.changeConnection(m2, gen3);
            reseau.changeConnection(m3, gen1);
            
            double nouveauCout = reseau.calculercout();
            
            // Si la rotation améliore le coût, on la garde
            if (nouveauCout < coutActuel) {
                ameliorations++;
                if (ameliorations <= 5) {
                    System.out.printf("  [%4d] Triplet : %s->%s, %s->%s, %s->%s (cout: %.15f)\n", 
                        i + 1, m1.getnom(), gen2.getnom(), m2.getnom(), gen3.getnom(), 
                        m3.getnom(), gen1.getnom(), nouveauCout);
                }
            } else {
                // Sinon, annuler la rotation
                reseau.changeConnection(m1, gen1);
                reseau.changeConnection(m2, gen2);
                reseau.changeConnection(m3, gen3);
            }
        }
        
        return ameliorations;
    }
    
    // === PHASE 5 : Passes exhaustives multi-niveaux ===
    
    /**
     * Passes exhaustives multi-niveaux pour garantir l'optimum local.
     * 
     * Cette phase finale effectue des passes complètes sur le réseau
     * avec deux niveaux d'optimisation :
     * 
     * Niveau 1 : Mouvements simples exhaustifs
     *   - Tester chaque maison avec chaque générateur
     *   - Appliquer immédiatement toute amélioration trouvée
     * 
     * Niveau 2 : Échanges de paires exhaustifs (si niveau 1 bloqué)
     *   - Tester systématiquement les échanges de paires
     *   - Utilisé uniquement si aucun mouvement simple n'améliore
     * 
     * La fonction continue jusqu'à ce qu'une passe complète ne trouve
     * aucune amélioration, garantissant ainsi qu'on a atteint un optimum local.
     * 
     * @param maxPasses nombre maximum de passes à effectuer
     * @return le nombre total d'améliorations effectuées
     */
    private int passesExhaustivesMultiNiveaux(int maxPasses) {
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();
        int ameliorationsTotal = 0;
        
        // Effectuer jusqu'à maxPasses passes complètes
        for (int passe = 1; passe <= maxPasses; passe++) {
            int ameliorationsCePasse = 0;
            
            System.out.printf("  Passe %d...\n", passe);
            
            // === NIVEAU 1 : Mouvements simples exhaustifs ===
            // Tester chaque maison avec chaque générateur
            for (Maison m : maisons) {
                Generateur ancienGen = trouverGenerateurDeMaison(m);
                if (ancienGen == null) continue;
                
                double coutActuel = reseau.calculercout();
                Generateur meilleurGen = ancienGen;
                double meilleurCout = coutActuel;
                
                // Tester tous les générateurs pour cette maison
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
                
                // Si amélioration trouvée, l'appliquer
                if (meilleurCout < coutActuel) {
                    reseau.changeConnection(m, meilleurGen);
                    ameliorationsCePasse++;
                    ameliorationsTotal++;
                    System.out.printf("    Simple : %s : %s -> %s (cout: %.15f)\n", 
                        m.getnom(), ancienGen.getnom(), meilleurGen.getnom(), meilleurCout);
                }
            }
            
            // === NIVEAU 2 : Échanges de paires exhaustifs ===
            // Utilisé seulement si niveau 1 n'a rien trouvé
            if (ameliorationsCePasse == 0 && maisons.size() >= 2) {
                // Tester un échantillon d'échanges de paires
                // (limité pour éviter une explosion combinatoire)
                for (int i = 0; i < Math.min(maisons.size() - 1, 10); i++) {
                    for (int j = i + 1; j < Math.min(maisons.size(), i + 11); j++) {
                        Maison m1 = maisons.get(i);
                        Maison m2 = maisons.get(j);
                        
                        Generateur gen1 = trouverGenerateurDeMaison(m1);
                        Generateur gen2 = trouverGenerateurDeMaison(m2);
                        
                        if (gen1 == null || gen2 == null || gen1.equals(gen2)) continue;
                        
                        double coutActuel = reseau.calculercout();
                        
                        // Tenter l'échange
                        reseau.changeConnection(m1, gen2);
                        reseau.changeConnection(m2, gen1);
                        
                        double nouveauCout = reseau.calculercout();
                        
                        // Si amélioration, la garder et passer à la passe suivante
                        if (nouveauCout < coutActuel) {
                            ameliorationsCePasse++;
                            ameliorationsTotal++;
                            System.out.printf("    Paire : (%s,%s) <-> (%s,%s) (cout: %.15f)\n", 
                                m1.getnom(), gen2.getnom(), m2.getnom(), gen1.getnom(), nouveauCout);
                            break; // Sortir de la boucle j
                        } else {
                            // Annuler l'échange
                            reseau.changeConnection(m1, gen1);
                            reseau.changeConnection(m2, gen2);
                        }
                    }
                    if (ameliorationsCePasse > 0) break; // Sortir de la boucle i
                }
            }
            
            // Si aucune amélioration trouvée dans cette passe, c'est terminé
            if (ameliorationsCePasse == 0) {
                System.out.println("  Aucune amelioration - optimum atteint !");
                break;
            } else {
                System.out.printf("  %d amelioration(s) lors de cette passe\n", ameliorationsCePasse);
            }
        }
        
        return ameliorationsTotal;
    }
    
    // === Méthodes utilitaires ===
    
    /**
     * Trouve le générateur auquel une maison est actuellement connectée.
     * 
     * Parcourt la map des connexions pour trouver quelle entrée contient
     * la maison recherchée dans sa liste de valeurs.
     * 
     * @param m la maison dont on cherche le générateur
     * @return le générateur connecté, ou null si la maison n'est pas connectée
     */
    private Generateur trouverGenerateurDeMaison(Maison m) {
        Map<Generateur, List<Maison>> connexions = reseau.getConnexions();
        
        // Parcourir toutes les entrées de la map
        for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
            // Si la liste de maisons de ce générateur contient m
            if (entry.getValue().contains(m)) {
                return entry.getKey(); // Retourner le générateur
            }
        }
        
        return null; // Maison non trouvée
    }
    
    /**
     * Affiche des statistiques détaillées sur l'état du réseau après optimisation.
     * 
     * Pour chaque générateur, affiche :
     * - La charge actuelle / capacité maximale
     * - Le taux d'utilisation en pourcentage
     * - Le nombre de maisons connectées
     * - L'état (SURCHARGE, Optimal, Sous-utilisé, etc.)
     * 
     * Affiche également :
     * - Le taux d'utilisation moyen du réseau
     * - Le nombre de générateurs en surcharge
     * - Les valeurs de dispersion et surcharge (composantes du coût)
     */
    private void afficherStatistiquesReseau() {
        List<Generateur> generateurs = reseau.getG();

        Map<Generateur, List<Maison>> connexions = reseau.getConnexions();
        
        System.out.println("Statistiques detaillees du reseau optimise :");
        
        double tauxMoyen = 0;
        int nbSurcharges = 0;
        
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
            tauxMoyen += taux / 100.0;
            
            String etat;
            if (taux > 100) {
                etat = "SURCHARGE";
                nbSurcharges++;
            } else if (taux > 90) {
                etat = "Tres charge";
            } else if (taux > 70) {
                etat = "Optimal";
            } else if (taux > 40) {
                etat = "Sous-utilise";
            } else {
                etat = "Faible charge";
            }
            
            System.out.printf("  %s : %.0f/%.0f kW (%.1f%%) - %d maisons - %s\n",
                g.getnom(), charge, capacite, taux, nbMaisons, etat);
        }
        
        tauxMoyen /= generateurs.size();
        
        System.out.printf("Taux d'utilisation moyen : %.1f%%\n", tauxMoyen * 100);
        System.out.printf("Generateurs surcharges   : %d / %d\n", nbSurcharges, generateurs.size());
        
        double dispersion = reseau.Disp();
        double surcharge = reseau.surcharge();
        System.out.printf("Dispersion               : %.15f\n", dispersion);
        System.out.printf("Surcharge                : %.15f\n", surcharge);
    }
    
    public Reseaux getReseau() {
        return reseau;
    }
    
    public void setK(int k) {
        if (k > 0) {
            this.k = k;
        }
    }
    
    public int getK() {
        return k;
    }
}
