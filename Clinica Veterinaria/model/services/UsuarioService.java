package model.services;

import java.util.List;
import java.util.Optional;
import model.entities.Usuario;
import model.repositories.IUsuarioRepository;

public class UsuarioService implements IUsuarioService {
    private final IUsuarioRepository repo;

    public UsuarioService(IUsuarioRepository repo) { this.repo = repo; }

    @Override public Usuario crear(Usuario u, String passwordPlano) {
        u.setPasswordHash(PasswordUtil.sha256(passwordPlano));
        return repo.save(u);
    }

    @Override public Usuario editar(Usuario u) { return repo.save(u); }

    @Override public void desactivar(long id) { repo.setEnabled(id, false); }

    @Override public void activar(long id) { repo.setEnabled(id, true); }   // <- NUEVO

    @Override public Optional<Usuario> buscarPorEmail(String email) { return repo.findByEmail(email); }

    @Override public Optional<Usuario> porId(long id) { return repo.findById(id); } // <- NUEVO

    @Override public List<Usuario> buscar(String texto) { return repo.search(texto); }

    @Override public void resetPassword(long id, String nuevoPassword) {
        Usuario u = repo.findById(id).orElseThrow();
        u.setPasswordHash(PasswordUtil.sha256(nuevoPassword));
        repo.save(u);
    }
}
