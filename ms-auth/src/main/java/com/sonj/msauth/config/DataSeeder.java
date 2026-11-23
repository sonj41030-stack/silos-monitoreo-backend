package com.sonj.msauth.config;

import com.sonj.msauth.entity.RolUsuario;
import com.sonj.msauth.entity.Usuario;
import com.sonj.msauth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        crearSiNoExiste("Carlos Operador", "operador@silos.com", "operador123", RolUsuario.OPERADOR);
        crearSiNoExiste("Ana Administradora", "admin@silos.com", "admin123", RolUsuario.ADMINISTRADOR);
        crearSiNoExiste("Roberto Jefe", "jefe@silos.com", "jefe123", RolUsuario.JEFATURA);
    }

    private void crearSiNoExiste(String nombre, String email, String clave, RolUsuario rol) {
        if (repo.existsByEmailIgnoreCase(email)) {
            return;
        }
        repo.save(Usuario.builder()
                .id(UUID.randomUUID().toString())
                .nombre(nombre)
                .email(email)
                .passwordHash(encoder.encode(clave))
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .rol(rol)
                .build());
    }
}
