module com.yunhang.forum {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;

    // Data / utils
    requires com.google.gson;
    requires jakarta.mail;
    requires java.desktop;

    // Optional standard modules (uncomment if used)
    // requires java.sql;

    // Export our API packages
    exports com.yunhang.forum;
    exports com.yunhang.forum.model.entity;
    exports com.yunhang.forum.model.enums;
    exports com.yunhang.forum.model.session;
    exports com.yunhang.forum.service.strategy;
    exports com.yunhang.forum.util;

    // Open controller and fxml-accessed packages for reflection (FXML loader)
    opens com.yunhang.forum.controller.auth to javafx.fxml;
    opens com.yunhang.forum.model.entity to javafx.fxml, com.google.gson;
    opens com.yunhang.forum.model.enums to javafx.fxml;
    opens com.yunhang.forum.controller.post to javafx.fxml;
    opens com.yunhang.forum.controller.main to javafx.fxml;
}
