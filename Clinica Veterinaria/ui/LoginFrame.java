package ui;

import java.awt.*;
import javax.swing.*;
import model.entities.Usuario;
import model.services.IAuthService;

public class LoginFrame extends JFrame {
    private final IAuthService auth;
    private final JTextField txtEmail = new JTextField("admin@vet.com", 20);
    private final JPasswordField txtPass = new JPasswordField("admin123", 20);
    private final JButton btnLogin = new JButton("Ingresar");

    public LoginFrame(IAuthService auth) {
        this.auth = auth;
        setTitle("Clínica Vet - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 220);
        setLocationRelativeTo(null);

        var p = new JPanel(new GridLayout(3,2,8,8));
        p.add(new JLabel("Email:")); p.add(txtEmail);
        p.add(new JLabel("Contraseña:")); p.add(txtPass);
        p.add(new JLabel("")); p.add(btnLogin);
        add(p);

        btnLogin.addActionListener(e -> onLogin());
    }

    private void onLogin() {
        String email = txtEmail.getText().trim();
        String pass  = new String(txtPass.getPassword());
        auth.login(email, pass).ifPresentOrElse(this::goMain,
            () -> JOptionPane.showMessageDialog(this, "Credenciales inválidas o usuario desactivado"));
    }

    private void goMain(Usuario u) {
        new MainFrame(u).setVisible(true);
        dispose();
    }
}
