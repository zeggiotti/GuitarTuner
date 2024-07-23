package zeggiotti.fft;

/**
 * Implementa la DFT e l'algoritmo di Cooley-Tukey per la FFT di un segnale discreto reale.
 * Costruita con lo scopo di calcolare lo spettro delle frequenze di un segnale audio.
 */

public class FastFourierTransform {

    /**
     * Implementazione della DFT di un segnale.
     *
     * @param data il segnale da trasformare
     * @return
     */
    public Complex[] DFT(double[] data) {

        Complex[] result = new Complex[data.length];

        for (int k = 0; k < result.length; k++) {
            result[k] = new Complex(0, 0);
            for (int n = 0; n < data.length; n++) {
                result[k].real += data[n] * Math.cos(2 * Math.PI * k * n / data.length);
                result[k].imaginary += data[n] * Math.sin(-2 * Math.PI * k * n / data.length);
            }
        }

        return result;

    }

    public Complex[] FFT(short[] data) {
        double[] d = new double[data.length];
        for (int k = 0; k < data.length; k++) {
            d[k] = (double) data[k];
        }
        return FFT(d);
    }

    /*
    Calcola il zero-padding sull'ingresso se non è potenza di 2. Sebbene funzioni, i risultati sono
    un'approssimazione del risultato corretto (meglio avere sempre ingressi potenze di 2).
     */
    public Complex[] FFT(double[] d) {
        double[] data;
        if (!isPowerOfTwo(d.length)) {
            data = new double[getNearestPowerOfTwo(d.length)];
            for (int i = 0; i < d.length; i++)
                data[i] = d[i];
            for (int i = d.length; i < data.length; i++)
                data[i] = 0;
        } else data = d;
        return recursiveFFT(data);
    }

    /**
     * Implementazione dell'algoritmo FFT di Cooley–Tukey.
     *
     * @param data il segnale da trasformare
     * @return lo spettro delle frequenze del segnale.
     */
    public Complex[] recursiveFFT(double[] data) {

        if (data.length <= 1) {
            return new Complex[]{new Complex(data[0], 0)};
        }

        Complex[] result = new Complex[data.length];

        Complex[] even = recursiveFFT(extractEven(data));
        Complex[] odd = recursiveFFT(extractOdd(data));

        for (int k = 0; k < data.length / 2; k++) {
            result[k] = new Complex(0, 0);
            result[k + data.length / 2] = new Complex(0, 0);

            double real = odd[k].real, img = odd[k].imaginary;
            double constReal = Math.cos(2 * Math.PI * k / data.length);
            double constImg = Math.sin(2 * Math.PI * k / data.length);
            odd[k].real = real * constReal + img * constImg;
            odd[k].imaginary = -real * constImg + img * constReal;

            result[k].real = even[k].real + odd[k].real;
            result[k].imaginary = even[k].imaginary + odd[k].imaginary;
            result[k + data.length / 2].real = even[k].real - odd[k].real;
            result[k + data.length / 2].imaginary = even[k].imaginary - odd[k].imaginary;
        }

        return result;

    }

    public void toDouble(Complex[] data, double[] real, double[] img) {
        for (int k = 0; k < data.length; k++) {
            real[k] = data[k].real;
            img[k] = data[k].imaginary;
        }
    }

    public double getFrequencyMagnitude(double[] data, int frequency, float sampleRate) {
        int k = (int) (data.length * frequency / sampleRate);

        if(k >= data.length)
            throw new IllegalArgumentException();

        Complex result = new Complex(0, 0);
        for (int n = 0; n < data.length; n++) {
            result.real += data[n] * Math.cos(2 * Math.PI * k * n / data.length);
            result.imaginary += data[n] * Math.sin(-2 * Math.PI * k * n / data.length);
        }

        return Math.sqrt(result.real * result.real + result.imaginary * result.imaginary);
    }

    public double getFrequencyMagnitude(short[] data, int frequency, float sampleRate) {
        double[] d = new double[data.length];
        for (int k = 0; k < data.length; k++) {
            d[k] = (double) data[k];
        }
        return getFrequencyMagnitude(d, frequency, sampleRate);
    }

    public double[] toMagnitudes(Complex[] data) {
        double[] result = new double[data.length];
        for (int k = 0; k < data.length; k++) {
            result[k] = Math.sqrt(data[k].real * data[k].real + data[k].imaginary * data[k].imaginary);
        }
        return result;
    }

    private double[] extractEven(double[] data) {
        double[] result = new double[data.length / 2];
        for (int i = 0; i < data.length; i += 2) {
            result[i / 2] = data[i];
        }
        return result;
    }

    private double[] extractOdd(double[] data) {
        double[] result = new double[data.length / 2];
        for (int i = 1; i < data.length; i += 2) {
            result[i / 2] = data[i];
        }
        return result;
    }

    private boolean isPowerOfTwo(int n) {
        double a = Math.log(n) / Math.log(2);
        return (int) (Math.ceil(a)) == (int) (Math.floor(a));
    }

    private int getNearestPowerOfTwo(int a) {
        return (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros(a - 1));
    }

}
