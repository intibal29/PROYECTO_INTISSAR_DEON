module com.alesandro.olimpiadas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.intissar.olimpiadas to javafx.fxml;
    exports com.intissar.olimpiadas;
    exports com.intissar.olimpiadas.controladores;
    exports com.intissar.olimpiadas.model;
    exports com.intissar.olimpiadas.dao;
    opens com.intissar.olimpiadas.controladores to javafx.fxml;
    exports com.intissar.olimpiadas.language;
    opens com.intissar.olimpiadas.language to javafx.fxml;
}