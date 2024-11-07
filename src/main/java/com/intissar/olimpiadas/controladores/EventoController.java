package com.intissar.olimpiadas.controladores;

import com.intissar.olimpiadas.dao.DaoDeporte;
import com.intissar.olimpiadas.dao.DaoEvento;
import com.intissar.olimpiadas.dao.DaoOlimpiada;
import com.intissar.olimpiadas.model.Deporte;
import com.intissar.olimpiadas.model.Evento;
import com.intissar.olimpiadas.model.Olimpiada;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Clase que controla los eventos de la ventana de gestión de eventos.
 * Permite crear y modificar eventos, así como gestionar su visualización en la interfaz gráfica.
 */
public class EventoController implements Initializable {
    private Evento evento; // Evento que se está editando o creando

    @FXML // fx:id="lstDeporte"
    private ListView<Deporte> lstDeporte; // Lista de deportes disponibles

    @FXML // fx:id="lstOlimpiada"
    private ListView<Olimpiada> lstOlimpiada; // Lista de olimpiadas disponibles

    @FXML // fx:id="txtNombre"
    private TextField txtNombre; // Campo de texto para el nombre del evento

    @FXML
    private ResourceBundle resources; // Recursos de la interfaz

    /**
     * Constructor que recibe un evento para editar.
     *
     * @param evento Evento a editar.
     */
    public EventoController(Evento evento) {
        this.evento = evento;
    }

    /**
     * Constructor por defecto para crear un nuevo evento.
     */
    public EventoController() {
        this.evento = null;
    }

    /**
     * Inicializa el controlador. Se llama al cargar el FXML.
     * Carga las listas de olimpiadas y deportes.
     *
     * @param url URL de la ventana
     * @param resourceBundle Recursos de la interfaz
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        cargarListas(); // Carga las listas de olimpiadas y deportes
        if (this.evento != null) {
            // Carga los datos del evento en los campos de texto y listas
            txtNombre.setText(evento.getNombre());
            lstOlimpiada.getSelectionModel().select(evento.getOlimpiada());
            lstDeporte.getSelectionModel().select(evento.getDeporte());
        }
    }

    /**
     * Función que carga las listas de olimpiadas y deportes.
     */
    public void cargarListas() {
        ObservableList<Olimpiada> olimpiadas = DaoOlimpiada.cargarListado(); // Carga la lista de olimpiadas desde la base de datos
        lstOlimpiada.getItems().addAll(olimpiadas); // Agrega las olimpiadas a la lista
        ObservableList<Deporte> deportes = DaoDeporte.cargarListado(); // Carga la lista de deportes desde la base de datos
        lstDeporte.getItems().addAll(deportes); // Agrega los deportes a la lista
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
     * Guarda un nuevo evento o actualiza uno existente.
     *
     * @param event Evento de acción
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = ""; // Inicializa la variable de error
        // Valida los datos ingresados
        if (txtNombre.getText().isEmpty()) {
            error = resources.getString("validate.event.name") + "\n"; // Valida el nombre del evento
        }
        if (lstOlimpiada.getSelectionModel().getSelectedItem() == null) {
            error += resources.getString("validate.event.olympic") + "\n"; // Valida la selección de olimpiada
        }
        if (lstDeporte.getSelectionModel().getSelectedItem() == null) {
            error += resources.getString("validate.event.sport") + "\n"; // Valida la selección de deporte
        }
        if (!error.isEmpty()) {
            alerta(error); // Muestra mensaje de error si hay problemas de validación
        } else {
            Evento nuevo = new Evento(); // Crea un nuevo objeto Evento
            nuevo.setNombre(txtNombre.getText()); // Establece el nombre del evento
            nuevo.setOlimpiada(lstOlimpiada.getSelectionModel().getSelectedItem()); // Establece la olimpiada seleccionada
            nuevo.setDeporte(lstDeporte.getSelectionModel().getSelectedItem()); // Establece el deporte seleccionado
            if (this.evento == null) { // Si no hay evento seleccionado, se crea uno nuevo
                int id = DaoEvento.insertar(nuevo); // Intenta insertar el nuevo evento
                if (id == -1) {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error si la inserción falla
                } else {
                    confirmacion(resources.getString("save.events")); // Muestra mensaje de éxito
                    Stage stage = (Stage) txtNombre.getScene().getWindow(); // Obtiene la ventana actual
                    stage.close(); // Cierra la ventana
                }
            } else { // Si hay un evento seleccionado, se actualiza
                if (DaoEvento.modificar(evento, nuevo)) {
                    confirmacion(resources.getString("update.events")); // Muestra mensaje de éxito
                    Stage stage = (Stage) txtNombre.getScene().getWindow(); // Obtiene la ventana actual
                    stage.close(); // Cierra la ventana
                } else {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error si la actualización falla
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
        alerta.showAndWait(); // Muestra el diálogo y espera la respuesta
    }
}