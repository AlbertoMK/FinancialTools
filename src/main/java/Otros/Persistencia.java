package Otros;

import Modelo.Deposito;

import java.sql.*;

public class Persistencia {

    private final static String URL ="jdbc:sqlite:Database";

    private Persistencia() {}

    public static void guardarDeposito(Deposito deposito) {
        Connection connection = abrirConexion();
        String sql = "INSERT INTO Deposito (id, desembolso, tae, fechaContratacion, comisionCompra, nombre, venta_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, deposito.getId());
            statement.setDouble(2, deposito.getDesembolso());
            statement.setDouble(3, deposito.getTAE());
            statement.setDate(4, Utils.CalendarToSQLDate(deposito.getFechaContratacion()));
            statement.setDouble(5, deposito.getComisionCompra());
            statement.setString(6, deposito.getNombre());
            statement.setNull(7, Types.INTEGER);
            statement.executeUpdate();

            cerrarConexion(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection abrirConexion() {
        Connection connection = null;
        try  {
            connection = DriverManager.getConnection(URL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    private static void cerrarConexion(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

