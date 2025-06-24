package com.example.demo.service;

import com.example.demo.model.Login;
import com.example.demo.model.dto.LoginResponse;
import com.example.demo.repository.LoginRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final LoginRepository loginRepository;

    public AuthService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    /** Registra nuevo usuario */
    public Login register(String correo, String contrasena, String idCarrera) {
        Login nuevo = new Login();
        nuevo.setCorreo(correo);
        nuevo.setContrasena(contrasena);
        nuevo.setIdCarrera(idCarrera);
        return loginRepository.save(nuevo);
    }

    /** Realiza login */
    public LoginResponse login(String correo, String contrasena) {
        Login user = loginRepository.findByCorreoAndContrasena(correo, contrasena).orElse(null);
        if (user == null) {
            return null;
        }

        String token = "token_" + UUID.randomUUID();
        return new LoginResponse(user.getCorreo(), token);
    }

    /** Devuelve todos los usuarios */
    public List<Login> listAll() {
        return loginRepository.findAll();
    }

    /** Devuelve usuario por ID */
    public Login findById(String id) {
        return loginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }
}
