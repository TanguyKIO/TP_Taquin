module com.example.tp_taquin {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.tp_taquin to javafx.fxml;
    exports com.example.tp_taquin;
    exports com.example.tp_taquin.model;
    opens com.example.tp_taquin.model to javafx.fxml;
}