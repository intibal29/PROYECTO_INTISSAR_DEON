package com.intissar.olimpiadas;

import com.intissar.olimpiadas.db.DBConnect;
import com.intissar.olimpiadas.language.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Clase donde se ejecuta la aplicación principal
 *
 * @author intissar
 */
public class OlimpiadasApplication extends Application {
    /**
     * {@inheritDoc}
     *
     * Función donde se carga y se muestra la ventana de la aplicación
     *
     * @param stage
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Establecer el idioma predeterminado (por ejemplo, español)
        String defaultLanguage = "es"; // Cambia esto según el idioma que desees usar por defecto
        LanguageManager languageManager = LanguageManager.getInstance(defaultLanguage);

        ResourceBundle bundle = languageManager.getBundle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(bundle.getString("app.name"));
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/Olimpiadas.png"))));
        stage.setScene(scene);
        stage.setMinWidth(600);
        stage.setMinHeight(350);
        stage.show();

        // Conectar a la base de datos
        try {
            DBConnect dbConnect = new DBConnect();
            Connection connection = dbConnect.getConnection();
            System.out.println("Conexión a la base de datos establecida con éxito.");

            // Puedes hacerlo en un método de cierre o en el evento de cierre de la ventana
            // dbConnect.closeConnection();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Función main donde se lanza la aplicación
     *
     * @param args parámetros por consola
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}