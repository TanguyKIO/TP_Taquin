module viewController {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens viewController to javafx.fxml;
    exports viewController;

}