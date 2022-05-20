package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Spliterator;
import java.util.function.Consumer;



//TO BE REMOVED !
import javafx.scene.input.KeyCode;
//

public final class JaVelo extends Application {



    public static void main(String[] args) { launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));

        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";

        TileManager tileManager =
                new TileManager(cacheBasePath, tileServerHost);

        CityBikeCF costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer =
                new RouteComputer(graph, costFunction);

        RouteBean rb = new RouteBean(routeComputer);

        ErrorManager errorManager = new ErrorManager();

        AnnotatedMapManager amm = new AnnotatedMapManager(graph, tileManager, rb, errorManager::displayError);

        ElevationProfileManager profileBorderPane =
                new ElevationProfileManager((ObjectProperty<ElevationProfile>) rb.getElevationProfile(),
                rb.highlightedPositionProperty());

        DoubleProperty t = profileBorderPane.getHighlightProperty();
        t.bind(Bindings.when(
                        profileBorderPane.mousePositionOnProfileProperty().greaterThanOrEqualTo(0)).then(profileBorderPane.mousePositionOnProfileProperty()).otherwise(amm.mousePositionOnRouteProperty()));
        SplitPane.setResizableWithParent(profileBorderPane.pane(),false);

        SplitPane sp = new SplitPane(amm.pane());
        sp.setOrientation(Orientation.VERTICAL);

        rb.getElevationProfile().addListener((p, oldS, newS)-> {// pourquoi pas itinéraire
            if(oldS == null && newS != null) {

                sp.getItems().add(1, profileBorderPane.pane());


            }
            else if(oldS != null && newS == null){
                sp.getItems().remove(1);
            }
        });
        MenuItem menuItem = new MenuItem("Exporter GPX");
        menuItem.disableProperty().bind(rb.getRoute().isNull());
        menuItem.setOnAction(a -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", rb.getRoute().get(), rb.getElevationProfile().get());
            } catch (Exception e) {
                throw new UncheckedIOException(new IOException());
            }
        });
        Menu menu = new Menu("Fichier", null, menuItem);
        MenuBar menuBar = new MenuBar(menu);
        menuBar.setUseSystemMenuBar(true);

        Pane errorManagerPane = errorManager.pane();

        StackPane stackPane = new StackPane(sp, errorManagerPane);


        BorderPane finalPane = new BorderPane(stackPane);
        finalPane.setTop(menuBar);

        finalPane.getStylesheets().add("map.css");

        rb.highlightedPositionProperty().bind(Bindings.when(amm.mousePositionOnRouteProperty()
                .greaterThanOrEqualTo(0)).
                        then(amm.mousePositionOnRouteProperty()).
                        otherwise(profileBorderPane.mousePositionOnProfileProperty()));

        //TO BE REMOVED !
        Scene temp = new Scene(finalPane);
        //

        //TODO BE REMOVED !
        temp.setOnKeyPressed(k -> {
            if (k.getCode().equals(KeyCode.ENTER)) {
                rb.waypoints.clear();
            }
        });
        //
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(
                //TO BE REMOVED !
                temp
                //
        );
        primaryStage.setTitle("JaVelo");
        primaryStage.show();
    }
}
