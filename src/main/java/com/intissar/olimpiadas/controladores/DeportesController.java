package com.intissar.olimpiadas.controladores;

import com.intissar.olimpiadas.dao.DaoDeporte;
import com.intissar.olimpiadas.model.Deporte;
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
 * Controlador para la gestión de deportes en la aplicación de olimpiadas.
 * Permite crear, modificar y eliminar deportes, así como gestionar su visualización en la interfaz gráfica.
 */
public class DeportesController implements Initializable {
    private Deporte deporte; // Deporte seleccionado actualmente
    private Deporte crear; // Objeto para crear un nuevo deporte

    @FXML // Elemento de la interfaz para eliminar un deporte
    private Button btnEliminar;

    @FXML // ComboBox para seleccionar deportes
    private ComboBox<Deporte> cbDeporte;

    @FXML // Campo de texto para ingresar el nombre del deporte
    private TextField txtNombre;

    @FXML // Etiqueta para mostrar mensajes de error al eliminar
    private Label lblDelete;

    @FXML
    private ResourceBundle resources; // Recursos de la interfaz

    /**
     * Inicializa el controlador. Se llama al cargar el FXML.
     * Carga la lista de deportes y configura el ComboBox.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        this.deporte = null;
        crear = new Deporte();
        crear.setId_deporte(0);
        crear.setNombre(resources.getString("cb.new")); // Nombre para la opción de crear nuevo deporte
        cargarDeportes(); // Carga los deportes en el ComboBox
        // Listener para detectar cambios en la selección del ComboBox
        cbDeporte.getSelectionModel().selectedItemProperty().addListener(this::cambioDeporte);
    }

    /**
     * Carga la lista de deportes en el ComboBox.
     */
    public void cargarDeportes() {
        cbDeporte.getItems().clear(); // Limpia los elementos actuales del ComboBox
        cbDeporte.getItems().add(crear); // Agrega la opción para crear un nuevo deporte
        ObservableList<Deporte> deportes = DaoDeporte.cargarListado(); // Carga la lista de deportes desde la base de datos
        cbDeporte.getItems().addAll(deportes); // Agrega los deportes al ComboBox
        cbDeporte.getSelectionModel().select(0); // Selecciona el primer elemento por defecto
    }

    /**
     * Maneja el cambio de selección en el ComboBox de deportes.
     * Actualiza el campo de texto y el botón de eliminar según el deporte seleccionado.
     */
    public void cambioDeporte(ObservableValue<? extends Deporte> observable, Deporte oldValue, Deporte newValue) {
        if (newValue != null) {
            btnEliminar.setDisable(true); // Desactiva el botón de eliminar por defecto
            lblDelete.setVisible(false); // Oculta la etiqueta de error
            if (newValue.equals(crear)) {
                deporte = null; // No hay deporte seleccionado para editar
                txtNombre.setText(null); // Limpia el campo de texto
            } else {
                deporte = newValue; // Actualiza el deporte seleccionado
                txtNombre.setText(deporte.getNombre()); // Muestra el nombre del deporte en el campo de texto
                // Verifica si el deporte es eliminable
                if (DaoDeporte.esEliminable(deporte)) {
                    btnEliminar.setDisable(false); // Habilita el botón de eliminar si es eliminable
                } else {
                    lblDelete.setVisible(true); // Muestra la etiqueta de error si no es eliminable
                }
            }
        }
    }

    /**
     * Cierra la ventana actual cuando se cancela la acción.
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) txtNombre.getScene().getWindow(); // Obtiene la ventana actual
        stage.close(); // Cierra la ventana
    }

    /**
     * Elimina el deporte seleccionado después de confirmar la acción.
     */
    @FXML
    void eliminar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION); // Crea un diálogo de confirmación
        alert.initOwner(txtNombre.getScene().getWindow()); // Establece la ventana padre
        alert.setHeaderText(null);
        alert.setTitle(resources.getString("window.confirm")); // Título del diálogo
        alert.setContentText(resources.getString("delete.sports.prompt")); // Mensaje de confirmación
        Optional<ButtonType> result = alert .showAndWait(); // Muestra el diálogo y espera la respuesta del usuario
        if (result.get() == ButtonType.OK) { // Si el usuario confirma la eliminación
            if (DaoDeporte.eliminar(deporte)) { // Intenta eliminar el deporte
                confirmacion(resources.getString("delete.sports.success")); // Muestra mensaje de éxito
                cargarDeportes(); // Recarga la lista de deportes
            } else {
                alerta(resources.getString("delete.sports.fail")); // Muestra mensaje de error
            }
        }
    }

    /**
     * Guarda un nuevo deporte o actualiza uno existente.
     */
    @FXML
    void guardar(ActionEvent event) {
        if (txtNombre.getText().isEmpty()) { // Verifica si el campo de texto está vacío
            alerta(resources.getString("validate.sports.name")); // Muestra mensaje de validación
        } else {
            Deporte nuevo = new Deporte(); // Crea un nuevo objeto Deporte
            nuevo.setNombre(txtNombre.getText()); // Establece el nombre del deporte
            if (this.deporte == null) { // Si no hay deporte seleccionado, se crea uno nuevo
                int id = DaoDeporte.insertar(nuevo); // Intenta insertar el nuevo deporte
                if (id == -1) {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error
                } else {
                    confirmacion(resources.getString("save.sports")); // Muestra mensaje de éxito
                    cargarDeportes(); // Recarga la lista de deportes
                }
            } else { // Si hay un deporte seleccionado, se actualiza
                if (DaoDeporte.modificar(this.deporte, nuevo)) {
                    confirmacion(resources.getString("update.sports")); // Muestra mensaje de éxito
                    cargarDeportes(); // Recarga la lista de deportes
                } else {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error
                }
            }
        }
    }

    /**
     * Muestra un mensaje de alerta con el texto proporcionado.
     */
    public void alerta(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR); // Crea un diálogo de error
        alerta.setHeaderText(null);
        alerta.setTitle("ERROR"); // Título del diálogo
        alerta.setContentText(texto); // Mensaje de error
        alerta.showAndWait(); // Muestra el diálogo y espera la respuesta
    }

    /**
     * Muestra un mensaje de confirmación con el texto proporcionado.
     */
    public void confirmacion(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION); // Crea un diálogo de información
        alerta.setHeaderText(null);
        alerta.setTitle("INFO"); // Título del diálogo
        alerta.setContentText(texto); // Mensaje de confirmación
        alerta.showAndWait(); // Muestra el diálogo y espera la respuesta
    }
}