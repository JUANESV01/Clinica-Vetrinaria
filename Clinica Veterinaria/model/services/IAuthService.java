package model.services;

import model.entities.Usuario;
import java.util.Optional;

public interface IAuthService {
    Optional<Usuario> login(String email, String passwordPlano);
}
