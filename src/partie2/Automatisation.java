package partie2;
import partie1.Maison;
import partie1.Generateur;
import partie1.Reseaux;
import java.util.List;
import java.util.Random;
public class Automatisation {
	private Reseaux reseau;
    private int k; // nombre de tentatives
    private Random rand;

    public Automatisation(Reseaux reseau, int k) {
        this.reseau = reseau;
        this.k = k;
        this.rand = new Random();
    }
    public void resoudre() {
        List<Maison> maisons = reseau.getM();
        List<Generateur> generateurs = reseau.getG();

        for (int i = 0; i < k; i++) {
            Maison m = maisons.get(rand.nextInt(maisons.size()));
            Generateur g = generateurs.get(rand.nextInt(generateurs.size()));

            // Copier le réseau
            Reseaux copie = reseau.copier();
            copie.changeConnection(m, g);

            double coutOriginal = reseau.calculercout();
            double coutNouveau = copie.calculercout();
            System.out.println("\n--- Tentative " + (i+1) + " ---");
            System.out.println("Maison choisie : " + m.getnom());
            System.out.println("Nouveau générateur proposé : " + g.getnom());
            System.out.println("Coût avant : " + coutOriginal);
            System.out.println("Coût après : " + coutNouveau);
            if (coutNouveau < coutOriginal) {
                reseau = copie;
                System.out.println("=> Nouvelle configuration acceptée !");
            } else {
                System.out.println("=> Changement rejeté.");
            }
        }
    }


    public Reseaux getReseau() {
        return reseau;
    }
	

}
