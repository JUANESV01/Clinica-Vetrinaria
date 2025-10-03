package model.services;

import java.util.List;
import java.util.Optional;
import model.entities.Usuario;

public interface IUsuarioService {
    Usuario crear(Usuario u, String passwordPlano);
    Usuario editar(Usuario u);
    void desactivar(long id);
    void activar(long id);                 // <- NUEVO
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> porId(long id);      // <- NUEVO
    List<Usuario> buscar(String texto);
    void resetPassword(long id, String nuevoPassword);
}
