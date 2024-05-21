package Otros;

import Modelo.Deposito;
import org.sqlite.jdbc4.JDBC4Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Persistencia {

    private final static String URL ="jdbc:sqlite:Database";

    private Persistencia() {}

    // ------------------------------------------------- DEPOSITOS ---------------------------------------------------

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
        finally {
            cerrarConexion(connection);
        }
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
        finally {
            cerrarConexion(connection);
        }
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

    /**
     *
     * @return Lista de representaciones de los depósitos.
     * Cada depósito está representado con una clave por atributo.
     * La clave 'retribuciones' contiene una lista de HashMap con las siguientes claves: 'fecha' y 'importe'.
     * La clave 'venta' contiene un HashMap con una clave por atributo de la venta. NOTA: Si el depósito no estiviese vendido, el valor de la clave 'venta' sería null
     */
    public static List<HashMap<String, Object>> getDepositos() {
        List<HashMap<String, Object>> resultado = new ArrayList<>();
        Connection connection = abrirConexion();
        String sqlDepositos = "SELECT * FROM Deposito";
        ResultSet depositosResult;
        try {
            depositosResult = connection.createStatement().executeQuery(sqlDepositos);
            while(depositosResult.next()) {
                HashMap<String, Object> representacionDeposito = new HashMap<>();
                int id = depositosResult.getInt("id");
                String nombre = depositosResult.getString("nombre");
                double desembolso = depositosResult.getDouble("desembolso");
                Calendar fechaContratacion = Utils.SQLDateToCalendar(depositosResult.getDate("fechaContratacion"));
                double comisionCompra = depositosResult.getDouble("comisionCompra");
                double tae = depositosResult.getDouble("tae");
                int ventaId = depositosResult.getInt("venta_id");
                HashMap<String, String> representacionVenta = getVenta(ventaId);
                List<HashMap<String, String>> representacionRetribuciones = getRetribucionesDeposito(id);

                representacionDeposito.put("id", id);
                representacionDeposito.put("nombre", nombre);
                representacionDeposito.put("desembolso", desembolso);
                representacionDeposito.put("fechaContratacion", fechaContratacion);
                representacionDeposito.put("comisionCompra", comisionCompra);
                representacionDeposito.put("tae", tae);
                representacionDeposito.put("venta", representacionVenta);
                representacionDeposito.put("retribuciones", representacionRetribuciones);
                resultado.add(representacionDeposito);
            }
            return resultado;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            cerrarConexion(connection);
        }
    }

    /**
     *
     * @param idVenta Id de la venta a la que se quiere acceder
     * @return HashMap con los atributos de la venta, si no tiene venta devuelve null
     */
    private static HashMap<String, String> getVenta(int idVenta) {
        ResultSet ventaResult;
        Connection connection = abrirConexion();
        try {
            String sqlVenta = "SELECT * FROM VentaDeposito WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlVenta);
            preparedStatement.setInt(1, idVenta);
            ventaResult = preparedStatement.executeQuery();
            if(ventaResult.isBeforeFirst()) { //hay elemento
                HashMap<String, String> resultado = new HashMap<>();
                ventaResult.next();
                int id = ventaResult.getInt("id");
                Date fecha = ventaResult.getDate("fecha");
                double comision = ventaResult.getDouble("comision");
                double importeVenta = ventaResult.getDouble("importeVenta");
                resultado.put("id", String.valueOf(id));
                resultado.put("fecha", Utils.serializarFechaEuropea(Utils.SQLDateToCalendar(fecha)));
                resultado.put("comision", String.valueOf(comision));
                resultado.put("importeVenta", String.valueOf(importeVenta));
                return resultado;
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            cerrarConexion(connection);
        }
    }

    private static List<HashMap<String, String>> getRetribucionesDeposito(int idDeposito) {
        List<HashMap<String, String>> resultado = new ArrayList<>();
        ResultSet result;
        Connection connection = abrirConexion();
        String sql = "SELECT * FROM RetribucionDeposito WHERE deposito_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, idDeposito);
            result = preparedStatement.executeQuery();
            while(result.next()) {
                HashMap<String, String> representacion = new HashMap<>();
                Calendar fecha = Utils.SQLDateToCalendar(result.getDate("fecha"));
                double importe = result.getDouble("importe");
                representacion.put("fecha", Utils.serializarFechaEuropea(fecha));
                representacion.put("importe", String.valueOf(importe));
                resultado.add(representacion);
            }
            return resultado;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            cerrarConexion(connection);
        }
    }

    // ------------------------------------------------- ACCIONES ---------------------------------------------------

    // ------------------------------------------------- CUENTAS ---------------------------------------------------

    // ------------------------------------------------- OTROS ---------------------------------------------------

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

    public static int getSiguienteId() {
        Connection connection = abrirConexion();
        String sql = "SELECT * FROM SiguienteId";
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            result.next();
            return result.getInt("siguienteId");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            cerrarConexion(connection);
        }
    }
}

