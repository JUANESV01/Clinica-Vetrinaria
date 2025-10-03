package app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import model.entities.Rol;
import model.entities.Usuario;
import model.repositories.h2.ConnectionFactory;
import model.repositories.h2.RolRepositoryH2;
import model.repositories.h2.UsuarioRepositoryH2;
import model.services.AuthService;
import model.services.IAuthService;
import model.services.PasswordUtil;
import model.services.RolService;
import ui.LoginFrame;

public class App {

    public static void main(String[] args) {
        initSchema();   // crea tablas si no existen
        seed();         // inserta roles + admin

        SwingUtilities.invokeLater(() -> {
            var usuarioRepo = new UsuarioRepositoryH2();
            IAuthService auth = new AuthService(usuarioRepo);
            new LoginFrame(auth).setVisible(true);
        });
    }

    // Lee y ejecuta resources/schema.sql SIN parámetros “nombrados”
    private static void initSchema() {
        try (Connection c = ConnectionFactory.get();
             BufferedReader br = new BufferedReader(
                 new InputStreamReader(new FileInputStream("resources/schema.sql"), "UTF-8"))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');

            for (String sql : sb.toString().split(";")) {
                String s = sql.trim();
                if (!s.isEmpty()) {
                    try (PreparedStatement ps = c.prepareStatement(s)) { ps.execute(); }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando schema", e);
        }
    }

    // Crea roles y usuario admin si no existen
    private static void seed() {
        var rolRepo  = new RolRepositoryH2();
        var userRepo = new UsuarioRepositoryH2();
        var rolSrv   = new RolService(rolRepo);

        Rol admin = rolSrv.crearSiNoExiste("Administrador");
        rolSrv.crearSiNoExiste("Médico");
        rolSrv.crearSiNoExiste("Auxiliar");

        userRepo.findByEmail("admin@vet.com").orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNombre("Admin");
            u.setEmail("admin@vet.com");
            u.setPasswordHash(PasswordUtil.sha256("admin123"));
            u.setRol(admin);
            return userRepo.save(u);
        });
    }
}
