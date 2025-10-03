package model.repositories.h2;

import model.entities.Rol;
import model.entities.Usuario;
import model.repositories.IUsuarioRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepositoryH2 implements IUsuarioRepository {

    private Rol mapRol(ResultSet rs) throws SQLException {
        Rol r = new Rol();
        r.setId(rs.getLong("rol_id"));
        r.setName(rs.getString("rol_nombre"));
        r.setEnabled(rs.getBoolean("rol_habilitado"));
        return r;
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setHabilitado(rs.getBoolean("habilitado"));
        u.setRol(mapRol(rs));
        return u;
    }

    @Override public Usuario save(Usuario u) {
        try (Connection c = ConnectionFactory.get()) {
            if (u.getId() == 0) {
                try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO usuarios(nombre,email,password_hash,habilitado,rol_id) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, u.getNombre());
                    ps.setString(2, u.getEmail());
                    ps.setString(3, u.getPasswordHash());
                    ps.setBoolean(4, u.isHabilitado());
                    ps.setLong(5, u.getRol().getId());
                    ps.executeUpdate();
                    try (ResultSet ks = ps.getGeneratedKeys()) { if (ks.next()) u.setId(ks.getLong(1)); }
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE usuarios SET nombre=?, email=?, password_hash=?, habilitado=?, rol_id=? WHERE id=?")) {
                    ps.setString(1, u.getNombre());
                    ps.setString(2, u.getEmail());
                    ps.setString(3, u.getPasswordHash());
                    ps.setBoolean(4, u.isHabilitado());
                    ps.setLong(5, u.getRol().getId());
                    ps.setLong(6, u.getId());
                    ps.executeUpdate();
                }
            }
            return u;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public Optional<Usuario> findByEmail(String email) {
        String sql = """
            SELECT u.id,u.nombre,u.email,u.password_hash,u.habilitado,u.rol_id,
                   r.nombre AS rol_nombre, r.habilitado AS rol_habilitado
            FROM usuarios u JOIN roles r ON r.id = u.rol_id
            WHERE u.email = ?
        """;
        try (Connection c = ConnectionFactory.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public Optional<Usuario> findById(long id) {
        String sql = """
            SELECT u.id,u.nombre,u.email,u.password_hash,u.habilitado,u.rol_id,
                   r.nombre AS rol_nombre, r.habilitado AS rol_habilitado
            FROM usuarios u JOIN roles r ON r.id = u.rol_id
            WHERE u.id = ?
        """;
        try (Connection c = ConnectionFactory.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return Optional.of(map(rs)); }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public List<Usuario> search(String texto) {
        String like = "%" + texto + "%";
        String sql = """
            SELECT u.id,u.nombre,u.email,u.password_hash,u.habilitado,u.rol_id,
                   r.nombre AS rol_nombre, r.habilitado AS rol_habilitado
            FROM usuarios u JOIN roles r ON r.id = u.rol_id
            WHERE u.nombre LIKE ? OR u.email LIKE ?
            ORDER BY u.nombre
        """;
        List<Usuario> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override public void setEnabled(long id, boolean enabled) {
        String sql = "UPDATE usuarios SET habilitado=? WHERE id=?";
        try (Connection c = ConnectionFactory.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, enabled);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
