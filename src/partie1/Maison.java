package partie1;
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
     * Constructeur d'une maison avec un nom et un type de consommation.
     *
     * @param nom  le nom de la maison
     * @param type le type de consommation {@link ConsommationType}
     */
	public Maison(String nom, ConsommationType type) {
	    this.nom = nom;
	    this.cons = type.getValeur();
	}
	/**
     * Constructeur d'une maison avec un nom et une consommation en kW.
     *
     * @param cons consommation de la maison en kW
     * @param nom  le nom de la maison
     */
	public Maison(int cons, String nom) {
	    this.cons = cons;
	    this.nom = nom;
	}
	/**
     * Enumération représentant les types de consommation électrique.
     * Chaque type est associé à une valeur en kW.
     */

	public enum ConsommationType {
	    BASSE(10),
	    NORMAL(20),
	    FORTE(40);

	    private final int valeur;

	    ConsommationType(int valeur) {
	        this.valeur = valeur;
	    }
	    /**
         * Retourne la valeur en kW correspondant au type de consommation.
         *
         * @return consommation en kW
         */
	    public int getValeur() {
	        return valeur;
	    }
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
     * Retourne la consommation électrique de la maison.
     *
     * @return consommation en kW
     */

	public int getcons() {
		return cons;
	}
	/**
     * Modifie la consommation de la maison en utilisant un type de consommation.
     *
     * @param type nouveau type de consommation {@link ConsommationType}
     */
	public void setCons(ConsommationType type) {
	    this.cons = type.getValeur();
	}
	/**
     * Retourne une représentation textuelle de la maison.
     *
     * @return chaîne de la forme "Nom (consommation kW)"
     */
    @Override
    public String toString() {
        return nom + " (" + cons + " kW)";
    }
	

}
