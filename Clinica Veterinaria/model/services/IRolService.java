package model.services;

import model.entities.Rol;
import java.util.List;

public interface IRolService {
    Rol crearSiNoExiste(String nombre);
    List<Rol> listar();
}
