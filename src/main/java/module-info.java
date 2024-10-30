module com.servidor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.xml.bind;
    requires java.desktop;
    requires transitive javafx.graphics;

    opens com.servidor to javafx.fxml;
    exports com.servidor;
    exports com.servidor.modelo; // Asegúrate de exportar este paquete
    exports com.servidor.util;
}
