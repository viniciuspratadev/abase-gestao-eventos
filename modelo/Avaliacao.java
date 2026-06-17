package modelo;

import java.sql.Timestamp;

public class Avaliacao {
    private int idAvaliacao;
    private int nota;
    private String comentario;
    private Timestamp dataAvaliacao;
    private int idCheckin;

    public Avaliacao() {}

    public int getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(int idAvaliacao) { this.idAvaliacao = idAvaliacao; }
    public int getNota() { return nota; }
    public void setNota(int nota) { this.nota = nota; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Timestamp getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(Timestamp dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
    public int getIdCheckin() { return idCheckin; }
    public void setIdCheckin(int idCheckin) { this.idCheckin = idCheckin; }
}