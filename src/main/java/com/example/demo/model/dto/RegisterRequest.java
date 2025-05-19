package com.example.demo.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @Email(message = "Correo inválido")
    String correo,

    @NotBlank(message = "La contraseña no puede estar vacía")
    String contrasena,

    @NotBlank(message = "El idCarrera no puede estar vacío")
    String idCarrera
) {}
