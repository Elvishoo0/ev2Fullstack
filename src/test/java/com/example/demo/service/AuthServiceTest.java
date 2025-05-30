package com.example.demo.service;

import com.example.demo.model.Login;
import com.example.demo.model.dto.LoginResponse;
import com.example.demo.repository.LoginRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceTest {

  private final LoginRepository repo;

  public AuthServiceTest(LoginRepository repo) {
    this.repo = repo;
  }

  /** Registro */
  public Login register(String correo, String contrasena, String idCarrera) {
    repo.findByCorreo(correo).ifPresent(u -> {
      throw new IllegalStateException("El correo ya existe: " + correo);
    });
    Login u = new Login();
    u.setCorreo(correo);
    u.setContrasena(contrasena);
    u.setIdCarrera(idCarrera);
    return repo.save(u);
  }

  /** Login */
  public LoginResponse login(String correo, String contrasena) {
    Login u = repo.findByCorreo(correo)
                  .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    if (!contrasena.equals(u.getContrasena())) {
      throw new IllegalArgumentException("Credenciales inválidas");
    }
    return new LoginResponse(u.getId(), "login ok");
  }

  /** Listar todos */
  public List<Login> listAll() {
    return repo.findAll();
  }
}
