package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * A class computing the routes between the waypoints.
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class RouteBean {

    /**
     * The Maximum distance between each point of the
     */
    private static final int MAX_STEP_LENGTH = 5;

    /**
     * The minimum number of waypoints placed for a route to be computed.
     */
    private static final int MIN_WAYPOINTS = 2;

    private final RouteComputer routeComputer;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final Map<Pair, Route> computedRoute = new LinkedHashMap<>();
    public final ObservableList<Waypoint> waypoints;


    /**
     * The constructor. Initialization of the attributes.
     *
     * @param rc The element allowing us to calculate the best itinerary between two points.
     */
    public RouteBean(RouteComputer rc) {

        this.highlightedPosition = new SimpleDoubleProperty();
        this.route = new SimpleObjectProperty<>();
        this.elevationProfile = new SimpleObjectProperty<>();
        this.waypoints = FXCollections.observableArrayList();

        this.routeComputer = rc;

        waypoints.addListener((Observable o) -> computeRoute());

        route.addListener((p, oldS, newS) -> elevationProfile.set(route.get() == null ?
                null : ElevationProfileComputer.elevationProfile(route.get(), MAX_STEP_LENGTH))
        );
    }

    /**
     * This method gives a property of the elevation profile in ReadOnly.
     *
     * @return a ReadOnlyObjectProperty of the elevation profile.
     */
    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfile() {
        return elevationProfile;
    }

    /**
     * This method gives a property of the route in ReadOnly.
     *
     * @return a ReadOnlyObjectProperty of the route.
     */
    public ReadOnlyObjectProperty<Route> getRoute() {
        return route;
    }

    /**
     * This method gives a double property of the highlighted position.
     *
     * @return a DoubleProperty of the highlighted profile.
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * This method gives the value of the highlighted position.
     *
     * @return the double value of the highlighted position.
     */
    public double highlightedPosition() {
        return highlightedPosition.doubleValue();
    }

    /**
     * This method sets the value of the highlighted position.
     *
     * @param value The new value of the highlighted position.
     */
    public void setHighlightedPosition(double value) {
        highlightedPosition.set(value);
    }

    /**
     * This method computes the itinerary between each pair of following waypoints.
     */
    private void computeRoute() {
        if (waypoints.size() >= MIN_WAYPOINTS) {
            List<Route> listRoute = new ArrayList<>();
            for (int i = 1; i < waypoints.size(); i++) {
                Waypoint startWaypoint = waypoints.get(i - 1);
                Waypoint endWaypoint = waypoints.get(i);
                if (!(startWaypoint.closestNodeId() == endWaypoint.closestNodeId())) {
                    if (!computedRoute.containsKey(new Pair(startWaypoint, endWaypoint))) {
                        Route tempRoute = routeComputer.bestRouteBetween(startWaypoint.closestNodeId(),
                                endWaypoint.closestNodeId());
                        if (tempRoute == null) {
                            route.set(null);
                            return;
                        }
                        computedRoute.put(new Pair(startWaypoint, endWaypoint), tempRoute);
                        listRoute.add(tempRoute);
                    } else listRoute.add(computedRoute.get(new Pair(startWaypoint, endWaypoint)));
                }
            }
            route.set((!listRoute.isEmpty()) ? new MultiRoute(listRoute) : null);
        } else route.set(null);
    }

    /**
     * This method gives the index of a non-empty segment at a given position.
     *
     * @param position The position of the segment.
     * @return The segment at the position.
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).closestNodeId();
            int n2 = waypoints.get(i + 1).closestNodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * Record representing a pair of waypoints.
     */
    record Pair(Waypoint a, Waypoint b) {
    }
}