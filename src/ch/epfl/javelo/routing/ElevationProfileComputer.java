package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * A class intended to build an ElevationProfile
 *
 * @author Gaspard Thoral (345230)
 * @author Alexandre Mourot (346365)
 */
public final class ElevationProfileComputer {

    /**
     * A private constructor.
     */
    private ElevationProfileComputer() {
    }

    /**
     * This method allows us to build an ElevationProfile while interpolating the missing data.
     *
     * @param route         - Route : The route of which we compute the ElevationProfile.
     * @param maxStepLength - double : The gap between edges.
     * @return - ElevationProfile : The ElevationProfile associated to the given route.
     * @throws IllegalArgumentException (checkArgument) : Throws an exception if
     *                                  the gap between two edges is negative or null.
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);
        int nbSamples = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double length = route.length();
        double spaceBetween = length / (nbSamples - 1);
        float[] samples = new float[nbSamples];
        List<Integer> indexes = new ArrayList<>();


        for (int i = 0; i < nbSamples; i++) {
            samples[i] = ((float) route.elevationAt(i * spaceBetween));
        }
        if (firstValid(samples) == -1 || lastValid(samples) == -1) {
            return new ElevationProfile(length, new float[nbSamples]);
        }
        Arrays.fill(samples, 0, firstValid(samples), samples[firstValid(samples)]);
        Arrays.fill(samples, lastValid(samples), samples.length, samples[lastValid(samples)]);

        if (!Float.isNaN(samples[0]) && Float.isNaN(samples[1])) {
            indexes.add(0);
        }

        //Taking the indexes of values surrounding NaNs to facilitate interpolation.
        for (int i = 1; i < samples.length - 1; i++) {
            if (!Float.isNaN(samples[i]) && Float.isNaN(samples[i - 1]) && Float.isNaN(samples[i + 1])) {
                indexes.add(i);
                indexes.add(i);
            } else if (!Float.isNaN(samples[i]) && (Float.isNaN(samples[i - 1]) || Float.isNaN(samples[i + 1]))) {
                indexes.add(i);
            }
        }
        if (!Float.isNaN(samples[samples.length - 1]) && Float.isNaN(samples[samples.length - 2])) {
            indexes.add(samples.length - 1);
        }

        //Interpolating
        for (int i = 0; i <= (indexes.size() / 2) - 1; i++) {
            int quantity = indexes.get(2 * i + 1) - indexes.get(2 * i);
            DoubleUnaryOperator func = Functions.sampled(new float[]{samples[indexes.get(2 * i)], samples[indexes.get(2 * i + 1)]}, quantity);
            for (int j = 1; j < quantity; j++) {
                samples[indexes.get(2 * i) + j] = (float) func.applyAsDouble(j);
            }

        }
        return new ElevationProfile(length, samples);
    }

    /**
     * This method allows us to compute the first non-NaN value of an array of float.
     *
     * @param floatArray - float[] : The array we are looking through.
     * @return - int : The first valid index or -1 if it does not exist.
     */
    private static int firstValid(float[] floatArray) {
        for (int i = 0; i < floatArray.length; i++) {
            if (!Float.isNaN(floatArray[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * This method allows us to compute the last non-NaN value of an array of float.
     *
     * @param floatArray - float[] : The array we are looking through.
     * @return - int : The last valid index or -1 if it does not exist.
     */
    private static int lastValid(float[] floatArray) {
        for (int i = floatArray.length - 1; i >= 0; i--) {
            if (!Float.isNaN(floatArray[i])) {
                return i;
            }
        }
        return -1;
    }

}
