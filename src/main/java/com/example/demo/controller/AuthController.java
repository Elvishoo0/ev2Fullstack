package com.example.demo.controller;

import com.example.demo.model.Login;
import com.example.demo.model.dto.*;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService svc;

  public AuthController(AuthService svc) {
    this.svc = svc;
  }

  /** POST /auth/register */
  @PostMapping("/register")
  public ResponseEntity<Login> register(@Valid @RequestBody RegisterRequest body) {
    Login created = svc.register(
      body.correo(),
      body.contrasena(),
      body.idCarrera()
    );
    return ResponseEntity.ok(created);
  }

  /** POST /auth/login */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest body) {
    LoginResponse resp = svc.login(
      body.correo(),
      body.contrasena()
    );
    return ResponseEntity.ok(resp);
  }

  /** GET /auth/users */
  @GetMapping("/users")
  public ResponseEntity<List<Login>> listUsers() {
    List<Login> all = svc.listAll();
    return ResponseEntity.ok(all);
  }
}
