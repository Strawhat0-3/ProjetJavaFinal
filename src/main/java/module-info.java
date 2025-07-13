module org.example.projetjavafinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.sql;  // Ajout pour JDBC

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires jakarta.persistence;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires jbcrypt;

    // Ouvertures pour JavaFX
    opens org.example.projetjavafinal to javafx.fxml;
    opens org.example.projetjavafinal.controller to javafx.fxml;

    // LIGNE CRUCIALE : Ouvrir le package model à Hibernate
    opens org.example.projetjavafinal.model to org.hibernate.orm.core, jakarta.persistence;

    // Si vous avez des services ou DAOs, ouvrez-les aussi
    opens org.example.projetjavafinal.service to org.hibernate.orm.core;
    opens org.example.projetjavafinal.dao to org.hibernate.orm.core;
    opens org.example.projetjavafinal.util to org.hibernate.orm.core;

    // Exports
    exports org.example.projetjavafinal;
    exports org.example.projetjavafinal.controller;
}