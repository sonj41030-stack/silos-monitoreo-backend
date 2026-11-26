package com.sonj.authservice.config;

import com.sonj.authservice.model.Rol;
import com.sonj.authservice.model.Usuario;
import com.sonj.authservice.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Usuario("Operador Demo", "operador@silos.com", encoder.encode("operador123"), Rol.OPERADOR));
                repo.save(new Usuario("Admin Demo", "admin@silos.com", encoder.encode("admin123"), Rol.ADMINISTRADOR));
                repo.save(new Usuario("Jefatura Demo", "jefe@silos.com", encoder.encode("jefe123"), Rol.JEFATURA));
            }
        };
    }
}