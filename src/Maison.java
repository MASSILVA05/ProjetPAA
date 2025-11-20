/**
 * Classe représentant une maison dans le réseau électrique.
 * <p>
 * Une maison possède un nom et une consommation électrique (en kW).
 * Cette classe permet de créer des maisons, de récupérer et modifier leurs attributs,
 * ainsi que d’obtenir une représentation textuelle.
 * </p>
 * 
 * Membre du groupe :
 * <ul>
 *     <li>Massilva Djennadi</li>
 *     <li>Ines Meslem</li>
 *     <li>Lizaveta Dzemchankova</li>
 * </ul>
 */
public class Maison {
	/**
     * Consommation électrique de la maison en kW.
     */

	private int cons;
	/**
     * Nom de la maison.
     */

	private String nom;
	/**
     * Constructeur pour créer une maison avec un nom et une consommation.
     * 
     * @param cons consommation de la maison (en kW)
     * @param nom nom de la maison
     */
	public Maison(int cons,String nom) {
		this.cons=cons;
		this.nom=nom;
	}
	/**
     * Retourne le nom de la maison.
     * 
     * @return nom de la maison
     */

	public String getnom() {
		return this.nom;
	}
	/**
     * Retourne la consommation de la maison.
     * 
     * @return consommation en kW
     */
	public int getcons() {
		return cons;
	}
	/**
     * Modifie la consommation de la maison.
     * 
     * @param cons nouvelle consommation en kW
     */

	public void setCons(int cons) {
        this.cons = cons;
    }
	/**
     * Retourne une représentation textuelle de la maison.
     * <p>
     * Format : "Nom (consommation kW)"
     * </p>
     * 
     * @return chaîne représentant la maison
     */
    @Override
    public String toString() {
        return nom + " (" + cons + " kW)";
    }
	

}
