package com.intissar.olimpiadas;

import com.intissar.olimpiadas.db.DBConnect; // Asegúrate de importar la clase DBConnect
import com.intissar.olimpiadas.language.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Clase principal de la aplicación que extiende la clase Application de JavaFX.
 * Esta clase es responsable de iniciar la aplicación y cargar la interfaz de usuario.
 *
 * @author intissar
 */
public class OlimpiadasApplication extends Application {

    /**
     * Método que se llama al iniciar la aplicación.
     * Carga la ventana principal y establece la conexión a la base de datos.
     *
     * @param stage El escenario principal de la aplicación.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Establecer el idioma predeterminado (por ejemplo, español)
        String defaultLanguage = "es"; // Cambia esto según el idioma que desees usar por defecto
        LanguageManager languageManager = LanguageManager.getInstance(defaultLanguage);

        // Cargar el ResourceBundle para la localización
        ResourceBundle bundle = languageManager.getBundle();

        // Cargar el archivo FXML y establecer el bundle de recursos
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());

        // Configurar el título y el icono de la ventana
        stage.setTitle(bundle.getString("app.name"));
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Olimpiadas.png"))));

        // Establecer la escena y las dimensiones mínimas de la ventana
        stage.setScene(scene);
        stage.setMinWidth(550);
        stage.setMinHeight(300);
        stage.show(); // Mostrar la ventana

        // Conectar a la base de datos
        try {
            DBConnect dbConnect = new DBConnect(); // Crear una nueva instancia de conexión a la base de datos
            Connection connection = dbConnect.getConnection(); // Obtener la conexión
            System.out.println("Conexión a la base de datos establecida con éxito.");

            // dbConnect.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage()); // Manejo de errores de conexión
        }
    }

    /**
     * Método principal que lanza la aplicación.
     *
     * @param args Parámetros de línea de comandos.
     */
    public static void main(String[] args) {
        Application.launch(args); // Iniciar la aplicación JavaFX
    }
}