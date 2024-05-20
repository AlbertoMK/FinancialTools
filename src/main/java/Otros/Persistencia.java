package Otros;

import Modelo.Deposito;

import java.sql.*;
import java.util.Calendar;

public class Persistencia {

    private final static String URL ="jdbc:sqlite:Database";

    private Persistencia() {}

    public static void guardarDeposito(int id, double desembolso, double tae, Calendar fechaContratacion, double comisionCompra, String nombre) {
        Connection connection = abrirConexion();
        String sql = "INSERT INTO Deposito (id, desembolso, tae, fechaContratacion, comisionCompra, nombre, venta_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setDouble(2, desembolso);
            statement.setDouble(3, tae);
            statement.setDate(4, Utils.CalendarToSQLDate(fechaContratacion));
            statement.setDouble(5, comisionCompra);
            statement.setString(6, nombre);
            statement.setNull(7, Types.INTEGER);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cerrarConexion(connection);
    }

    public static void venderDeposito(Deposito deposito) {
        Connection connection = abrirConexion();
        long id;

        String sql = "INSERT INTO VentaDeposito (fecha, comision, importeVenta) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Utils.CalendarToSQLDate(deposito.getVenta().getFecha()));
            statement.setDouble(2, deposito.getVenta().getComision());
            statement.setDouble(3, deposito.getVenta().getImporteVenta());
            statement.executeUpdate();
            ResultSet generatedKey = statement.getGeneratedKeys();
            id = generatedKey.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        sql = "UPDATE Deposito SET venta_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.setLong(2, deposito.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cerrarConexion(connection);
    }

    public static void añadirRetribucionDeposito(int idDeposito, Calendar fecha, double importe) {
        Connection connection = abrirConexion();
        String sql = "INSERT INTO RetribucionDeposito (fecha, importe, deposito_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Utils.CalendarToSQLDate(fecha));
            statement.setDouble(2, importe);
            statement.setLong(3, idDeposito);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cerrarConexion(connection);
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

    public static void incrementarId(int siguienteId) {
        Connection connection = abrirConexion();
        String sql = "UPDATE SiguienteId SET siguienteId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, siguienteId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        cerrarConexion(connection);
    }
}

