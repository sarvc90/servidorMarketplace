module com.servidor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.graphics;
    requires java.xml.bind;
    requires java.desktop;

    opens com.servidor to javafx.fxml;
    exports com.servidor;
}
