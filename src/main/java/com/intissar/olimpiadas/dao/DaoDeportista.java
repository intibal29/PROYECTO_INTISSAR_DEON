package com.intissar.olimpiadas.dao;

import com.intissar.olimpiadas.db.DBConnect;
import com.intissar.olimpiadas.model.Deportista;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

/**
 * Clase que maneja las operaciones de acceso a datos relacionadas con la tabla "Deportista".
 * Proporciona métodos para crear, leer, actualizar y eliminar deportistas en la base de datos.
 */
public class DaoDeportista {

    /**
     * Busca un deportista en la base de datos por su ID.
     *
     * @param id ID del deportista a buscar.
     * @return El objeto Deportista correspondiente o null si no se encuentra.
     */
    public static Deportista getDeportista(int id) {
        DBConnect connection; // Conexión a la base de datos
        Deportista deportista = null; // Inicializa la variable de deportista
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT id_deportista, nombre, sexo, peso, altura, foto FROM Deportista WHERE id_deportista = ?";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, id); // Establece el ID del deportista en la consulta
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta
            if (rs.next()) { // Si hay resultados
                int id_deportista = rs.getInt("id_deportista");
                String nombre = rs.getString("nombre");
                char sexo = rs.getString("sexo").charAt(0); // Obtiene el sexo como carácter
                int peso = rs.getInt("peso");
                int altura = rs.getInt("altura");
                Blob foto = rs.getBlob("foto"); // Obtiene la foto como Blob
                deportista = new Deportista(id_deportista, nombre, sexo, peso, altura, foto); // Crea un nuevo objeto Deportista
            }
            rs.close(); // Cierra el ResultSet
            connection.closeConnection(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
        }
        return deportista; // Devuelve el deportista encontrado o null
    }

    /**
     * Convierte un archivo de imagen en un objeto Blob.
     *
     * @param file Archivo de imagen a convertir.
     * @return El Blob correspondiente a la imagen.
     * @throws SQLException Si ocurre un error en la base de datos.
     * @throws IOException  Si ocurre un error al leer el archivo.
     */
    public static Blob convertFileToBlob(File file) throws SQLException, IOException {
        DBConnect connection = new DBConnect(); // Crea una nueva conexión a la base de datos
        // Abre una conexión a la base de datos
        try (Connection conn = connection.getConnection();
             FileInputStream inputStream = new FileInputStream(file)) {

            // Crea un Blob
            Blob blob = conn.createBlob();
            // Escribe los bytes del archivo en el Blob
            byte[] buffer = new byte[1024];
            int bytesRead;

            try (var outputStream = blob.setBinaryStream(1)) { // Crea un flujo de salida para el Blob
                while ((bytesRead = inputStream.read(buffer)) != -1) { // Lee el archivo en bloques
                    outputStream.write(buffer, 0, bytesRead); // Escribe en el Blob
                }
            }
            return blob; // Devuelve el Blob
        }
    }

    /**
     * Carga todos los deportistas de la base de datos y los devuelve en una lista observable.
     *
     * @return Lista observable de deportistas.
     */
    public static ObservableList<Deportista> cargarListado() {
        DBConnect connection; // Conexión a la base de datos
        ObservableList<Deportista> deportistas = FXCollections.observableArrayList(); // Lista observable para almacenar deportistas
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT id_deportista, nombre, sexo, peso, altura, foto FROM Deportista";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery(); // Ejecuta la consulta
            while (rs.next()) { // Itera a través de los resultados
                int id_deportista = rs.getInt("id_deportista");
                String nombre = rs.getString("nombre");
                char sexo = rs.getString("sexo").charAt( 0); // Obtiene el sexo como carácter
                int peso = rs.getInt("peso");
                int altura = rs.getInt("altura");
                Blob foto = rs.getBlob("foto"); // Obtiene la foto como Blob
                Deportista deportista = new Deportista(id_deportista, nombre, sexo, peso, altura, foto); // Crea un nuevo objeto Deportista
                deportistas.add(deportista); // Agrega el deportista a la lista
            }
            rs.close(); // Cierra el ResultSet
            connection.closeConnection(); // Cierra la conexión
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
        }
        return deportistas; // Devuelve la lista de deportistas
    }

    /**
     * Verifica si un deportista se puede eliminar de la base de datos.
     *
     * @param deportista El objeto Deportista a verificar.
     * @return true si se puede eliminar, false en caso contrario.
     */
    public static boolean esEliminable(Deportista deportista) {
        DBConnect connection; // Conexión a la base de datos
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "SELECT count(*) as cont FROM Participacion WHERE id_deportista = ?";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, deportista.getId_deportista()); // Establece el ID del deportista en la consulta
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
     * Modifica los datos de un deportista en la base de datos.
     *
     * @param deportista      Instancia del deportista con datos actuales.
     * @param deportistaNuevo Instancia del deportista con nuevos datos.
     * @return true si la modificación fue exitosa, false en caso contrario.
     */
    public static boolean modificar(Deportista deportista, Deportista deportistaNuevo) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "UPDATE Deportista SET nombre = ?, sexo = ?, peso = ?, altura = ?, foto = ? WHERE id_deportista = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setString(1, deportistaNuevo.getNombre()); // Establece el nuevo nombre del deportista
            pstmt.setString(2, deportistaNuevo.getSexo() + ""); // Establece el nuevo sexo del deportista
            pstmt.setInt(3, deportistaNuevo.getPeso()); // Establece el nuevo peso del deportista
            pstmt.setInt(4, deportistaNuevo.getAltura()); // Establece la nueva altura del deportista
            pstmt.setBlob(5, deportistaNuevo.getFoto()); // Establece la nueva foto del deportista
            pstmt.setInt(6, deportista.getId_deportista()); // Establece el ID del deportista a modificar
            int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la actualización
            System.out.println("Actualizado deportista"); // Mensaje de éxito
            pstmt.close(); // Cierra la declaración
            connection.closeConnection(); // Cierra la conexión
            return filasAfectadas > 0; // Devuelve true si se afectaron filas
        } catch (SQLException e) {
            System.err.println(e.getMessage()); // Manejo de errores
            return false; // Devuelve false si hubo un error
        }
    }

    /**
     * Inserta un nuevo deportista en la base de datos.
     *
     * @param deportista Instancia del modelo Deportista con datos nuevos.
     * @return ID del nuevo deportista o -1 si la inserción falla.
     */
    public static int insertar(Deportista deportista) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "INSERT INTO Deportista (nombre, sexo, peso, altura, foto) VALUES (?, ?, ?, ?, ?)";
            pstmt = connection.getConnection().prepareStatement(consulta, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, deportista.getNombre()); // Establece el nombre del nuevo deportista
            pstmt.setString(2, deportista.getSexo() + ""); // Establece el sexo del nuevo deportista
            pstmt.setInt(3, deportista.getPeso()); // Establece el peso del nuevo deportista
            pstmt.setInt(4, deportista.getAltura()); // Establece la altura del nuevo deportista
            pstmt.setBlob(5, deportista.getFoto()); // Establece la foto del nuevo deportista
            int filasAfectadas = pstmt.executeUpdate(); // Ejecuta la inserción
            System.out.println("Nueva entrada en deportista"); // Mensaje de éxito
            if (filasAfectadas > 0) { // Si se afectaron filas
                ResultSet rs = pstmt.getGeneratedKeys(); // Obtiene las claves generadas
                if (rs.next()) { // Si hay claves generadas
                    int id = rs.getInt(1); // Obtiene el ID del nuevo deportista
                    pstmt.close(); // Cierra la declaración
                    connection.closeConnection(); // Cierra la conexión
                    return id; // Devuelve el ID del nuevo deportista
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
     * Elimina un deportista de la base de datos.
     *
     * @param deportista El objeto Deportista a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public static boolean eliminar(Deportista deportista) {
        DBConnect connection; // Conexión a la base de datos
        PreparedStatement pstmt; // Declaración preparada para la consulta
        try {
            connection = new DBConnect(); // Crea una nueva conexión a la base de datos
            String consulta = "DELETE FROM Deportista WHERE id_deportista = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, deportista.getId_deportista()); // Establece el ID del deportista a eliminar
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