module org.se13 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.json;

    opens org.se13 to javafx.fxml;
    exports org.se13;
    exports org.se13.view.base;
    exports org.se13.view.lifecycle;
    exports org.se13.view.ranking;
    exports org.se13.view.difficulty;
    opens org.se13.view.difficulty to javafx.fxml;
    opens org.se13.view.ranking to javafx.fxml;
    exports org.se13.view.setting;
    opens org.se13.view.setting to javafx.fxml;
    exports org.se13.view.start;
    opens org.se13.view.start to javafx.fxml;
    exports org.se13.view.tetris;
    opens org.se13.view.tetris to javafx.fxml;
    exports org.se13.view.nav;
    opens org.se13.view.nav to javafx.fxml;
}