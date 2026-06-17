package modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Checkin {
    private int idCheckin;
    private Timestamp dataCheckin;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private int idUsuario;
    private int idEvento;

    public Checkin() {}

    public int getIdCheckin() { return idCheckin; }
    public void setIdCheckin(int idCheckin) { this.idCheckin = idCheckin; }
    public Timestamp getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(Timestamp dataCheckin) { this.dataCheckin = dataCheckin; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }
}