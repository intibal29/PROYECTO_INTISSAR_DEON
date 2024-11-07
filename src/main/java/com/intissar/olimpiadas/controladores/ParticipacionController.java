package com.intissar.olimpiadas.controladores;

import com.intissar.olimpiadas.dao.DaoDeportista;
import com.intissar.olimpiadas.dao.DaoEquipo;
import com.intissar.olimpiadas.dao.DaoEvento;
import com.intissar.olimpiadas.dao.DaoParticipacion;
import com.intissar.olimpiadas.model.Deportista;
import com.intissar.olimpiadas.model.Equipo;
import com.intissar.olimpiadas.model.Evento;
import com.intissar.olimpiadas.model.Participacion;
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
 * Controlador para la gestión de participaciones en eventos deportivos.
 * Permite crear y modificar participaciones, así como gestionar su visualización en la interfaz gráfica.
 */
public class ParticipacionController implements Initializable {
    private Participacion participacion; // Participación que se está editando o creando

    @FXML // fx:id="lstDeportista"
    private ListView<Deportista> lstDeportista; // Lista de deportistas disponibles

    @FXML // fx:id="lstEquipo"
    private ListView<Equipo> lstEquipo; // Lista de equipos disponibles

    @FXML // fx:id="lstEvento"
    private ListView<Evento> lstEvento; // Lista de eventos disponibles

    @FXML // fx:id="txtEdad"
    private TextField txtEdad; // Campo de texto para la edad del deportista

    @FXML // fx:id="txtMedalla"
    private TextField txtMedalla; // Campo de texto para la medalla obtenida

    @FXML
    private ResourceBundle resources; // Recursos de la interfaz

    /**
     * Constructor que recibe una participación para editar.
     *
     * @param participacion Participación a editar.
     */
    public ParticipacionController(Participacion participacion) {
        this.participacion = participacion;
    }

    /**
     * Constructor por defecto para crear una nueva participación.
     */
    public ParticipacionController() {
        this.participacion = null;
    }

    /**
     * Inicializa el controlador. Se llama al cargar el FXML.
     * Carga las listas de deportistas, equipos y eventos.
     *
     * @param url URL de la ventana
     * @param resourceBundle Recursos de la interfaz
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        cargarListas(); // Carga las listas de deportistas, equipos y eventos
        if (this.participacion != null) {
            // Carga los datos de la participación en los campos de texto y listas
            lstDeportista.getSelectionModel().select(participacion.getDeportista());
            lstDeportista.setDisable(true); // Desactiva la selección de deportista si se está editando
            lstEvento.getSelectionModel().select(participacion.getEvento());
            lstEvento.setDisable(true); // Desactiva la selección de evento si se está editando
            lstEquipo.getSelectionModel().select(participacion.getEquipo());
            txtEdad.setText(String.valueOf(participacion.getEdad())); // Muestra la edad
            txtMedalla.setText(participacion.getMedalla()); // Muestra la medalla
        }
    }

    /**
     * Carga las listas de deportistas, equipos y eventos desde la base de datos.
     */
    public void cargarListas() {
        ObservableList<Deportista> deportistas = DaoDeportista.cargarListado(); // Carga la lista de deportistas
        lstDeportista.getItems().addAll(deportistas); // Agrega los deportistas a la lista
        ObservableList<Evento> eventos = DaoEvento.cargarListado(); // Carga la lista de eventos
        lstEvento.getItems().addAll(eventos); // Agrega los eventos a la lista
        ObservableList<Equipo> equipos = DaoEquipo.cargarListado(); // Carga la lista de equipos
        lstEquipo.getItems().addAll(equipos); // Agrega los equipos a la lista
    }

    /**
     * Cierra la ventana actual cuando se cancela la acción.
     *
     * @param event Evento de acción
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) txtEdad.getScene().getWindow(); // Obtiene la ventana actual
        stage.close(); // Cierra la ventana
    }

    /**
     * Guarda una nueva participación o actualiza una existente.
     *
     * @param event Evento de acción
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar(); // Valida los datos ingresados
        if (!error.isEmpty()) {
            alerta(error); // Muestra mensaje de error si hay problemas de validación
        } else {
            Participacion nuevo = new Participacion(); // Crea un nuevo objeto Participacion
            nuevo.setDeportista(lstDeportista.getSelectionModel().getSelectedItem());
            nuevo.setEvento(lstEvento.getSelectionModel().getSelectedItem());
            nuevo.setEquipo(lstEquipo.getSelectionModel().getSelectedItem());
            nuevo.setEdad(Integer.parseInt(txtEdad.getText()));
            nuevo.setMedalla(txtMedalla.getText());
            if (this.participacion == null) { // Si no hay participación seleccionada, se crea una nueva
                if (DaoParticipacion.insertar(nuevo)) {
                    confirmacion(resources.getString("save.participation")); // Muestra mensaje de éxito
                    Stage stage = (Stage) txtEdad.getScene().getWindow();
                    stage.close(); // Cierra la ventana
                } else {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error si la inserción falla
                }
            } else { // Si hay una participación seleccionada, se actualiza
                if (DaoParticipacion.modificar(participacion, nuevo)) {
                    confirmacion(resources.getString("update.participation")); // Muestra mensaje de éxito
                    Stage stage = (Stage) txtEdad.getScene().getWindow();
                    stage.close(); // Cierra la ventana
                } else {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error si la actualización falla
                }
            }
        }
    }

    /**
     * Valida los datos ingresados en los campos de texto y listas.
     *
     * @return Mensajes de error si hay problemas de validación
     */
    public String validar() {
        StringBuilder error = new StringBuilder();
        if (lstDeportista.getSelectionModel().getSelectedItem() == null) {
            error.append(resources.getString("validate.participation.athlete")).append("\n");
        }
        if (lstEvento.getSelectionModel().getSelectedItem() == null) {
            error.append(resources.getString("validate.participation.event")).append("\n");
        }
        if (lstEquipo.getSelectionModel().getSelectedItem() == null) {
            error.append(resources.getString("validate.participation.team")).append("\n");
        }
        if (txtEdad.getText().isEmpty()) {
            error.append(resources.getString("validate.participation.age")).append("\n");
        } else {
            try {
                Integer.parseInt(txtEdad.getText()); // Intenta convertir la edad a un número
            } catch (NumberFormatException e) {
                error.append(resources.getString("validate.participation.age.num")).append("\n"); // Mensaje de error si no es un número
            }
        }
        if (txtMedalla.getText().isEmpty()) {
            error.append(resources.getString("validate.participation.medal")).append("\n"); // Valida que la medalla no esté vacía
        } else {
            if (txtMedalla.getText().length() > 6) {
                error.append(resources.getString("validate.participation.medal.num")).append("\n"); // Mensaje de error si la medalla es demasiado larga
            }
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