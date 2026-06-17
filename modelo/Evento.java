package modelo;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.ArrayList;

public class Evento {
    private int idEvento;
    private String urlEvento;
    private String nome;
    private Date dataEvento;
    private Time horaEvento;
    private int idLocal;
    private int idSite;
    private int idProdutor;
    private String status;
    private int visualizacoes;
    private int cliquesUrl;
    private List<Integer> idsCategorias = new ArrayList<>();

    public Evento() {}

    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }

    public String getUrlEvento() { return urlEvento; }
    public void setUrlEvento(String urlEvento) { this.urlEvento = urlEvento; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Date getDataEvento() { return dataEvento; }
    public void setDataEvento(Date dataEvento) { this.dataEvento = dataEvento; }

    public Time getHoraEvento() { return horaEvento; }
    public void setHoraEvento(Time horaEvento) { this.horaEvento = horaEvento; }

    public int getIdLocal() { return idLocal; }
    public void setIdLocal(int idLocal) { this.idLocal = idLocal; }

    public int getIdSite() { return idSite; }
    public void setIdSite(int idSite) { this.idSite = idSite; }

    public int getIdProdutor() { return idProdutor; }
    public void setIdProdutor(int idProdutor) { this.idProdutor = idProdutor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getVisualizacoes() { return visualizacoes; }
    public void setVisualizacoes(int visualizacoes) { this.visualizacoes = visualizacoes; }

    public int getCliquesUrl() { return cliquesUrl; }
    public void setCliquesUrl(int cliquesUrl) { this.cliquesUrl = cliquesUrl; }

    public List<Integer> getIdsCategorias() { return idsCategorias; }
    public void setIdsCategorias(List<Integer> idsCategorias) { this.idsCategorias = idsCategorias; }
}