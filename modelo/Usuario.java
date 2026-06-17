package modelo;

import java.sql.Timestamp;

public class Usuario {
    private int idUsuario;
    private String email;
    private String senhaHash;
    private Timestamp dataCadastro;
    private boolean statusAprov;

    // Construtor
    public Usuario() {}

    // Getters e Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public Timestamp getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(Timestamp dataCadastro) { this.dataCadastro = dataCadastro; }
    public boolean isStatusAprov() { return statusAprov; }
    public void setStatusAprov(boolean statusAprov) { this.statusAprov = statusAprov; }
}