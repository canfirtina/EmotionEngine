package sensormanager.util;

/**
 * Created by Mustafa on 1.5.2015.
 */
public class Filter {

    private static final int NOTCH50 = 0;
    private static final int NOTCH60 = 1;
    private static final int BP1_50 = 2;

    private static final FilterCoefficients[] filters = {
            /**
             * 50 Hz notch filter
             */
            new FilterCoefficients(
                    new double[]{1.0, -1.21449348, 2.29780334, -1.17207163, 0.93138168},
                    new double[]{0.96508099, -1.19328255, 2.29902305, -1.19328255, 0.96508099}
            ),
            /**
             * 60 Hz notch filter
             */
            new FilterCoefficients(
                    new double[]{ 1.000000000000000e+000, -2.467782611297853e-001, 1.944171784691352e+000, -2.381583792217435e-001, 9.313816821269039e-001 },
                    new double[]{ 9.650809863447347e-001, -2.424683201757643e-001, 1.945391494128786e+000, -2.424683201757643e-001, 9.650809863447347e-001  }
            ),

            /**
             * Bandpass filter 1-50 Hz
             */
            new FilterCoefficients(
                    new double[]{ 2.001387256580675e-001, 0.0f, -4.002774513161350e-001, 0.0f, 2.001387256580675e-001 },
                    new double[]{ 1.0f, -2.355934631131582e+000, 1.941257088655214e+000, -7.847063755334187e-001, 1.999076052968340e-001  }
            ),

    };

    public static void filterNotch50(double[] data) {
        filterIIR(filters[NOTCH50].getB(),filters[NOTCH50].getA(),data);
    }

    public static void filterNotch60(double[] data) {
        filterIIR(filters[NOTCH60].getB(),filters[NOTCH60].getA(),data);
    }



    public static void bandpass1_50(double[] data) {
        filterIIR(filters[BP1_50].getB(),filters[BP1_50].getA(),data);
    }



    public static void filterIIR(double[] filt_b, double[] filt_a, double[] data) {
        int Nback = filt_b.length;
        double[] prev_y = new double[Nback];
        double[] prev_x = new double[Nback];

        //step through data points
        for (int i = 0; i < data.length; i++) {
            //shift the previous outputs
            for (int j = Nback-1; j > 0; j--) {
                prev_y[j] = prev_y[j-1];
                prev_x[j] = prev_x[j-1];
            }

            //add in the new point
            prev_x[0] = data[i];

            //compute the new data point
            double out = 0;
            for (int j = 0; j < Nback; j++) {
                out += filt_b[j]*prev_x[j];
                if (j > 0) {
                    out -= filt_a[j]*prev_y[j];
                }
            }

            //save output value
            prev_y[0] = out;
            data[i] = (float)out;
        }
    }

}
