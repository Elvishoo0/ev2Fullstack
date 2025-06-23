package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logins")
public class Login {

  @Id
  private String id;

  private String correo;

  @JsonIgnore
  private String contrasena;

  private String idCarrera;

  // ✅ Constructor vacío requerido para usar new Login()
  public Login() {
  }

  // Constructor con argumentos
  public Login(String id, String correo, String contrasena, String idCarrera) {
    this.id = id;
    this.correo = correo;
    this.contrasena = contrasena;
    this.idCarrera = idCarrera;
  }

  // Getters & Setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getCorreo() { return correo; }
  public void setCorreo(String correo) { this.correo = correo; }

  public String getContrasena() { return contrasena; }
  public void setContrasena(String contrasena) { this.contrasena = contrasena; }

  public String getIdCarrera() { return idCarrera; }
  public void setIdCarrera(String idCarrera) { this.idCarrera = idCarrera; }
}
