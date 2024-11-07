package com.intissar.olimpiadas.controladores;

import com.intissar.olimpiadas.dao.DaoDeportista;
import com.intissar.olimpiadas.model.Deportista;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controlador para la gestión de deportistas en la aplicación de olimpiadas.
 * Permite crear, modificar y eliminar deportistas, así como gestionar su visualización en la interfaz gráfica.
 */
public class DeportistaController implements Initializable {
    private Deportista deportista; // Deportista que se está editando o creando
    private Blob imagen; // Imagen del deportista

    @FXML
    private ImageView foto; // Vista de la imagen del deportista

    @FXML
    private RadioButton rbFemale; // RadioButton para seleccionar sexo femenino

    @FXML
    private RadioButton rbMale; // RadioButton para seleccionar sexo masculino

    @FXML
    private ToggleGroup tgSexo; // Grupo de radio buttons para el sexo

    @FXML
    private TextField txtAltura; // Campo de texto para la altura del deportista

    @FXML
    private TextField txtNombre; // Campo de texto para el nombre del deportista

    @FXML
    private TextField txtPeso; // Campo de texto para el peso del deportista

    @FXML
    private Button btnFotoBorrar; // Botón para borrar la foto del deportista

    @FXML
    private ResourceBundle resources; // Recursos de la interfaz

    /**
     * Constructor que recibe un deportista para editar.
     *
     * @param deportista Deportista a editar.
     */
    public DeportistaController(Deportista deportista) {
        this.deportista = deportista;
    }

    /**
     * Constructor por defecto para crear un nuevo deportista.
     */
    public DeportistaController() {
        this.deportista = null;
    }

    /**
     * Inicializa el controlador. Se llama al cargar el FXML.
     * Carga los datos del deportista si se está editando uno existente.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        this.imagen = null; // Inicializa la imagen como nula
        if (deportista != null) {
            // Carga los datos del deportista en los campos de texto
            txtNombre.setText(deportista.getNombre());
            if (deportista.getSexo() == 'F') {
                rbFemale.setSelected(true);
                rbMale.setSelected(false);
            } else {
                rbMale.setSelected(true);
                rbFemale.setSelected(false);
            }
            txtPeso.setText(String.valueOf(deportista.getPeso()));
            txtAltura.setText(String.valueOf(deportista.getAltura()));
            if (deportista.getFoto() != null) {
                this.imagen = deportista.getFoto(); // Carga la foto del deportista
                try {
                    InputStream imagenStream = deportista.getFoto().getBinaryStream();
                    foto.setImage(new Image(imagenStream)); // Muestra la imagen en la vista
                } catch (SQLException e) {
                    throw new RuntimeException(e); // Manejo de excepciones
                }
                btnFotoBorrar.setDisable(false); // Habilita el botón de borrar foto
            }
        }
    }

    /**
     * Borra la foto del deportista.
     */
    @FXML
    void borrarFoto(ActionEvent event) {
        imagen = null; // Establece la imagen como nula
        foto.setImage(new Image(getClass().getResourceAsStream("/images/deportista.png"))); // Muestra imagen por defecto
        btnFotoBorrar.setDisable(true); // Desactiva el botón de borrar foto
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
     * Guarda un nuevo deportista o actualiza uno existente.
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar(); // Valida los datos ingresados
        if (!error.isEmpty()) {
            alerta(error); // Muestra mensaje de error si hay problemas de validación
        } else {
            Deportista nuevo = new Deportista(); // Crea un nuevo objeto Deportista
            nuevo.setNombre(txtNombre.getText()); // Establece el nombre del deportista

            // Asignación del sexo usando el enum SexCategory
            nuevo.setSexo(rbFemale.isSelected() ? Deportista.SexCategory.FEMALE : Deportista.SexCategory.MALE);

            nuevo.setPeso(Integer.parseInt(txtPeso.getText())); // Establece el peso del deportista
            nuevo.setAltura(Integer.parseInt(txtAltura.getText())); // Establece la altura del deportista
            nuevo.setFoto(this.imagen); // Establece la foto del deportista

            if (this.deportista == null) { // Si no hay deportista seleccionado, se crea uno nuevo
                int id = DaoDeportista.insertar(nuevo); // Intenta insertar el nuevo deportista
                if (id == -1) {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error
                } else {
                    confirmacion(resources.getString("save.athlete")); // Muestra mensaje de éxito
                    Stage stage = (Stage) txtNombre.getScene().getWindow(); // Cierra la ventana
                    stage.close();
                }
            } else { // Si hay un deportista seleccionado, se actualiza
                if (DaoDeportista.modificar(this.deportista, nuevo)) {
                    confirmacion(resources.getString("update.athlete")); // Muestra mensaje de éxito
                    Stage stage = (Stage) txtNombre.getScene().getWindow(); // Cierra la ventana
                    stage.close();
                } else {
                    alerta(resources.getString("save.fail")); // Muestra mensaje de error
                }
            }
        }
    }

    /**
     * Valida los datos ingresados por el usuario.
     *
     * @return Un mensaje de error si hay problemas de validación, o una cadena vacía si todo es correcto.
     */
    private String validar() {
        StringBuilder error = new StringBuilder(); // Acumula los mensajes de error
        if (txtNombre.getText().isEmpty()) {
            error.append(resources.getString("validate.athlete.name")).append("\n"); // Valida el nombre
        }
        if (txtPeso.getText().isEmpty()) {
            error.append(resources.getString("validate.athlete.weight")).append("\n"); // Valida el peso
        } else {
            try {
                Integer.parseInt(txtPeso.getText()); // Verifica que el peso sea un número
            } catch (NumberFormatException e) {
                error.append(resources.getString("validate.athlete.weight.num")).append("\n"); // Mensaje de error si no es un número
            }
        }
        if (txtAltura.getText().isEmpty()) {
            error.append(resources.getString("validate.athlete.height")).append("\n"); // Valida la altura
        } else {
            try {
                Integer.parseInt(txtAltura.getText()); // Verifica que la altura sea un número
            } catch (NumberFormatException e) {
                error.append(resources.getString("validate.athlete.height.num")).append("\n"); // Mensaje de error si no es un número
            }
        }
        return error.toString(); // Devuelve los errores acumulados
    }

    /**
     * Permite al usuario seleccionar una imagen para el deportista.
     */
    @FXML
    void seleccionImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser(); // Crea un selector de archivos
        fileChooser.setTitle(resources.getString("athlete.photo.chooser")); // Título del selector
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")); // Filtros de extensión
        fileChooser.setInitialDirectory(new File(".")); // Establece el directorio inicial
        File file = fileChooser.showOpenDialog(null); // Muestra el selector y espera la selección
        if (file != null) {
            try {
                double kbs = (double) file.length() / 1024; // Calcula el tamaño del archivo en KB
                if (kbs > 64) {
                    alerta(resources.getString("athlete.photo.chooser.size")); // Mensaje de error si el archivo es demasiado grande
                } else {
                    InputStream imagenStream = new FileInputStream(file); // Crea un flujo de entrada para la imagen
                    Blob blob = DaoDeportista.convertFileToBlob(file); // Convierte el archivo a un Blob
                    this.imagen = blob; // Asigna el Blob a la variable de imagen
                    foto.setImage(new Image(imagenStream)); // Muestra la imagen seleccionada en la vista
                    btnFotoBorrar.setDisable(false); // Habilita el botón para borrar la foto
                }
            } catch (IOException | SQLException e) {
                alerta(resources.getString("athlete.photo.chooser.fail")); // Muestra mensaje de error si hay problemas al cargar la imagen
            }
        } else {
            System.out.println("Imagen no seleccionada"); // Mensaje en consola si no se selecciona ninguna imagen
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