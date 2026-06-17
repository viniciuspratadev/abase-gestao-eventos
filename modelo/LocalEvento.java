package modelo;

import java.math.BigDecimal;

public class LocalEvento {
    private int idLocal;
    private String nome;
    private String endereco;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public LocalEvento() {}

    public int getIdLocal() { return idLocal; }
    public void setIdLocal(int idLocal) { this.idLocal = idLocal; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    @Override
    public String toString() {
        return this.nome; // É isso que aparecerá escrito no menu suspenso
    }
}