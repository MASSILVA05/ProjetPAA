
public class Maison {
	private int cons;
	private String nom;
	public Maison(int cons,String nom) {
		this.cons=cons;
		this.nom=nom;
	}
	public String getnom() {
		return this.nom;
	}
	public int getcons() {
		return cons;
	}
	public void setCons(int cons) {
        this.cons = cons;
    }

    @Override
    public String toString() {
        return nom + " (" + cons + " kW)";
    }
	

}
