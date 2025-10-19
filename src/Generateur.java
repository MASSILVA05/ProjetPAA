
public class Generateur {
	private int cap;
	private String nom;
	public Generateur(int cap,String nom) {
		this.cap=cap;
		this.nom=nom;
	}
	public int getcap() {
		return this.cap;
	}
	public String getnom() {
		return this.nom;
	}
	

    public void setCap(int cap) {
        this.cap = cap;
    }
    @Override
    public String toString() {
        return nom + " (" + cap + " kW)";
    }

}
