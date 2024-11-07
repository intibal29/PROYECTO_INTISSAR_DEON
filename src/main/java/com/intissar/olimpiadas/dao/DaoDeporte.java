package com.intissar.olimpiadas.dao;

import com.intissar.olimpiadas.db.DBConnect;
import com.intissar.olimpiadas.model.Deporte;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que maneja las operaciones de acceso a datos relacionadas con la tabla "Deporte".
 * Proporciona métodos para crear, leer, actualizar y eliminar deportes en la base de datos.
 */
public class DaoDeporte {

    /**
     * Busca un deporte en la base de datos por su ID.
     *
     * @param id ID del deporte a buscar.
     * @return El objeto Deporte correspondiente o null si no se encuentra.
     */
    public static Deporte getDeporte(int id) {
        DBConnect connection; // Conexión a la base de datos
        Deporte deporte = null; // Inicializa la variable de deporte
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT id_deporte, nombre FROM Deporte WHERE id_deporte = ?";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, id); // Establece el ID del deporte en la consulta
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta
            if (rs.next()) { // Si hay resultados
                int id_deporte = rs.getInt("id_deporte");
                String nombre = rs.getString("nombre");
                deporte = new Deporte(id_deporte, nombre); // Crea un nuevo objeto Deporte
            }
            rs.close(); // Cierra el ResultSet
            connection.closeConnection(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
        }
        return deporte; // Devuelve el deporte encontrado o null
    }

    /**
     * Carga todos los deportes de la base de datos y los devuelve en una lista observable.
     *
     * @return Lista observable de deportes.
     */
    public static ObservableList<Deporte> cargarListado() {
        DBConnect connection; // Conexión a la base de datos
        ObservableList<Deporte> deportes = FXCollections.observableArrayList(); // Lista observable para almacenar deportes
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT id_deporte, nombre FROM Deporte";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta
            while (rs.next()) { // Itera a través de los resultados
                int id_deporte = rs.getInt("id_deporte");
                String nombre = rs.getString("nombre");
                Deporte deporte = new Deporte(id_deporte, nombre); // Crea un nuevo objeto Deporte
                deportes.add(deporte); // Agrega el deporte a la lista
            }
            rs.close(); // Cierra el ResultSet
            connection.closeConnection(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
        }
        return deportes; // Devuelve la lista de deportes
    }

    /**
     * Verifica si un deporte se puede eliminar de la base de datos.
     *
     * @param deporte El objeto Deporte a verificar.
     * @return true si se puede eliminar, false en caso contrario.
     */
    public static boolean esEliminable(Deporte deporte) {
        DBConnect connection; // Conexión a la base de datos
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT count(*) as cont FROM Evento WHERE id_deporte = ?";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, deporte.getId_deporte()); // Establece el ID del deporte en la consulta
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta
            if (rs.next()) { // Si hay resultados
                int cont = rs.getInt("cont"); // Obtiene el conteo de eventos asociados
                rs.close(); // Cierra el ResultSet
                connection.closeConnection(); // Cierra la conexión
                return (cont == 0); // Devuelve true si no hay eventos asociados
            }
            rs.close(); // Cierra el ResultSet
            connection.closeConnection(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
        }
        return false; // Devuelve false si no se puede determinar si es eliminable
    }

    /**
     * Modifica los datos de un deporte en la base de datos.
     *
     * @param deporte      Instancia del deporte con datos actuales.
     * @param deporteNuevo Instancia del deporte con nuevos datos.
     * @return true si la modificación fue exitosa, false en caso contrario.
     */
    public static boolean modificar(Deporte deporte, Deporte deporteNuevo) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "UPDATE Deporte SET nombre = ? WHERE id_deporte = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, deporteNuevo.getNombre()); // Establece el nuevo nombre del deporte
            pstmt.setInt(2, deporte.getId_deporte()); // Establece el ID del deporte a modificar
            int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la actualización
            System.out.println("Actualizado deporte"); // Mensaje de éxito
            pstmt.close(); // Cierra la declaración
            connection.closeConnection(); // Cierra la conexión
            return filasAfectadas > 0; // Devuelve true si se afectaron filas
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
            return false; // Devuelve false si hubo un error
        }
    }

    /**
     * Inserta un nuevo deporte en la base de datos.
     *
     * @param deporte Instancia del modelo Deporte con datos nuevos.
     * @return ID del nuevo deporte o -1 si la inserción falla.
     */
    public static int insertar(Deporte deporte) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "INSERT INTO Deporte (nombre) VALUES (?)";
            pstmt = connection.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, deporte.getNombre()); // Establece el nombre del nuevo deporte
            int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la inserción
            System.out.println("Nueva entrada en deporte"); // Mensaje de éxito
            if (filasAfectadas > 0) { // Si se afectaron filas
                ResultSet rs = pstmt.getGeneratedKeys(); // Obtiene las claves generadas
                if (rs.next()) { // Si hay claves generadas
                    int id = rs.getInt(1); // Obtiene el ID del nuevo deporte
                    pstmt.close(); // Cierra la declaración
                    connection.closeConnection(); // Cierra la conexión
                    return id; // Devuelve el ID del nuevo deporte
                }
            }
            pstmt.close(); // Cierra la declaración
            connection.closeConnection(); // Cierra la conexión
            return -1; // Devuelve -1 si no se pudo obtener el ID
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
            return -1; // Devuelve -1 si hubo un error
        }
    }

    /**
     * Elimina un deporte de la base de datos.
     *
     * @param deporte El objeto Deporte a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean eliminar(Deporte deporte) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "DELETE FROM Deporte WHERE id_deporte = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, deporte.getId_deporte()); // Establece el ID del deporte a eliminar
            int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la eliminación
            pstmt.close(); // Cierra la declaración
            connection.closeConnection(); // Cierra la conexión
            System.out.println("Eliminado con éxito"); // Mensaje de éxito
            return filasAfectadas > 0; // Devuelve true si se afectaron filas
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
            return false; // Devuelve false si hubo un error
        }
    }
}