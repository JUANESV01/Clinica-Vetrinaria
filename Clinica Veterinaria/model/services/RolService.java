package model.services;

import model.entities.Rol;
import model.repositories.IRolRepository;
import java.util.List;

public class RolService implements IRolService {
    private final IRolRepository repo;
    public RolService(IRolRepository repo) { this.repo = repo; }

    @Override public Rol crearSiNoExiste(String nombre) {
        return repo.findByNombre(nombre).orElseGet(() -> {
            Rol r = new Rol(); r.setName(nombre); r.setEnabled(true);
            return repo.save(r);
        });
    }

    @Override public List<Rol> listar() { return repo.findAll(); }
}
