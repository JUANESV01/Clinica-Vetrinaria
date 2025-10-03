package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.entities.Rol;
import model.entities.Usuario;
import model.repositories.h2.RolRepositoryH2;
import model.repositories.h2.UsuarioRepositoryH2;
import model.services.RolService;
import model.services.UsuarioService;

public class UsuariosFrame extends JFrame {
    private final Usuario actual; // refuerzo autorización
    private final UsuarioService usuarios = new UsuarioService(new UsuarioRepositoryH2());
    private final RolService roles = new RolService(new RolRepositoryH2());

    private final JTextField txtBuscar = new JTextField(20);
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnNuevo = new JButton("Nuevo");
    private final JButton btnEditar = new JButton("Editar");
    private final JButton btnDesactivar = new JButton("Desactivar");
    private final JButton btnActivar    = new JButton("Activar");
    private final JButton btnResetPass  = new JButton("Reset pass");

    private final DefaultListModel<String> modelo = new DefaultListModel<>();
    private final JList<String> lst = new JList<>(modelo);

    public UsuariosFrame(Usuario actual) {
        this.actual = actual;

        // Refuerzo: solo Admin puede entrar aquí
        if (!"Administrador".equalsIgnoreCase(actual.getRol().getName())) {
            JOptionPane.showMessageDialog(this, "No tienes permiso para gestionar usuarios.");
            dispose();
            return;
        }

        setTitle("Gestión de usuarios");
        setSize(800, 800);
        setLocationRelativeTo(null);

        var top = new JPanel();
        top.add(new JLabel("Buscar:"));
        top.add(txtBuscar);
        top.add(btnBuscar);
        top.add(btnNuevo);
        top.add(btnEditar);
        top.add(btnDesactivar);
        top.add(btnActivar);
        top.add(btnResetPass);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(lst), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> refrescar());
        btnNuevo.addActionListener(e -> crearUsuario());
        btnEditar.addActionListener(e -> editarUsuario());
        btnDesactivar.addActionListener(e -> onDesactivar());
        btnActivar.addActionListener(e -> onActivar());
        btnResetPass.addActionListener(e -> onResetPass());

        refrescar();
    }

    private void refrescar() {
        modelo.clear();
        List<Usuario> data = usuarios.buscar(txtBuscar.getText().trim());
        for (Usuario u : data) {
            String estado = u.isHabilitado() ? "Activo" : "Inactivo";
            modelo.addElement(u.getId() + " | " + u.getNombre() + " | " + u.getEmail() + " | " + u.getRol().getName() + " | " + estado);
        }
    }

    private Long idSeleccionado() {
        String item = lst.getSelectedValue();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la lista.");
            return null;
        }
        try {
            String idStr = item.split("\\|")[0].trim();
            return Long.parseLong(idStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No pude leer el ID del usuario seleccionado.");
            return null;
        }
    }

    private void crearUsuario() {
        JTextField nombre = new JTextField();
        JTextField email = new JTextField();
        JPasswordField pass = new JPasswordField();
        var rolesLista = roles.listar().toArray(new Rol[0]);
        var comboRol = new JComboBox<>(rolesLista);

        JPanel p = new JPanel(new GridLayout(0,1));
        p.add(new JLabel("Nombre:")); p.add(nombre);
        p.add(new JLabel("Email:")); p.add(email);
        p.add(new JLabel("Contraseña:")); p.add(pass);
        p.add(new JLabel("Rol:")); p.add(comboRol);

        int ok = JOptionPane.showConfirmDialog(this, p, "Nuevo usuario", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            if (nombre.getText().isBlank() || email.getText().isBlank() || pass.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }
            Usuario u = new Usuario();
            u.setNombre(nombre.getText().trim());
            u.setEmail(email.getText().trim());
            u.setRol((Rol) comboRol.getSelectedItem());
            usuarios.crear(u, new String(pass.getPassword()));
            refrescar();
        }
    }

    private void editarUsuario() {
        Long id = idSeleccionado();
        if (id == null) return;

        var opt = usuarios.porId(id);
        if (opt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
            return;
        }
        Usuario u = opt.get();

        JTextField nombre = new JTextField(u.getNombre());
        JTextField email  = new JTextField(u.getEmail());
        var rolesLista = roles.listar().toArray(new Rol[0]);
        var comboRol = new JComboBox<>(rolesLista);
        comboRol.setSelectedItem(
            roles.listar().stream().filter(r -> r.getId() == u.getRol().getId()).findFirst().orElse(rolesLista[0])
        );

        JPanel p = new JPanel(new GridLayout(0,1));
        p.add(new JLabel("Nombre:")); p.add(nombre);
        p.add(new JLabel("Email:"));  p.add(email);
        p.add(new JLabel("Rol:"));    p.add(comboRol);

        int ok = JOptionPane.showConfirmDialog(this, p, "Editar usuario", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            u.setNombre(nombre.getText().trim());
            u.setEmail(email.getText().trim());
            u.setRol((Rol) comboRol.getSelectedItem());
            usuarios.editar(u);
            refrescar();
        }
    }

    private void onDesactivar() {
        Long id = idSeleccionado();
        if (id == null) return;
        int ok = JOptionPane.showConfirmDialog(this, "¿Desactivar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            usuarios.desactivar(id);
            refrescar();
        }
    }

    private void onActivar() {
        Long id = idSeleccionado();
        if (id == null) return;
        int ok = JOptionPane.showConfirmDialog(this, "¿Activar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            usuarios.activar(id);
            refrescar();
        }
    }

    private void onResetPass() {
        Long id = idSeleccionado();
        if (id == null) return;
        JPasswordField pass = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(this, pass, "Nueva contraseña", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String nueva = new String(pass.getPassword());
            if (nueva.isBlank()) {
                JOptionPane.showMessageDialog(this, "La contraseña no puede estar vacía.");
                return;
            }
            usuarios.resetPassword(id, nueva);
            JOptionPane.showMessageDialog(this, "Contraseña actualizada.");
        }
    }
}

