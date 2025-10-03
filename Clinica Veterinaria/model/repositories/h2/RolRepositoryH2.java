package model.repositories.h2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.entities.Rol;
import model.repositories.IRolRepository;

public class RolRepositoryH2 implements IRolRepository {

    @Override public Rol save(Rol r) {
        try (Connection c = ConnectionFactory.get()) {
            if (r.getId() == 0) {
                try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO roles(nombre, habilitado) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, r.getName());
                    ps.setBoolean(2, r.isEnabled());
                    ps.executeUpdate();
                    try (ResultSet ks = ps.getGeneratedKeys()) { if (ks.next()) r.setId(ks.getLong(1)); }
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE roles SET nombre=?, habilitado=? WHERE id=?")) {
                    ps.setString(1, r.getName());
                    ps.setBoolean(2, r.isEnabled());
                    ps.setLong(3, r.getId());
                    ps.executeUpdate();
                }
            }
            return r;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public Optional<Rol> findById(long id) {
        String sql = "SELECT id,nombre,habilitado FROM roles WHERE id=?";
        try (Connection c = ConnectionFactory.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public Optional<Rol> findByNombre(String nombre) {
        String sql = "SELECT id,nombre,habilitado FROM roles WHERE nombre=?";
        try (Connection c = ConnectionFactory.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public List<Rol> findAll() {
        List<Rol> out = new ArrayList<>();
        String sql = "SELECT id,nombre,habilitado FROM roles ORDER BY nombre";
        try (Connection c = ConnectionFactory.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    private Rol map(ResultSet rs) throws SQLException {
        Rol r = new Rol();
        r.setId(rs.getLong("id"));
        r.setName(rs.getString("nombre"));
        r.setEnabled(rs.getBoolean("habilitado"));
        return r;
    }
}
