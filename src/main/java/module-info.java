module org.example.projetjavafinal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens org.example.projetjavafinal to javafx.fxml;
    exports org.example.projetjavafinal;
}