module org.example.projetjavafinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires jakarta.persistence;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires jbcrypt;

    opens org.example.projetjavafinal to javafx.fxml;
    opens org.example.projetjavafinal.controller to javafx.fxml;  // Ajouter cette ligne

    exports org.example.projetjavafinal;
    exports org.example.projetjavafinal.controller;  // Ajouter cette ligne
}