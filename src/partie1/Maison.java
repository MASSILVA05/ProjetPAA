package partie1;

/**
 * Classe représentant une maison dans le réseau électrique.
 * 
 * Une maison possède un nom et une consommation électrique (en kW).
 * Cette classe permet de créer des maisons, de récupérer et modifier leurs attributs,
 * ainsi que d'obtenir une représentation textuelle.
 * 
 * @author Massilva Djennadi
 * @author Ines Meslem
 * @author Lizaveta Dzemchankova
 */
public class Maison {
    
    /**
     * Énumération représentant les types de consommation électrique.
     * Chaque type est associé à une valeur en kW.
     */
    public enum ConsommationType {
        BASSE(10, "BASSE"),
        NORMAL(20, "NORMAL"),
        FORTE(40, "FORTE");

        private final int valeur;
        private final String nom;

        /**
         * Constructeur de l'énumération.
         * 
         * @param valeur la consommation en kW pour ce type
         * @param nom le nom du type
         */
        ConsommationType(int valeur, String nom) {
            this.valeur = valeur;
            this.nom = nom;
        }

        /**
         * Retourne la valeur en kW correspondant au type de consommation.
         * 
         * @return consommation en kW
         */
        public int getValeur() {
            return valeur;
        }

        /**
         * Retourne le nom du type de consommation.
         * 
         * @return nom du type (BASSE, NORMAL, FORTE)
         */
        public String getNom() {
            return nom;
        }
    }

    /**
     * Consommation électrique de la maison en kW.
     */
    private int cons;

    /**
     * Nom de la maison.
     */
    private String nom;

    /**
     * Type de consommation de la maison (pour sauvegarde et affichage).
     */
    private ConsommationType consommationType;

    /**
     * Constructeur d'une maison avec un nom et un type de consommation.
     * 
     * @param nom le nom de la maison
     * @param type le type de consommation {@link ConsommationType}
     */
    public Maison(String nom, ConsommationType type) {
        this.nom = nom;
        this.consommationType = type;
        this.cons = type.getValeur();
    }

    /**
     * Constructeur d'une maison avec un nom et une consommation en kW.
     * 
     * Utilisé principalement lors de la lecture depuis fichier.
     * Détermine automatiquement le type de consommation.
     * 
     * @param cons consommation de la maison en kW
     * @param nom le nom de la maison
     */
    public Maison(int cons, String nom) {
        this.cons = cons;
        this.nom = nom;
        this.consommationType = determinerType(cons);
    }

    /**
     * Détermine le type de consommation à partir d'une valeur en kW.
     * 
     * @param valeur la consommation en kW
     * @return le type de consommation correspondant
     */
    private static ConsommationType determinerType(int valeur) {
        switch (valeur) {
            case 10:
                return ConsommationType.BASSE;
            case 20:
                return ConsommationType.NORMAL;
            case 40:
                return ConsommationType.FORTE;
            default:
                return ConsommationType.NORMAL;
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
     * Retourne la consommation électrique de la maison en kW.
     * 
     * @return consommation en kW
     */
    public int getcons() {
        return cons;
    }

    /**
     * Retourne le type de consommation de la maison sous forme de String.
     * Utilisé pour l'affichage et la sauvegarde du réseau.
     * 
     * @return le nom du type de consommation (BASSE, NORMAL, FORTE)
     */
    public String getcons_type() {
        return consommationType.getNom();
    }

    /**
     * Modifie la consommation de la maison en utilisant un type de consommation.
     * Met à jour à la fois le type et la valeur de consommation.
     * 
     * @param type nouveau type de consommation {@link ConsommationType}
     */
    public void setCons(ConsommationType type) {
        this.consommationType = type;
        this.cons = type.getValeur();
    }

    /**
     * Retourne une représentation textuelle de la maison.
     * 
     * Format : "Nom (type - consommation kW)"
     * Exemple : "M1 (NORMAL - 20 kW)"
     * 
     * @return chaîne formatée de la maison
     */
    @Override
    public String toString() {
        return nom + " (" + consommationType.getNom() + " - " + cons + " kW)";
    }
}
