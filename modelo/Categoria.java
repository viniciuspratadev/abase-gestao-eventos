package modelo;

public class Categoria {
    private int idCateg;
    private String nomeCategoria;

    public Categoria() {}

    public int getIdCateg() { return idCateg; }
    public void setIdCateg(int idCateg) { this.idCateg = idCateg; }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }

    @Override
    public String toString() {
        return this.nomeCategoria;
    }
}