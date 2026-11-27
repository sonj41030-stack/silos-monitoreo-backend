package com.sonj.msauth.service;

import com.sonj.msauth.dto.UsuarioAdminResponse;
import com.sonj.msauth.entity.RolUsuario;
import com.sonj.msauth.entity.Usuario;
import com.sonj.msauth.exception.ApiException;
import com.sonj.msauth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;

    public List<UsuarioAdminResponse> listar(String emailAdmin) {
        exigirAdmin(emailAdmin);
        return repo.findAll(Sort.by("nombre")).stream().map(this::aDto).toList();
    }

    @Transactional
    public UsuarioAdminResponse cambiarRol(String emailAdmin, String id, RolUsuario nuevoRol) {
        Usuario admin = exigirAdmin(emailAdmin);
        Usuario usuario = buscar(id);
        if (admin.getId().equals(usuario.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "No puedes cambiar tu propio rol.");
        }
        usuario.setRol(nuevoRol);
        repo.save(usuario);
        return aDto(usuario);
    }

    @Transactional
    public UsuarioAdminResponse cambiarEstado(String emailAdmin, String id, boolean activo) {
        Usuario admin = exigirAdmin(emailAdmin);
        Usuario usuario = buscar(id);
        if (!activo && admin.getId().equals(usuario.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "No puedes desactivar tu propia cuenta.");
        }
        usuario.setActivo(activo);
        repo.save(usuario);
        return aDto(usuario);
    }

    private Usuario exigirAdmin(String email) {
        Usuario u = repo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Token ausente o inválido."));
        if (!u.isActivo() || u.getRol() != RolUsuario.ADMINISTRADOR) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No tienes permisos para esta operación.");
        }
        return u;
    }

    private Usuario buscar(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    private UsuarioAdminResponse aDto(Usuario u) {
        return new UsuarioAdminResponse(u.getId(), u.getNombre(), u.getEmail(),
                u.getFechaNacimiento(), u.getRol().name(), u.isActivo());
    }
}