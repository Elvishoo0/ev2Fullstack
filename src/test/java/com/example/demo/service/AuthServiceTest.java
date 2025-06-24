package com.example.demo.service;

import com.example.demo.model.Login;
import com.example.demo.model.dto.LoginResponse;
import com.example.demo.repository.LoginRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void testLoginUserFound() {
        Login user = new Login();
        user.setCorreo("caro@example.com");
        user.setContrasena("123456");

        when(loginRepository.findByCorreoAndContrasena("caro@example.com", "123456"))
                .thenReturn(Optional.of(user));

        LoginResponse response = authService.login("caro@example.com", "123456");

        assertNotNull(response);
        assertEquals("caro@example.com", response.uid()); // se usa uid() porque así se llama el campo
        assertTrue(response.message().startsWith("token_")); // se usa message() porque así se llama el campo
    }

    @Test
    void testLoginUserNotFound() {
        when(loginRepository.findByCorreoAndContrasena("fail@example.com", "wrongpass"))
                .thenReturn(Optional.empty());

        LoginResponse response = authService.login("fail@example.com", "wrongpass");

        assertNull(response);
    }

    @Test
    void testRegisterUser() {
        Login nuevo = new Login();
        nuevo.setCorreo("caro@example.com");
        nuevo.setContrasena("123456");
        nuevo.setIdCarrera("TI-2024");

        when(loginRepository.save(any(Login.class))).thenReturn(nuevo);

        Login result = authService.register("caro@example.com", "123456", "TI-2024");

        assertNotNull(result);
        assertEquals("caro@example.com", result.getCorreo());
        assertEquals("TI-2024", result.getIdCarrera());
    }

    @Test
    void testListAllUsers() {
        List<Login> users = List.of(
                new Login("1", "caro@example.com", "123", "TI-2024"),
                new Login("2", "sofi@example.com", "456", "TI-2025")
        );

        when(loginRepository.findAll()).thenReturn(users);

        List<Login> result = authService.listAll();

        assertEquals(2, result.size());
        assertEquals("caro@example.com", result.get(1).getCorreo());
    }
}
