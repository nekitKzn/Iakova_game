module com.nekitvp.iakova {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.nekitvp.iakova to javafx.fxml;
    exports com.nekitvp.iakova;
}