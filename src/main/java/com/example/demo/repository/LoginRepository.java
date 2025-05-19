package com.example.demo.repository;

import com.example.demo.model.Login;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface LoginRepository extends MongoRepository<Login, String> {
  Optional<Login> findByCorreo(String correo);
}
