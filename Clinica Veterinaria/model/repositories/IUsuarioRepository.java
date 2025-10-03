package model.repositories;

import java.util.List;
import java.util.Optional;
import model.entities.Usuario;

public interface IUsuarioRepository {
    Usuario save(Usuario u); // insert/update
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(long id);
    List<Usuario> search(String texto); // por nombre o email
    void setEnabled(long id, boolean enabled);
}
