package com.intissar.olimpiadas.dao;

import com.intissar.olimpiadas.db.DBConnect;
import com.intissar.olimpiadas.model.Deportista;
import com.intissar.olimpiadas.model.Equipo;
import com.intissar.olimpiadas.model.Evento;
import com.intissar.olimpiadas.model.Participacion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase donde se ejecuta las consultas para la tabla Participacion
 */
public class DaoParticipacion {
    /**
     * Metodo que carga los datos de la tabla Participacions y los devuelve para usarlos en un listado de participacions
     *
     * @return listado de participacions para cargar en un tableview
     */
    public static ObservableList<Participacion> cargarListado() {
        DBConnect connection;
        ObservableList<Participacion> participacions = FXCollections.observableArrayList();
        try{
            connection = new DBConnect();
            String consulta = "SELECT id_deportista,id_evento,id_equipo,edad,medalla FROM Participacion";
            PreparedStatement pstmt = connection.getConnection().prepareStatement(consulta);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id_deportista = rs.getInt("id_deportista");
                Deportista deportista = DaoDeportista.getDeportista(id_deportista);
                int id_evento = rs.getInt("id_evento");
                Evento evento = DaoEvento.getEvento(id_evento);
                int id_equipo = rs.getInt("id_equipo");
                Equipo equipo = DaoEquipo.getEquipo(id_equipo);
                int edad = rs.getInt("edad");
                String medalla = rs.getString("medalla");
                Participacion participacion = new Participacion(deportista,evento,equipo,edad,medalla);
                participacions.add(participacion);
            }
            rs.close();
            connection.closeConnection();
        }catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return participacions;
    }

    /**
     * Metodo que modifica los datos de un participacion en la BD
     *
     * @param participacion		Instancia del participacion con datos
     * @param participacionNuevo Nuevos datos del participacion a modificar
     * @return			true/false
     */
    public static boolean modificar(Participacion participacion, Participacion participacionNuevo) {
        DBConnect connection;
        PreparedStatement pstmt;
        try {
            connection = new DBConnect();
            String consulta = "UPDATE Participacion SET id_deportista = ?,id_evento = ?,id_equipo = ?,edad = ?,medalla = ? WHERE id_deportista = ? AND id_evento = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, participacionNuevo.getDeportista().getId_deportista());
            pstmt.setInt(2, participacionNuevo.getEvento().getId_evento());
            pstmt.setInt(3, participacionNuevo.getEquipo().getId_equipo());
            pstmt.setInt(4, participacionNuevo.getEdad());
            pstmt.setString(5, participacionNuevo.getMedalla());
            pstmt.setInt(6, participacion.getDeportista().getId_deportista());
            pstmt.setInt(7, participacion.getEvento().getId_evento());
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Actualizado participacion");
            pstmt.close();
            connection.closeConnection();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Metodo que CREA un nuevo participacion en la BD
     *
     * @param participacion		Instancia del modelo participacion con datos nuevos
     * @return			true/false
     */
    public static boolean insertar(Participacion participacion) {
        DBConnect connection;
        PreparedStatement pstmt;
        try {
            connection = new DBConnect();
            String consulta = "INSERT INTO Participacion (id_deportista,id_evento,id_equipo,edad,medalla) VALUES (?,?,?,?,?) ";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, participacion.getDeportista().getId_deportista());
            pstmt.setInt(2, participacion.getEvento().getId_evento());
            pstmt.setInt(3, participacion.getEquipo().getId_equipo());
            pstmt.setInt(4, participacion.getEdad());
            pstmt.setString(5, participacion.getMedalla());
            int filasAfectadas = pstmt.executeUpdate();
            System.out.println("Nueva entrada en participacion");
            return (filasAfectadas > 0);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un participacion en función del modelo Participacion que le hayamos pasado
     *
     * @param participacion Participacion a eliminar
     * @return a boolean
     */
    public static boolean eliminar(Participacion participacion) {
        DBConnect connection;
        PreparedStatement pstmt;
        try {
            connection = new DBConnect();
            String consulta = "DELETE FROM Participacion WHERE id_deportista = ? AND id_evento = ?";
            pstmt = connection.getConnection().prepareStatement(consulta);
            pstmt.setInt(1, participacion.getDeportista().getId_deportista());
            pstmt.setInt(2, participacion.getEvento().getId_evento());
            int filasAfectadas = pstmt.executeUpdate();
            pstmt.close();
            connection.closeConnection();
            System.out.println("Eliminado con éxito");
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }


}
