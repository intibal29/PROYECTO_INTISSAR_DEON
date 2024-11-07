module com.example.proyectoolimpiadas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.proyectoolimpiadas to javafx.fxml;
    exports com.example.proyectoolimpiadas;
    exports com.example.proyectoolimpiadas.controller;
    opens com.example.proyectoolimpiadas.controller to javafx.fxml;
}