package sensormanager.util;

/**
 * Created by Mustafa on 1.5.2015.
 */
public class FilterCoefficients {
    private double[] a;
    private double[] b;

    public FilterCoefficients(double[] a, double[] b) {
        this.a = a.clone();
        this.b = b.clone();
    }

    public double[] getA() {
        return a;
    }

    public double[] getB() {
        return b;
    }
}
