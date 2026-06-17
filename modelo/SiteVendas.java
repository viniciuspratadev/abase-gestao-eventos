package modelo;

public class SiteVendas {
    private int idSite;
    private String nome;
    private String urlBase;

    public SiteVendas() {}

    public int getIdSite() { return idSite; }
    public void setIdSite(int idSite) { this.idSite = idSite; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUrlBase() { return urlBase; }
    public void setUrlBase(String urlBase) { this.urlBase = urlBase; }

    @Override
    public String toString() {
        return this.nome;
    }
}