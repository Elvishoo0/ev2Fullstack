package com.example.demo.controller;

import com.example.demo.model.Login;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.LoginResponse;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

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
    public ResponseEntity<CollectionModel<EntityModel<Login>>> listUsers() {
        List<EntityModel<Login>> users = svc.listAll().stream()
            .map(user -> EntityModel.of(user,
                linkTo(methodOn(AuthController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(AuthController.class).listUsers()).withRel("all-users")))
            .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(users,
            linkTo(methodOn(AuthController.class).listUsers()).withSelfRel()));
    }

    /** GET /auth/users/{id} */
    @GetMapping("/users/{id}")
    public ResponseEntity<EntityModel<Login>> getUserById(@PathVariable String id) {
        Login user = svc.findById(id);  // asegúrate de que AuthService tenga este método
        return ResponseEntity.ok(EntityModel.of(user,
            linkTo(methodOn(AuthController.class).getUserById(id)).withSelfRel(),
            linkTo(methodOn(AuthController.class).listUsers()).withRel("all-users")));
    }
}
