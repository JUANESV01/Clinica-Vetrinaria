package model.repositories.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // BD en carpeta ./data (se crea sola)
    private static final String URL  = "jdbc:h2:file:./data/clinica;AUTO_SERVER=TRUE;MODE=MySQL";
    private static final String USER = "sa";
    private static final String PASS = "";

    static {
        try { Class.forName("org.h2.Driver"); }
        catch (ClassNotFoundException e) { throw new RuntimeException("No se encontró el driver H2", e); }
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
