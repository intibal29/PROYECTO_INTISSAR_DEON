package com.intissar.olimpiadas.controladores;

import com.intissar.olimpiadas.dao.DaoDeportista;
import com.intissar.olimpiadas.dao.DaoEvento;
import com.intissar.olimpiadas.dao.DaoParticipacion;
import com.intissar.olimpiadas.db.DBConnect;
import com.intissar.olimpiadas.language.LanguageSwitcher;
import com.intissar.olimpiadas.model.Deportista;
import com.intissar.olimpiadas.model.Evento;
import com.intissar.olimpiadas.model.Participacion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Clase que controla los eventos de la ventana principal
 */
public class MainController implements Initializable {
    @FXML // fx:id="btnEditar"
    private MenuItem btnEditar; // Value injected by FXMLLoader

    @FXML // fx:id="btnEliminar"
    private MenuItem btnEliminar; // Value injected by FXMLLoader

    @FXML // fx:id="cbTabla"
    private ComboBox<String> cbTabla; // Value injected by FXMLLoader

    @FXML // fx:id="filtroNombre"
    private TextField filtroNombre; // Value injected by FXMLLoader

    @FXML // fx:id="langEN"
    private RadioMenuItem langEN; // Value injected by FXMLLoader

    @FXML // fx:id="langES"
    private RadioMenuItem langES; // Value injected by FXMLLoader

    @FXML // fx:id="langEU"
    private RadioMenuItem langEU; // Value injected by FXMLLoader

    @FXML // fx:id="tabla"
    private TableView tabla; // Value injected by FXMLLoader

    @FXML // fx:id="tgIdioma"
    private ToggleGroup tgIdioma; // Value injected by FXMLLoader

    @FXML
    private ResourceBundle resources; // ResourceBundle injected automatically by FXML loader

    private ObservableList masterData = FXCollections.observableArrayList();
    private ObservableList filteredData = FXCollections.observableArrayList();

    /**
     * Función que se ejecuta cuando se inicia la ventana.
     *
     * @param url URL de la ubicación de los recursos
     * @param resourceBundle ResourceBundle para la localización
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;

        // Controlar acceso a la base de datos
        if (!iniciarBaseDeDatos()) {
            return; // Si hay un error, se cierra la aplicación
        }

        // Configurar idioma
        configurarIdioma();

        // Configurar ComboBox
        configurarComboBox();

        // Configurar tabla
        configurarTabla();

        // Carga inicial de deportistas
        cargarDeportistas();
    }

    /**
     * Intenta establecer la conexión a la base de datos.
     *
     * @return true si la conexión fue exitosa, false en caso contrario
     */
    private boolean iniciarBaseDeDatos() {
        try {
            new DBConnect();
            return true;
        } catch (SQLException e) {
            alerta(resources.getString("db.error"));
            Platform.exit(); // Cierra la aplicación
            return false;
        }
    }

    /**
     * Configura el idioma de la aplicación según la configuración actual.
     */
    private void configurarIdioma() {
        Locale locale = resources.getLocale();
        if (locale.equals(new Locale("es"))) {
            langES.setSelected(true);
        } else if (locale.equals(new Locale("en"))) {
            langEN.setSelected(true);
        } else {
            langEU.setSelected(true);
        }

        // Cambiar idioma al seleccionar un nuevo idioma
        tgIdioma.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            Locale newLocale = langES.isSelected() ? new Locale("es") :
                    langEN.isSelected() ? new Locale("en") : new Locale("eu");
            new LanguageSwitcher((Stage) tabla.getScene().getWindow()).switchLanguage(newLocale);
        });
    }

    /**
     * Configura el ComboBox para seleccionar la tabla a mostrar.
     */
    private void configurarComboBox() {
        cbTabla.getItems().addAll(resources.getString("cb.athletes"), resources.getString("cb.participations"), resources.getString("cb.events"));
        cbTabla.setValue(resources.getString("cb.athletes"));
        cbTabla.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals(resources.getString("cb.athletes"))) {
                cargarDeportistas();
            } else if (newValue.equals(resources.getString("cb.participations"))) {
                cargarParticipaciones();
            } else {
                cargarEventos();
            }
        });
    }

    /**
     * Configura la tabla y sus eventos.
     */
    private void configurarTabla() {
        // Event Listener para celdas de la tabla
        tabla.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            deshabilitarMenus(newValue == null);
        });

        // Context Menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem = new MenuItem(resources.getString("contextmenu.edit"));
        MenuItem borrarItem = new MenuItem(resources.getString("contextmenu.delete"));
        contextMenu.getItems().addAll(editarItem, borrarItem);
        editarItem.setOnAction(this::editar);
        borrarItem.setOnAction(this::eliminar);
        tabla.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    tabla.getSelectionModel().select(row.getItem());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

        // Event Listener para el filtro
        filtroNombre.setOnKeyTyped(keyEvent -> filtrar());

        // Doble-click para editar
        tabla.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editar(null);
            }
        });
    }

    /**
     * Función que filtra la tabla por nombre
     */
    public void filtrar() {
        String valor = filtroNombre.getText();
        if (valor != null) {
            valor = valor.toLowerCase();
            String item = cbTabla.getSelectionModel().getSelectedItem();
            if (item.equals(resources.getString("cb.athletes"))) {
                // Deportistas
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        Deportista deportista = (Deportista) obj;
                        String nombre = deportista.getNombre();
                        nombre = nombre.toLowerCase();
                        if (nombre.contains(valor)) {
                            filteredData.add(deportista);
                        }
                    }
                    tabla.setItems(filteredData);
                }
            } else {
                // Eventos
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        Evento evento = (Evento) obj;
                        String nombre = evento.getNombre();
                        nombre = nombre.toLowerCase();
                        if (nombre.contains(valor)) {
                            filteredData.add(evento);
                        }
                    }
                    tabla.setItems(filteredData);
                }
            }
        }
    }

    /**
     * Método que se ejecuta al presionar el botón "Añadir".
     * Abre una ventana para agregar un nuevo objeto en la tabla seleccionada.
     *
     * @param event Evento de acción
     */
    @FXML
    void aniadir(ActionEvent event) {
        String selectedItem = cbTabla.getSelectionModel().getSelectedItem();
        try {
            Window parentWindow = tabla.getScene().getWindow();
            String fxmlPath;
            String title;
            Object controller;

            // Determinar el tipo de objeto a añadir y configurar el controlador y la vista
            if (selectedItem.equals(resources.getString("cb.athletes"))) {
                fxmlPath = "/fxml/Deportista.fxml";
                title = resources.getString("window.add") + " " + resources.getString("window.athlete") + " - " + resources.getString("app.name");
                controller = new DeportistaController();
            } else if (selectedItem.equals(resources.getString("cb.participations"))) {
                fxmlPath = "/fxml/Participacion.fxml";
                title = resources.getString("window.add") + " " + resources.getString("window.participation") + " - " + resources.getString("app.name");
                controller = new ParticipacionController();
            } else {
                fxmlPath = "/fxml/Evento.fxml";
                title = resources.getString("window.add") + " " + resources.getString("window.event") + " - " + resources.getString("app.name");
                controller = new EventoController();
            }

            // Cargar la ventana correspondiente
            abrirVentana(fxmlPath, title, controller, parentWindow);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            alerta(resources.getString("message.window_open"));
        }
    }

    /**
     * Método auxiliar para abrir una nueva ventana.
     *
     * @param fxmlPath Ruta del archivo FXML
     * @param title Título de la ventana
     * @param controller Controlador para la nueva ventana
     * @param parentWindow Ventana padre
     * @throws IOException Si ocurre un error al cargar el FXML
     */
    private void abrirVentana(String fxmlPath, String title, Object controller, Window parentWindow) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath), resources);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Olimpiadas.png")));
        stage.setTitle(title);
        stage.initOwner(parentWindow);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        // Recargar los datos según el tipo de objeto añadido
        if (controller instanceof DeportistaController) {
            cargarDeportistas();
        } else if (controller instanceof ParticipacionController) {
            cargarParticipaciones();
        } else {
            cargarEventos();
        }
    }

    /**
     * Método que se ejecuta al seleccionar el menú "Deportes".
     * Abre la ventana correspondiente a la gestión de deportes.
     *
     * @param event Evento de acción
     */
    @FXML
    void deportes(ActionEvent event) {
        try {
            Window parentWindow = tabla.getScene().getWindow();
            abrirVentana("/fxml/Deportes.fxml", resources.getString("window.sports") + " - " + resources.getString("app.name"), parentWindow);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            alerta(resources.getString("message.window_open"));
        }
    }

    /**
     * Método auxiliar para abrir una nueva ventana.
     *
     * @param fxmlPath Ruta del archivo FXML
     * @param title Título de la ventana
     * @param parentWindow Ventana padre
     * @throws IOException Si ocurre un error al cargar el FXML
     */
    private void abrirVentana(String fxmlPath, String title, Window parentWindow) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath), resources);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/Olimpiadas.png")));
        stage.setTitle(title);
        stage.initOwner(parentWindow);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * Método que se ejecuta al seleccionar el menú "Editar...".
     * Abre una ventana para editar el objeto seleccionado en la tabla.
     *
     * @param event Evento de acción
     */
    @FXML
    void editar(ActionEvent event) {
        Object selectedItem = tabla.getSelectionModel().getSelectedItem(); // Obtener el objeto seleccionado
        if (selectedItem != null) {
            String selectedTable = cbTabla.getSelectionModel().getSelectedItem(); // Obtener el tipo de objeto de la tabla
            try {
                Window parentWindow = tabla.getScene().getWindow(); // Obtener la ventana padre

                // Determinar el tipo de objeto a editar y abrir la ventana correspondiente
                if (selectedTable.equals(resources.getString("cb.athletes"))) {
                    abrirVentanaDeportista((Deportista) selectedItem, parentWindow);
                } else if (selectedTable.equals(resources.getString("cb.participations"))) {
                    abrirVentanaParticipacion((Participacion) selectedItem, parentWindow);
                } else {
                    abrirVentanaEvento((Evento) selectedItem, parentWindow);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                alerta(resources.getString("message.window_open")); // Mostrar alerta en caso de error
            }
        }
    }

    /**
     * Método auxiliar para abrir la ventana de edición de un deportista.
     *
     * @param deportista Objeto deportista a editar
     * @param parentWindow Ventana padre
     * @throws IOException Si ocurre un error al cargar el FXML
     */
    private void abrirVentanaDeportista(Deportista deportista, Window parentWindow) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Deportista.fxml"), resources);
        DeportistaController controller = new DeportistaController(deportista);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = crearStage(scene, resources.getString("window.edit") + " " + resources.getString("window.athlete"));
        stage.initOwner(parentWindow);
        stage.showAndWait();
        cargarDeportistas(); // Recargar la lista de deportistas
    }

    /**
     * Método auxiliar para abrir la ventana de edición de una participación.
     *
     * @param participacion Objeto participación a editar
     * @param parentWindow Ventana padre
     * @throws IOException Si ocurre un error al cargar el FXML
     */
    private void abrirVentanaParticipacion(Participacion participacion, Window parentWindow) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Participacion.fxml"), resources);
        ParticipacionController controller = new ParticipacionController(participacion);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = crearStage(scene, resources.getString("window.edit") + " " + resources.getString("window.participation"));
        stage.initOwner(parentWindow);
        stage.showAndWait();
        cargarParticipaciones(); // Recargar la lista de participaciones
    }

    /**
     * Método auxiliar para abrir la ventana de edición de un evento.
     *
     * @param evento Objeto evento a editar
     * @param parentWindow Ventana padre
     * @throws IOException Si ocurre un error al cargar el FXML
     */
    private void abrirVentanaEvento(Evento evento, Window parentWindow) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Evento.fxml"), resources);
        EventoController controller = new EventoController(evento);
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = crearStage(scene, resources.getString("window.edit") + " " + resources.getString("window.event"));
        stage.initOwner(parentWindow);
        stage.showAndWait();
        cargarEventos(); // Recargar la lista de eventos
    }

    /**
     * Método auxiliar para crear y configurar una nueva ventana (Stage).
     *
     * @param scene Escena a mostrar en la ventana
     * @param title Título de la ventana
     * @return La ventana configurada
     */
    private Stage crearStage(Scene scene, String title) {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Olimpiadas.png"))));
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
    /**
     * Método que se ejecuta al seleccionar el menú "Eliminar...".
     * Elimina el objeto seleccionado en la tabla después de confirmar la acción.
     *
     * @param event Evento de acción
     */
    @FXML
    void eliminar(ActionEvent event) {
        Object selectedItem = tabla.getSelectionModel().getSelectedItem(); // Obtener el objeto seleccionado
        if (selectedItem != null) {
            String selectedTable = cbTabla.getSelectionModel().getSelectedItem(); // Obtener el tipo de objeto de la tabla

            // Determinar el tipo de objeto a eliminar y proceder con la eliminación
            if (selectedTable.equals(resources.getString("cb.athletes"))) {
                eliminarDeportista((Deportista) selectedItem);
            } else if (selectedTable.equals(resources.getString("cb.participations"))) {
                eliminarParticipacion((Participacion) selectedItem);
            } else {
                eliminarEvento((Evento) selectedItem);
            }
        }
    }

    /**
     * Método auxiliar para eliminar un deportista.
     *
     * @param deportista Objeto deportista a eliminar
     */
    private void eliminarDeportista(Deportista deportista) {
        if (DaoDeportista.esEliminable(deportista)) {
            if (confirmarEliminacion(resources.getString("delete.athlete.prompt"))) {
                if (DaoDeportista.eliminar(deportista)) {
                    cargarDeportistas(); // Recargar la lista de deportistas
                    confirmacion(resources.getString("delete.athlete.success"));
                } else {
                    alerta(resources.getString("delete.athlete.fail"));
                }
            }
        } else {
            alerta(resources.getString("delete.athlete.error"));
        }
    }

    /**
     * Método auxiliar para eliminar una participación.
     *
     * @param participacion Objeto participación a eliminar
     */
    private void eliminarParticipacion(Participacion participacion) {
        if (confirmarEliminacion(resources.getString("delete.participation.prompt"))) {
            if (DaoParticipacion.eliminar(participacion)) {
                cargarParticipaciones(); // Recargar la lista de participaciones
                confirmacion(resources.getString("delete.participation.success"));
            } else {
                alerta(resources.getString("delete.participation.fail"));
            }
        }
    }

    /**
     * Método auxiliar para eliminar un evento.
     *
     * @param evento Objeto evento a eliminar
     */
    private void eliminarEvento(Evento evento) {
        if (DaoEvento.esEliminable(evento)) {
            if (confirmarEliminacion(resources.getString("delete.event.prompt"))) {
                if (DaoEvento.eliminar(evento)) {
                    cargarEventos(); // Recargar la lista de eventos
                    confirmacion(resources.getString("delete.event.success"));
                } else {
                    alerta(resources.getString("delete.event.fail"));
                }
            }
        } else {
            alerta(resources.getString("delete.event.error"));
        }
    }

    /**
     * Método auxiliar para mostrar un diálogo de confirmación de eliminación.
     *
     * @param message Mensaje a mostrar en el diálogo
     * @return true si el usuario confirma la eliminación, false en caso contrario
     */
    private boolean confirmarEliminacion(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(tabla.getScene().getWindow());
        alert.setHeaderText(null);
        alert.setTitle(resources.getString("window.confirm"));
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK; // Retorna true si se confirma
    }

    /**
     * Método que se ejecuta al seleccionar el menú "Equipos".
     * Abre la ventana correspondiente a la gestión de equipos.
     *
     * @param event Evento de acción
     */
    @FXML
    void equipos(ActionEvent event) {
        abrirVentana("/fxml/Equipos.fxml", resources.getString("window.teams"));
    }

    /**
     * Método que se ejecuta al seleccionar el menú "Olimpiadas".
     * Abre la ventana correspondiente a la gestión de olimpiadas.
     *
     * @param event Evento de acción
     */
    @FXML
    void olimpiadas(ActionEvent event) {
        abrirVentana("/fxml/Olimpiadas.fxml", resources.getString("window.olympics"));
    }

    /**
     * Método auxiliar para abrir una nueva ventana.
     *
     * @param fxmlPath Ruta del archivo FXML
     * @param title Título de la ventana
     */
    private void abrirVentana(String fxmlPath, String title) {
        try {
            Window parentWindow = tabla.getScene().getWindow(); // Obtener la ventana padre
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath), resources);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Olimpiadas.png"))));
            stage.setTitle(title + " - " + resources.getString("app.name")); // Establecer el título de la ventana
            stage.initOwner(parentWindow);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait(); // Mostrar la ventana y esperar a que se cierre
        } catch (IOException e) {
            System.err.println(e.getMessage());
            alerta(resources.getString("message.window_open")); // Mostrar alerta en caso de error
        }
    }
    /**
     * Método que carga en la tabla las columnas y los datos de los deportistas.
     */
    private void cargarDeportistas() {
        // Limpiar la selección y los filtros
        limpiarTabla();
        filtroNombre.setDisable(false);

        // Definir y agregar columnas a la tabla
        agregarColumnasDeportistas();

        // Cargar los datos de los deportistas desde la base de datos
        ObservableList<Deportista> deportistas = DaoDeportista.cargarListado();
        masterData.setAll(deportistas); // Actualizar la lista maestra
        tabla.setItems(deportistas); // Establecer los elementos de la tabla
    }

    /**
     * Método que carga en la tabla las columnas y los datos de las participaciones.
     */
    private void cargarParticipaciones() {
        // Limpiar la selección y los filtros
        limpiarTabla();
        filtroNombre.setDisable(true);

        // Definir y agregar columnas a la tabla
        agregarColumnasParticipaciones();

        // Cargar los datos de las participaciones desde la base de datos
        ObservableList<Participacion> participaciones = DaoParticipacion.cargarListado();
        masterData.addAll(participaciones); // Actualizar la lista maestra
        tabla.setItems(participaciones); // Establecer los elementos de la tabla
    }

    /**
     * Método auxiliar para limpiar la tabla y sus filtros.
     */
    private void limpiarTabla() {
        tabla.getSelectionModel().clearSelection(); // Limpiar la selección actual
        filtroNombre.setText(null); // Reiniciar el filtro de nombre
        masterData.clear(); // Limpiar la lista maestra
        filteredData.clear(); // Limpiar la lista filtrada
        tabla.getItems().clear(); // Limpiar los elementos de la tabla
        tabla.getColumns().clear(); // Limpiar las columnas de la tabla
    }

    /**
     * Método auxiliar para agregar las columnas de deportistas a la tabla.
     */
    private void agregarColumnasDeportistas() {
        TableColumn<Deportista, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_deportista"));

        TableColumn<Deportista, String> colNombre = new TableColumn<>(resources.getString("table.athlete.name"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Deportista, Deportista.SexCategory> colSexo = new TableColumn<>(resources.getString("table.athlete.sex"));
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));

        TableColumn<Deportista, Integer> colPeso = new TableColumn<>(resources.getString("table.athlete.weight"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));

        TableColumn<Deportista, Integer> colAltura = new TableColumn<>(resources.getString("table.athlete.height"));
        colAltura.setCellValueFactory(new PropertyValueFactory<>("altura"));

        tabla.getColumns().addAll(colId, colNombre, colSexo, colPeso, colAltura); // Agregar columnas a la tabla
    }

    /**
     * Método auxiliar para agregar las columnas de participaciones a la tabla.
     */
    private void agregarColumnasParticipaciones() {
        TableColumn<Participacion, String> colDeportista = new TableColumn<>(resources.getString("table.participation.athlete"));
        colDeportista.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getDeportista().getNombre()));

        TableColumn<Participacion, String> colEvento = new TableColumn<>(resources.getString("table.participation.event"));
        colEvento.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getEvento().getNombre()));

        TableColumn<Participacion, String> colEquipo = new TableColumn<>(resources.getString("table.participation.team"));
        colEquipo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getEquipo().getNombre()));

        TableColumn<Participacion, Integer> colEdad = new TableColumn<>(resources.getString("table.participation.age"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        TableColumn<Participacion, String> colMedalla = new TableColumn<>(resources.getString("table.participation.medal"));
        colMedalla.setCellValueFactory(new PropertyValueFactory<>("medalla"));

        tabla.getColumns().addAll(colDeportista, colEvento, colEquipo, colEdad, colMedalla); // Agregar columnas a la tabla
    }

    /**
     * Método que carga en la tabla las columnas y los datos de los eventos.
     */
    private void cargarEventos() {
        // Limpiar la selección y los filtros
        limpiarTabla();
        filtroNombre.setDisable(false); // Habilitar el filtro de nombre

        // Definir y agregar columnas a la tabla
        agregarColumnasEventos();

        // Cargar los datos de los eventos desde la base de datos
        ObservableList<Evento> eventos = DaoEvento.cargarListado();
        masterData.setAll(eventos); // Actualizar la lista maestra
        tabla.setItems(eventos); // Establecer los elementos de la tabla
    }

    /**
     * Método auxiliar para agregar las columnas de eventos a la tabla.
     */
    private void agregarColumnasEventos() {
        TableColumn<Evento, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_evento"));

        TableColumn<Evento, String> colNombre = new TableColumn<>(resources.getString("table.event.name"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Evento, String> colOlimpiada = new TableColumn<>(resources.getString("table.event.olympic"));
        colOlimpiada.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getOlimpiada().getNombre()));

        TableColumn<Evento, String> colDeporte = new TableColumn<>(resources.getString("table.event.sport"));
        colDeporte.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getDeporte().getNombre()));

        tabla.getColumns().addAll(colId, colNombre, colOlimpiada, colDeporte); // Agregar columnas a la tabla
    }

    /**
     * Método que habilita o deshabilita los menús de edición.
     *
     * @param deshabilitado Indica si los menús deben estar deshabilitados
     */
    private void deshabilitarMenus(boolean deshabilitado) {
        btnEditar.setDisable(deshabilitado); // Deshabilitar el botón de editar
        btnEliminar.setDisable(deshabilitado); // Deshabilitar el botón de eliminar
    }


    /**
     * Método que muestra un mensaje de alerta al usuario.
     *
     * @param mensaje Contenido del mensaje de alerta
     */
    public void alerta(String mensaje) {
        // Crear una alerta de tipo ERROR
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null); // No mostrar encabezado
        alerta.setTitle("Error"); // Título de la alerta
        alerta.setContentText(mensaje); // Contenido del mensaje
        alerta.showAndWait(); // Mostrar la alerta y esperar a que el usuario la cierre
    }

    /**
     * Método que muestra un mensaje de confirmación al usuario.
     *
     * @param mensaje Contenido del mensaje de confirmación
     */
    public void confirmacion(String mensaje) {
        // Crear una alerta de tipo INFORMATION
        Alert confirmacion = new Alert(Alert.AlertType.INFORMATION);
        confirmacion.setHeaderText(null); // No mostrar encabezado
        confirmacion.setTitle("Información"); // Título de la confirmación
        confirmacion.setContentText(mensaje); // Contenido del mensaje
        confirmacion.showAndWait(); // Mostrar la confirmación y esperar a que el usuario la cierre
    }

}