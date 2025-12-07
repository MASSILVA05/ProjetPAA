package partie1;

/**
 * Classe représentant un générateur électrique.
 * <p>
 * Un générateur possède un nom et une capacité (en kW).
 * Cette classe permet de créer des générateurs, de récupérer et modifier leurs attributs,
 * ainsi que d’obtenir une représentation textuelle.
 * </p>
 * 
 * Membre du groupe :
 * <ul>
 *     <li>Massilva Djennadi</li>
 *     <li>Ines Meslem</li>
 *     <li>Lizaveta Dzemchankova</li>
 * </ul>
 * 
 */

public class Generateur {
	/**
     * Capacité du générateur en kW.
     */

	private int cap;
	/**
     * Nom du générateur.
     */

	private String nom;
	/**
     * Constructeur pour créer un générateur avec un nom et une capacité.
     * 
     * @param cap capacité du générateur (en kW)
     * @param nom nom du générateur
     */
	public Generateur(int cap,String nom) {
		this.cap=cap;
		this.nom=nom;
	}

    /**
     * Retourne la capacité du générateur.
     * 
     * @return capacité en kW
     */
	public int getcap() {
		return this.cap;
	}
	/**
     * Retourne le nom du générateur.
     * 
     * @return nom du générateur
     */

	public String getnom() {
		return this.nom;
	}
	/**
     * Modifie la capacité du générateur.
     * 
     * @param cap nouvelle capacité en kW
     */

    public void setCap(int cap) {
        this.cap = cap;
    }
    /**
     * Retourne une représentation textuelle du générateur.
     * <p>
     * Format : "Nom (capacité kW)"
     * </p>
     * 
     * @return chaîne représentant le générateur
     */
    @Override
    public String toString() {
        return nom + " (" + cap + " kW)";
    }
    
}
