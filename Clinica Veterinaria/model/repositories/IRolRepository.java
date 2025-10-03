package model.repositories;

import java.util.List;
import java.util.Optional;
import model.entities.Rol;

public interface IRolRepository {
    Rol save(Rol rol); // insert/update
    Optional<Rol> findById(long id);
    Optional<Rol> findByNombre(String nombre);
    List<Rol> findAll();
}
