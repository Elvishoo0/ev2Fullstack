package com.example.demo.controller;

import com.example.demo.model.Login;
import com.example.demo.model.dto.LoginResponse;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Tests para el endpoint POST /auth/register ---

    @Test
    @DisplayName("Debe registrar un nuevo usuario y retornar un estado 200 OK")
    void register_ShouldReturnOk_WhenSuccessful() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("newuser@example.com", "securePass123", "Ingenieria");
        Login registeredLogin = new Login(1L, "newuser@example.com", "securePass123", "Ingenieria");

        when(authService.register(
                eq(registerRequest.correo()),
                eq(registerRequest.contrasena()),
                eq(registerRequest.idCarrera())
        )).thenReturn(registeredLogin);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.correo").value("newuser@example.com"))
                .andExpect(jsonPath("$.idCarrera").value("Ingenieria"));

        verify(authService, times(1)).register(
                eq(registerRequest.correo()),
                eq(registerRequest.contrasena()),
                eq(registerRequest.idCarrera())
        );
    }

    @Test
    @DisplayName("Debe retornar un estado 400 Bad Request si el correo ya existe al registrar")
    void register_ShouldReturnBadRequest_WhenEmailExists() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("existing@example.com", "pass", "Carrera");

        when(authService.register(
                eq(registerRequest.correo()),
                eq(registerRequest.contrasena()),
                eq(registerRequest.idCarrera())
        )).thenThrow(new IllegalStateException("El correo ya existe: " + registerRequest.correo()));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, times(1)).register(
                eq(registerRequest.correo()),
                eq(registerRequest.contrasena()),
                eq(registerRequest.idCarrera())
        );
    }

    // --- Tests para el endpoint POST /auth/login ---

    @Test
    @DisplayName("Debe realizar el login y retornar un estado 200 OK con LoginResponse")
    void login_ShouldReturnOk_WhenSuccessful() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "correctPass");
        LoginResponse loginResponse = new LoginResponse(100L, "login ok");

        when(authService.login(
                eq(loginRequest.correo()),
                eq(loginRequest.contrasena())
        )).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(100L))
                .andExpect(jsonPath("$.message").value("login ok"));

        verify(authService, times(1)).login(
                eq(loginRequest.correo()),
                eq(loginRequest.contrasena())
        );
    }

    @Test
    @DisplayName("Debe retornar un estado 400 Bad Request si las credenciales de login son inválidas")
    void login_ShouldReturnBadRequest_WhenInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "wrongPass");

        when(authService.login(
                eq(loginRequest.correo()),
                eq(loginRequest.contrasena())
        )).thenThrow(new IllegalArgumentException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, times(1)).login(
                eq(loginRequest.correo()),
                eq(loginRequest.contrasena())
        );
    }

    @Test
    @DisplayName("Debe retornar un estado 400 Bad Request si el usuario no es encontrado en login")
    void login_ShouldReturnBadRequest_WhenUserNotFound() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "anyPass");

        when(authService.login(
                eq(loginRequest.correo()),
                eq(loginRequest.contrasena())
        )).thenThrow(new IllegalArgumentException("Usuario no encontrado"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, times(1)).login(
                eq(loginRequest.correo()),
                eq(loginRequest.contrasena())
        );
    }

    // --- Tests para el endpoint GET /auth/users ---

    @Test
    @DisplayName("Debe listar todos los usuarios y retornar un estado 200 OK")
    void listUsers_ShouldReturnOk_WithAllUsers() throws Exception {
        Login user1 = new Login(1L, "user1@example.com", "pass1", "IT");
        Login user2 = new Login(2L, "user2@example.com", "pass2", "Design");
        List<Login> allUsers = Arrays.asList(user1, user2);

        when(authService.listAll()).thenReturn(allUsers);

        mockMvc.perform(get("/auth/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].correo").value("user1@example.com"))
                .andExpect(jsonPath("$[1].correo").value("user2@example.com"));

        verify(authService, times(1)).listAll();
    }

    @Test
    @DisplayName("Debe retornar una lista vacía y estado 200 OK si no hay usuarios")
    void listUsers_ShouldReturnOk_WithEmptyList() throws Exception {
        List<Login> emptyList = Arrays.asList();

        when(authService.listAll()).thenReturn(emptyList);

        mockMvc.perform(get("/auth/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(authService, times(1)).listAll();
    }
}

// --- CLASES DTO AUXILIARES PARA EL TEST ---

final class RegisterRequest {
    private String correo;
    private String contrasena;
    private String idCarrera;

    public RegisterRequest(String correo, String contrasena, String idCarrera) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.idCarrera = idCarrera;
    }

    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getIdCarrera() { return idCarrera; }

    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setIdCarrera(String idCarrera) { this.idCarrera = idCarrera; }
}

final class LoginRequest {
    private String correo;
    private String contrasena;

    public LoginRequest(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }

    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}