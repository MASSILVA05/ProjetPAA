package partie2;

import partie1.Maison;
import partie1.Generateur;
import partie1.Reseaux;
import java.util.List;
import java.util.Random;

public class Automatisation {
	private Reseaux reseau;
    private int k; // nombre maximum d'itérations
    private Random random;
    
    public Automatisation(Reseaux reseau, int k) {
    	this.reseau = reseau;
        this.k = k;
        this.random = new Random();
    }

    public void resoudre() {
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();
        
        double coutInitial = reseau.calculercout();
        System.out.println("Coût initial : " + coutInitial + "\n");
        
        int ameliorations = 0;
        
        for (int i = 0; i < k; i++) {
            // Choisir une maison aléatoire
            Maison m = maisons.get(random.nextInt(maisons.size()));
            Generateur ancienGen = trouverGenerateurDeMaison(m);
            
            // Tester tous les générateurs pour cette maison
            Generateur meilleurGen = ancienGen;
            double meilleurCout = reseau.calculercout();
            
            for (Generateur g : generateurs) {
                if (g.equals(ancienGen)) continue;
                
                reseau.changeConnection(m, g);
                double coutTest = reseau.calculercout();
                
                if (coutTest < meilleurCout) {
                    meilleurCout = coutTest;
                    meilleurGen = g;
                }
                
                // Restaurer
                if (ancienGen != null) {
                    reseau.changeConnection(m, ancienGen);
                }
            }
            
            // Appliquer si meilleur
            if (!meilleurGen.equals(ancienGen)) {
                reseau.changeConnection(m, meilleurGen);
                ameliorations++;
                System.out.printf("✓ Itération %d : %s → %s (coût %.3f)\n", 
                    i + 1, m.getnom(), meilleurGen.getnom(), meilleurCout);
            }
        }
        
        double coutFinal = reseau.calculercout();
        System.out.println("\n=== Résultat final (Hybride) ===");
        System.out.printf("Coût initial : %.3f\n", coutInitial);
        System.out.printf("Coût final : %.3f\n", coutFinal);
        System.out.printf("Amélioration : %.2f%%\n", 
            100 * (coutInitial - coutFinal) / coutInitial);
        System.out.println("Améliorations : " + ameliorations + "/" + k);
    }
    
    private Generateur trouverGenerateurDeMaison(Maison m) {
        for (Generateur g : reseau.getG()) {
            List<Maison> maisons = reseau.getConnexions().get(g);
            if (maisons != null && maisons.contains(m)) {
                return g;
            }
        }
        return null;
    }

    public Reseaux getReseau() {
        return reseau;
    }
}

