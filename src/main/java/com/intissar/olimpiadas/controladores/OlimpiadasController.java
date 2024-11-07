package com.intissar.olimpiadas.controladores;

import com.intissar.olimpiadas.dao.DaoOlimpiada;
import com.intissar.olimpiadas.model.Olimpiada;
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
 * Controlador para la gestión de olimpiadas en la aplicación.
 * Permite crear, modificar y eliminar olimpiadas, así como gestionar su visualización en la interfaz gráfica.
 */
public class OlimpiadasController implements Initializable {
    private Olimpiada olimpiada; // Referencia a la olimpiada seleccionada
    private Olimpiada crear; // Referencia para crear una nueva olimpiada

    @FXML // fx:id="btnEliminar"
    private Button btnEliminar; // Botón para eliminar una olimpiada

    @FXML // fx:id="cbOlimpiada"
    private ComboBox<Olimpiada> cbOlimpiada; // ComboBox para seleccionar olimpiadas

    @FXML // fx:id="rbInvierno"
    private RadioButton rbInvierno; // RadioButton para seleccionar la temporada de invierno

    @FXML // fx:id="rbVerano"
    private RadioButton rbVerano; // RadioButton para seleccionar la temporada de verano

    @FXML // fx:id="tgTemporada"
    private ToggleGroup tgTemporada; // Grupo de radio buttons para la temporada

    @FXML // fx:id="txtAnio"
    private TextField txtAnio; // Campo de texto para el año de la olimpiada

    @FXML // fx:id="txtCiudad"
    private TextField txtCiudad; // Campo de texto para la ciudad de la olimpiada

    @FXML // fx:id="txtNombre"
    private TextField txtNombre; // Campo de texto para el nombre de la olimpiada

    @FXML // fx:id="lblDelete"
    private Label lblDelete; // Etiqueta para mostrar mensajes de error al eliminar

    @FXML
    private ResourceBundle resources; // Recursos de la interfaz

    /**
     * Inicializa el controlador. Se llama al cargar el FXML.
     * Carga las olimpiadas disponibles y configura el ComboBox.
     *
     * @param url URL de la ventana
     * @param resourceBundle Recursos de la interfaz
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        this.olimpiada = null;
        crear = new Olimpiada();
        crear.setId_olimpiada(0);
        crear.setNombre(resources.getString("cb.new")); // Opción para crear una nueva olimpiada
        cargarOlimpiadas(); // Carga las olimpiadas en el ComboBox
        // Listener para detectar cambios en la selección del ComboBox
        cbOlimpiada.getSelectionModel().selectedItemProperty().addListener(this::cambioOlimpiada);
    }

    /**
     * Carga las olimpiadas de la base de datos al ComboBox.
     */
    public void cargarOlimpiadas() {
        cbOlimpiada.getItems().clear(); // Limpia los elementos actuales del ComboBox
        cbOlimpiada.getItems().add(crear); // Agrega la opción para crear una nueva olimpiada
        ObservableList<Olimpiada> olimpiadas = DaoOlimpiada.cargarListado(); // Carga la lista de olimpiadas desde la base de datos
        cbOlimpiada.getItems().addAll(olimpiadas); // Agrega las olimpiadas al ComboBox
        cbOlimpiada.getSelectionModel().select(0); // Selecciona el primer elemento por defecto
    }

    /**
     * Listener del cambio del ComboBox.
     * Actualiza los campos de texto y el botón de eliminar según la olimpiada seleccionada.
     *
     * @param observable Observable que notifica el cambio
     * @param oldValue Valor anterior
     * @param newValue Nuevo valor seleccionado
     */
    public void cambioOlimpiada(ObservableValue<? extends Olimpiada> observable, Olimpiada oldValue, Olimpiada newValue) {
        if (newValue != null) {
            btnEliminar.setDisable(true); // Desactiva el botón de eliminar por defecto
            lblDelete.setVisible(false); // Oculta la etiqueta de error
            if (newValue.equals(crear)) {
                // Si se selecciona la opción de crear una nueva olimpiada
                olimpiada = null;
                txtNombre.clear ();
                txtAnio.clear();
                rbInvierno.setSelected(true);
                rbVerano.setSelected(false);
                txtCiudad.clear();
            } else {
                // Si se selecciona una olimpiada existente
                olimpiada = newValue;
                txtNombre.setText(olimpiada.getNombre());
                txtAnio.setText(String.valueOf(olimpiada.getAnio()));
                if (olimpiada.getTemporada().equals("Winter")) {
                    rbInvierno.setSelected(true);
                    rbVerano.setSelected(false);
                } else {
                    rbVerano.setSelected(true);
                    rbInvierno.setSelected(false);
                }
                txtCiudad.setText(olimpiada.getCiudad());
                btnEliminar.setDisable(!DaoOlimpiada.esEliminable(olimpiada)); // Habilita o deshabilita el botón de eliminar
                lblDelete.setVisible(!DaoOlimpiada.esEliminable(olimpiada)); // Muestra u oculta la etiqueta de error
            }
        }
    }

    /**
     * Cierra la ventana actual cuando se cancela la acción.
     *
     * @param event Evento de acción
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) txtNombre.getScene().getWindow(); // Obtiene la ventana actual
        stage.close(); // Cierra la ventana
    }

    /**
     * Elimina la olimpiada seleccionada después de confirmar la acción.
     *
     * @param event Evento de acción
     */
    @FXML
    void eliminar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(txtNombre.getScene().getWindow());
        alert.setHeaderText(null);
        alert.setTitle(resources.getString("window.confirm"));
        alert.setContentText(resources.getString("delete.olympics.prompt"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (DaoOlimpiada.eliminar(olimpiada)) {
                confirmacion(resources.getString("delete.olympics.success"));
                cargarOlimpiadas(); // Recarga las olimpiadas después de eliminar
            } else {
                alerta(resources.getString("delete.olympics.fail")); // Muestra mensaje de error si la eliminación falla
            }
        }
    }

    /**
     * Guarda una nueva olimpiada o actualiza una existente.
     *
     * @param event Evento de acción
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar(); // Valida los datos ingresados
        if (!error.isEmpty()) {
            alerta(error); // Muestra mensaje de error si hay problemas de validación
        } else {
            Olimpiada nuevo = new Olimpiada(); // Crea un nuevo objeto Olimpiada
            nuevo.setNombre(txtNombre.getText());
            nuevo.setAnio(Integer.parseInt(txtAnio.getText()));
            nuevo.setTemporada(Olimpiada.SeasonCategory.valueOf(rbInvierno.isSelected() ? "Winter" : "Summer"));
            nuevo.setCiudad(txtCiudad.getText());
            if (this.olimpiada == null) { // Si no hay olimpiada seleccionada, se crea una nueva
                int id = DaoOlimpiada.insertar(nuevo);
                if (id == -1) {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error si la inserción falla
                } else {
                    confirmacion(resources.getString("save.olympics")); // Muestra mensaje de éxito
                    cargarOlimpiadas(); // Recarga las olimpiadas
                }
            } else { // Si hay una olimpiada seleccionada, se actualiza
                if (DaoOlimpiada.modificar(this.olimpiada, nuevo)) {
                    confirmacion(resources.getString("update.olympics")); // Muestra mensaje de éxito
                    cargarOlimpiadas(); // Recarga las olimpiadas
                } else {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error si la actualización falla
                }
            }
        }
    }

    /**
     * Valida los datos ingresados en los campos de texto.
     *
     * @return Mensajes de error si hay problemas de validación
     */
    public String validar() {
        StringBuilder error = new StringBuilder();
        if (txtNombre.getText().isEmpty()) {
            error.append(resources.getString("validate.olympics.name")).append("\n");
        }
        if (txtAnio.getText().isEmpty()) {
            error.append(resources.getString("validate.olympics.year")).append("\n");
        } else {
            try {
                Integer.parseInt(txtAnio.getText()); // Intenta convertir el año a un número
            } catch (NumberFormatException e) {
                error.append(resources.getString("validate.olympics.year.num")).append("\n"); // Mensaje de error si no es un número
            }
        }
        if (txtCiudad.getText().isEmpty()) {
            error.append(resources.getString("validate.olympics.city")).append("\n"); // Valida que la ciudad no esté vacía
        }
        return error.toString(); // Devuelve los mensajes de error
    }

    /**
     * Muestra un mensaje de alerta al usuario.
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
     * Muestra un mensaje de confirmación al usuario.
     *
     * @param texto Contenido del mensaje
     */
    public void confirmacion(String texto) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION); // Crea un diálogo de información
        alerta.setHeaderText(null);
        alerta.setTitle("Info"); // Título del diálogo
        alerta.setContentText(texto); // Mensaje de confirmación
        alerta.showAndWait(); // Muestra el diálogo y espera la respuesta
    }
}