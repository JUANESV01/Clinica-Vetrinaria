package model.services;

import model.entities.Usuario;
import model.repositories.IUsuarioRepository;

import java.util.Optional;

public class AuthService implements IAuthService {
    private final IUsuarioRepository usuarios;

    public AuthService(IUsuarioRepository usuarios) { this.usuarios = usuarios; }

    @Override public Optional<Usuario> login(String email, String passwordPlano) {
        String hash = PasswordUtil.sha256(passwordPlano);
        return usuarios.findByEmail(email).filter(u -> u.getPasswordHash().equals(hash) && u.isHabilitado());
    }
}
