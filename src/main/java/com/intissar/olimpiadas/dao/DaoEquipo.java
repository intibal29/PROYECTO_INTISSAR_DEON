package com.intissar.olimpiadas.dao;

import com.intissar.olimpiadas.db.DBConnect;
import com.intissar.olimpiadas.model.Equipo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase que maneja las operaciones de acceso a datos relacionadas con la tabla "Equipo".
 * Proporciona métodos para crear, leer, actualizar y eliminar equipos en la base de datos.
 */
public class DaoEquipo {

    /**
     * Busca un equipo en la base de datos por su ID.
     *
     * @param id ID del equipo a buscar.
     * @return El objeto Equipo correspondiente o null si no se encuentra.
     */
    public static Equipo getEquipo(int id) {
        DBConnect connection; // Conexión a la base de datos
        Equipo equipo = null; // Inicializa la variable de equipo
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT id_equipo, nombre, iniciales FROM Equipo WHERE id_equipo = ?";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, id); // Establece el ID del equipo en la consulta
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta
            if (rs.next()) { // Si hay resultados
                int id_equipo = rs.getInt("id_equipo");
                String nombre = rs.getString("nombre");
                String iniciales = rs.getString("iniciales");
                equipo = new Equipo(id_equipo, nombre, iniciales); // Crea un nuevo objeto Equipo
            }
            rs.close(); // Cierra el ResultSet
            connection.closeConnection(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
        }
        return equipo; // Devuelve el equipo encontrado o null
    }

    /**
     * Carga todos los equipos de la base de datos y los devuelve en una lista observable.
     *
     * @return Lista observable de equipos.
     */
    public static ObservableList<Equipo> cargarListado() {
        DBConnect connection; // Conexión a la base de datos
        ObservableList<Equipo> equipos = FXCollections.observableArrayList(); // Lista observable para almacenar equipos
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT id_equipo, nombre, iniciales FROM Equipo";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta
            while (rs.next()) { // Itera a través de los resultados
                int id_equipo = rs.getInt("id_equipo");
                String nombre = rs.getString("nombre");
                String iniciales = rs.getString("iniciales");
                Equipo equipo = new Equipo(id_equipo, nombre, iniciales); // Crea un nuevo objeto Equipo
                equipos.add(equipo); // Agrega el equipo a la lista
            }
            rs.close(); // Cierra el ResultSet
            connection.closeConnection(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
        }
        return equipos; // Devuelve la lista de equipos
    }

    /**
     * Verifica si un equipo se puede eliminar de la base de datos.
     *
     * @param equipo El objeto Equipo a verificar.
     * @return true si se puede eliminar, false en caso contrario.
     */
    public static boolean esEliminable(Equipo equipo) {
        DBConnect connection; // Conexión a la base de datos
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT count(*) as cont FROM Participacion WHERE id_equipo = ?";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, equipo.getId_equipo()); // Establece el ID del equipo en la consulta
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta
            if (rs.next()) { // Si hay resultados
                int cont = rs.getInt("cont"); // Obtiene el conteo de participaciones
                rs.close(); // Cierra el ResultSet
                connection.closeConnection(); // Cierra la conexión
                return (cont == 0); // Devuelve true si no hay participaciones asociadas
            }
            rs.close(); // Cierra el ResultSet
            connection.closeConnection(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
        }
        return false; // Devuelve false si no se puede determinar si es eliminable
    }

    /**
     * Modifica los datos de un equipo en la base de datos.
     *
     * @param equipo      Instancia del equipo con datos actuales.
     * @param equipoNuevo Instancia del equipo con nuevos datos.
     * @return true si la modificación fue exitosa, false en caso contrario.
     */
    public static boolean modificar(Equipo equipo, Equipo equipoNuevo) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "UPDATE Equipo SET nombre = ?, iniciales = ? WHERE id_equipo = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, equipoNuevo.getNombre()); // Establece el nuevo nombre del equipo
            pstmt.setString(2, equipoNuevo.getIniciales()); // Establece las nuevas iniciales del equipo
            pstmt.setInt(3, equipo.getId_equipo()); // Establece el ID del equipo a modificar
            int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la actualización
            System.out.println("Actualizado equipo"); // Mensaje de éxito
            pstmt.close(); // Cierra la declaración
            connection.closeConnection(); // Cierra la conexión
            return filasAfectadas > 0; // Devuelve true si se afectaron filas
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
            return false; // Devuelve false si hubo un error
        }
    }

    /**
     * Inserta un nuevo equipo en la base de datos.
     *
     * @param equipo Instancia del modelo Equipo con datos nuevos.
     * @return ID del nuevo equipo o -1 si la inserción falla.
     */
    public static int insertar(Equipo equipo) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "INSERT INTO Equipo (nombre, iniciales) VALUES (?, ?)";
            pstmt = connection.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, equipo.getNombre()); // Establece el nombre del nuevo equipo
            pstmt.setString(2, equipo.getIniciales()); // Establece las iniciales del nuevo equipo
            int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la inserción
            System.out.println("Nueva entrada en equipo"); // Mensaje de éxito
            if (filasAfectadas > 0) { // Si se afectaron filas
                ResultSet rs = pstmt.getGeneratedKeys(); // Obtiene las claves generadas
                if (rs.next()) { // Si hay claves generadas
                    int id = rs.getInt(1); // Obtiene el ID del nuevo equipo
                    pstmt.close(); // Cierra la declaración
                    connection.closeConnection(); // Cierra la conexión
                    return id; // Devuelve el ID del nuevo equipo
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
     * Elimina un equipo de la base de datos.
     *
     * @param equipo El objeto Equipo a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean eliminar(Equipo equipo) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "DELETE FROM Equipo WHERE id_equipo = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, equipo.getId_equipo()); // Establece el ID del equipo a eliminar
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