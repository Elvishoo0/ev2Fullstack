package com.example.demo.controller;

import com.example.demo.model.Login;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.LoginResponse;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("caro@example.com", "123456", "TI-2024");
        Login user = new Login();
        user.setCorreo("caro@example.com");
        user.setContrasena("123456");
        user.setIdCarrera("TI-2024");

        when(authService.register(request.correo(), request.contrasena(), request.idCarrera()))
                .thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("caro@example.com"))
                .andExpect(jsonPath("$.idCarrera").value("TI-2024"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("caro@example.com", "123456");
        LoginResponse response = new LoginResponse("caro@example.com", "token123");

        when(authService.login(loginRequest.correo(), loginRequest.contrasena()))
                .thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("caro@example.com"))
                .andExpect(jsonPath("$.message").value("token123")); // usa los nombres reales de tu DTO
    }

    @Test
    void testListUsers() throws Exception {
        List<Login> users = List.of(
                new Login("eashcbns", "caro@example.com", "123456", "TI-2024"),
                new Login("eashcbn2edas", "sofi@example.com", "987654", "TI-2025")
        );

        when(authService.listAll()).thenReturn(users);

        mockMvc.perform(get("/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].correo").value("caro@example.com"))
                .andExpect(jsonPath("$[1].correo").value("sofi@example.com"));
    }
}
