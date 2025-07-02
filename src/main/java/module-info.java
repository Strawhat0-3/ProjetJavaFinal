module org.example.projetjavafinal {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires jakarta.persistence;
    requires static lombok;
    requires org.hibernate.orm.core;

    opens org.example.projetjavafinal to javafx.fxml;
    exports org.example.projetjavafinal;
}