package com.intissar.olimpiadas.controladores;

import com.intissar.olimpiadas.dao.DaoEquipo;
import com.intissar.olimpiadas.model.Equipo;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Clase que controla los eventos de la ventana de gestión de equipos.
 * Permite crear, modificar y eliminar equipos, así como gestionar su visualización en la interfaz gráfica.
 */
public class EquiposController implements Initializable {
    private Equipo equipo; // Equipo seleccionado actualmente
    private Equipo crear; // Objeto para crear un nuevo equipo

    @FXML // fx:id="btnEliminar"
    private Button btnEliminar; // Botón para eliminar un equipo

    @FXML // fx:id="cbEquipo"
    private ComboBox<Equipo> cbEquipo; // ComboBox para seleccionar equipos

    @FXML // fx:id="txtIniciales"
    private TextField txtIniciales; // Campo de texto para las iniciales del equipo

    @FXML // fx:id="txtNombre"
    private TextField txtNombre; // Campo de texto para el nombre del equipo

    @FXML // fx:id="lblDelete"
    private Label lblDelete; // Etiqueta para mostrar mensajes de error al eliminar

    @FXML
    private ResourceBundle resources; // Recursos de la interfaz

    /**
     * Función que se ejecuta cuando se inicia la ventana.
     * Carga la lista de equipos y configura el ComboBox.
     *
     * @param url URL de la ventana
     * @param resourceBundle Recursos de la interfaz
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        this.equipo = null; // Inicializa el equipo como nulo
        crear = new Equipo(); // Crea un nuevo objeto Equipo
        crear.setId_equipo(0); // Establece un ID por defecto
        crear.setNombre(resources.getString("cb.new")); // Nombre para la opción de crear un nuevo equipo
        cargarEquipos(); // Carga los equipos en el ComboBox
        // Listener para detectar cambios en la selección del ComboBox
        cbEquipo.getSelectionModel().selectedItemProperty().addListener(this::cambioEquipo);
    }

    /**
     * Función que carga los equipos de la base de datos al ComboBox.
     */
    public void cargarEquipos() {
        cbEquipo.getItems().clear(); // Limpia los elementos actuales del ComboBox
        cbEquipo.getItems().add(crear); // Agrega la opción para crear un nuevo equipo
        ObservableList<Equipo> equipos = DaoEquipo.cargarListado(); // Carga la lista de equipos desde la base de datos
        cbEquipo.getItems().addAll(equipos); // Agrega los equipos al ComboBox
        cbEquipo.getSelectionModel().select(0); // Selecciona el primer elemento por defecto
    }

    /**
     * Listener del cambio del ComboBox.
     * Actualiza los campos de texto y el botón de eliminar según el equipo seleccionado.
     *
     * @param observable Observable que notifica el cambio
     * @param oldValue Valor anterior
     * @param newValue Nuevo valor seleccionado
     */
    public void cambioEquipo(ObservableValue<? extends Equipo> observable, Equipo oldValue, Equipo newValue) {
        if (newValue != null) {
            btnEliminar.setDisable(true); // Desactiva el botón de eliminar por defecto
            lblDelete.setVisible(false); // Oculta la etiqueta de error
            if (newValue.equals(crear)) {
                equipo = null; // No hay equipo seleccionado para editar
                txtNombre.setText(null); // Limpia el campo de texto
                txtIniciales.setText(null); // Limpia el campo de iniciales
            } else {
                equipo = newValue; // Actualiza el equipo seleccionado
                txtNombre.setText(equipo.getNombre()); // Muestra el nombre del equipo en el campo de texto
                txtIniciales.setText(equipo.getIniciales()); // Muestra las iniciales del equipo en el campo de texto
                if (DaoEquipo.esEliminable(equipo)) {
                    btnEliminar.setDisable(false); // Habilita el botón de eliminar si es eliminable
                } else {
                    lblDelete.setVisible(true); // Muestra la etiqueta de error si no es eliminable
                }
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Cancelar".
     * Cierra la ventana actual.
     *
     * @param event Evento de acción
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) txtNombre.getScene().getWindow(); // Obtiene la ventana actual
        stage.close(); // Cierra la ventana
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Eliminar".
     * Elimina el equipo seleccionado después de confirmar la acción.
     *
     * @param event Evento de acción
     */
    @FXML
    void eliminar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Crea un diálogo de confirmación
        alert.initOwner(txtNombre.getScene().getWindow()); // Establece la ventana principal como propietario
        alert.setHeaderText(null);
        alert.setTitle(resources.getString("window.confirm")); // Título del diálogo
        alert.setContentText(resources.getString("delete.teams.prompt")); // Mensaje de confirmación
        Optional<ButtonType> result = alert.showAndWait(); // Muestra el diálogo y espera la respuesta
        if (result.isPresent() && result.get() == ButtonType.OK) { // Si el usuario confirma
            if (DaoEquipo.eliminar(equipo)) { // Intenta eliminar el equipo
                confirmacion(resources.getString("delete.teams.success")); // Muestra mensaje de éxito
                cargarEquipos(); // Recarga la lista de equipos
            } else {
                alerta(resources.getString("delete.teams.fail")); // Muestra mensaje de error
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Guardar".
     * Valida y procesa los datos ingresados para crear o modificar un equipo.
     *
     * @param event Evento de acción
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = ""; // Inicializa la variable de error
        if (txtNombre.getText().isEmpty()) {
            error = resources.getString("validate.teams.name") + "\n"; // Valida el nombre del equipo
        }
        if (txtIniciales.getText().isEmpty()) {
            error += resources.getString("validate.teams.noc") + "\n"; // Valida las iniciales
        } else if (txtIniciales.getText().length() > 3) {
            error += resources.getString("validate.teams.noc.num") + "\n"; // Valida la longitud de las iniciales
        }
        if (!error.isEmpty()) {
            alerta(error); // Muestra mensaje de error si hay problemas de validación
        } else {
            Equipo nuevo = new Equipo(); // Crea un nuevo objeto Equipo
            nuevo.setNombre(txtNombre.getText()); // Establece el nombre del equipo
            nuevo.setIniciales(txtIniciales.getText()); // Establece las iniciales del equipo
            if (this.equipo == null) { // Si no hay equipo seleccionado, se crea uno nuevo
                int id = DaoEquipo.insertar(nuevo); // Intenta insertar el nuevo equipo
                if (id == -1) {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error
                } else {
                    confirmacion(resources.getString("save.teams")); // Muestra mensaje de éxito
                    cargarEquipos(); // Recarga la lista de equipos
                }
            } else { // Si hay un equipo seleccionado, se actualiza
                if (DaoEquipo.modificar(equipo, nuevo)) {
                    confirmacion(resources.getString("update.teams")); // Muestra mensaje de éxito
                    cargarEquipos(); // Recarga la lista de equipos
                } else {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error
                }
            }
        }
    }

    /**
     * Función que muestra un mensaje de alerta al usuario.
     *
     * @param texto Contenido de la alerta
     */
    public void alerta(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR); // Crea un diálogo de error
        alerta.setHeaderText(null);
        alerta.setTitle("Error"); // Título del diálogo
        alerta.setContentText(texto); // Mensaje de error
        alerta.showAndWait(); // Muestra el diálogo y espera la respuesta
    }

    /**
     * Función que muestra un mensaje de confirmación al usuario.
     *
     * @param texto Contenido del mensaje
     */
    public void confirmacion(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION); // Crea un diálogo de información
        alerta.setHeaderText(null);
        alerta.setTitle("Info"); // Título del diálogo
        alerta.setContentText(texto); // Mensaje de confirmación
        alerta.showAndWait(); // Muestra el diálogo ```java
        // y espera la respuesta
    }
}