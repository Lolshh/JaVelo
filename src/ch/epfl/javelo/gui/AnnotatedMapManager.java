package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

import static ch.epfl.javelo.Math2.norm;
import static java.lang.Double.NaN;


/**
 * This class creates the annotated map.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class AnnotatedMapManager {

    /**
     * The initial MapViewParameter zoom level.
     */
    private final static int INITIAL_ZOOM_LEVEL = 12;
    /**
     * The initial MapViewParameter x coordinate.
     */
    private final static int INITIAL_X_VALUE = 543200;
    /**
     * The initial MapViewParameter y coordinate.
     */
    private final static int INITIAL_Y_VALUE = 370650;

    /**
     * The Minimum pixel distance at which we look for the closest point from the mouse on the route.
     */
    private final static int MIN_PIXEL_DISTANCE = 15;

    /**
     * The initial map view parameter.
     */
    private final ObjectProperty<MapViewParameters> mapViewParam =
            new SimpleObjectProperty<>(new MapViewParameters(INITIAL_ZOOM_LEVEL, INITIAL_X_VALUE, INITIAL_Y_VALUE));

    private final DoubleProperty mousePositionOnRouteProperty = new SimpleDoubleProperty();
    private final ObjectProperty<Point2D> mousePositionPoint2D = new SimpleObjectProperty<>();

    private final Pane pane;
    private final RouteBean bean;

    /**
     * Constructor creating the pane and handlers and initializing the attributes.
     *
     * @param graph the graph.
     * @param tiles the tile manager.
     * @param bean  the route.
     * @param cons  the error consumer.
     */
    public AnnotatedMapManager(Graph graph, TileManager tiles, RouteBean bean, Consumer<String> cons) {
        RouteManager routeManager = new RouteManager(bean, mapViewParam);
        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParam, bean.waypoints, cons);
        BaseMapManager baseMapManager = new BaseMapManager(tiles, waypointsManager, mapViewParam);
        this.pane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        this.bean = bean;
        createHandler();
    }

    /**
     * This handler allows us to bind the property of the mouse position on the route. It allows to set it
     * depending on the mouse's position on the pane.
     */
    private void createHandler() {
        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(
                () -> {
                    if (bean.getRoute().get() != null && mousePositionPoint2D.get() != null) {
                        PointCh pointActual = mapViewParam.get().pointAt(mousePositionPoint2D.get().getX(),
                                mousePositionPoint2D.get().getY()).toPointCh();
                        if (pointActual == null) return NaN;
                        RoutePoint closestPoint = bean.getRoute().get().
                                pointClosestTo(pointActual);
                        PointWebMercator tempPWM = PointWebMercator.ofPointCh(closestPoint.point());
                        double tempNorm = norm(
                                mousePositionPoint2D.get().getX() - mapViewParam.get().viewX(tempPWM),
                                mousePositionPoint2D.get().getY() - mapViewParam.get().viewY(tempPWM)
                        );
                        if (tempNorm <= MIN_PIXEL_DISTANCE) return closestPoint.position();
                        else return NaN;
                    } else return NaN;
                },
                mapViewParam, bean.getRoute(), mousePositionPoint2D));

        //Action to perform when mouse is moved on the pane.
        pane.setOnMouseMoved(event -> mousePositionPoint2D.set(new Point2D(event.getX(), event.getY())));
        //Action to perform when mouse leaves the pane.
        pane.setOnMouseExited(event -> mousePositionPoint2D.set(null));
        //Action to perform when mouse is dragged.
        pane.setOnMouseDragged(event -> mousePositionPoint2D.set(null));
    }

    /**
     * This method returns the pane.
     *
     * @return the pane.
     */
    public Pane pane() {
        return pane;
    }

    /**
     * This method returns the property of the mouse position on the route.
     *
     * @return the property containing the mouse position.
     */
    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }
}
