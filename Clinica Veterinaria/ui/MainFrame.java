package ui;

import javax.swing.*;
import model.entities.Usuario;
import model.repositories.h2.UsuarioRepositoryH2;
import model.services.AuthService;
import model.services.IAuthService;

public class MainFrame extends JFrame {
    private final Usuario actual;

    public MainFrame(Usuario actual) {
        this.actual = actual;

        setTitle("Clínica Vet - Principal (" + actual.getRol().getName() + ")");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Menú Usuarios (solo Admin)
        var menu = new JMenuBar();
        var mUsuarios = new JMenu("Usuarios");
        var miGestion = new JMenuItem("Gestionar usuarios");
        miGestion.addActionListener(e -> new UsuariosFrame(actual).setVisible(true));
        mUsuarios.add(miGestion);
        if ("Administrador".equalsIgnoreCase(actual.getRol().getName())) {
            menu.add(mUsuarios);
        }

        // Menú Sesión -> Cerrar sesión
        var mSesion = new JMenu("Sesión");
        var miCerrar = new JMenuItem("Cerrar sesión");
        miCerrar.addActionListener(e -> {
            // Abre de nuevo el Login y cierra este frame
            IAuthService auth = new AuthService(new UsuarioRepositoryH2());
            new LoginFrame(auth).setVisible(true);
            dispose();
        });
        mSesion.add(miCerrar);
        menu.add(mSesion);

        setJMenuBar(menu);
    }
}
