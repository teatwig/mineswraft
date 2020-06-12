module net.teatwig.mineswraft {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens net.teatwig.mineswraft to javafx.fxml;
    exports net.teatwig.mineswraft;
}