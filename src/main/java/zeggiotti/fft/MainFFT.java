package zeggiotti.fft;

import javax.sound.sampled.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MainFFT {
    private static FastFourierTransform transform;
    private static final int FREQUENCY = 50;
    private static final int SAMPLING_FREQUENCY = 256;

    public static void main(String[] args) {

        double[] signal = new double[SAMPLING_FREQUENCY];
        transform = new FastFourierTransform();

        System.out.println("Generazione segnale...");
        int index = 0;
        for (double i = 0; i < 1; i += (double) 1 / SAMPLING_FREQUENCY)
            signal[index++] = 100 * Math.cos(2 * Math.PI * i * FREQUENCY);

        long start, end;
        Complex[] result;

        System.out.println("\nFFT:");
        start = System.currentTimeMillis();
        result = transform.FFT(signal);
        end = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (end - start) + "ms\n");

        generateOutput(result);

    }

    private static void generateOutput(Complex[] result) {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine targetLine;

        byte[] signal = new byte[65536];
        short[] data = new short[signal.length / 2];

        try {
            targetLine = (TargetDataLine) AudioSystem.getLine(info);
            targetLine.open();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        targetLine.start();

        AudioInputStream audioStream = new AudioInputStream(targetLine);
        FastFourierTransform transform = new FastFourierTransform();

        float binWidth = format.getSampleRate() / data.length;

        try {
            audioStream.read(signal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i < signal.length; i += 2) {
            data[i >> 1] = (short) (signal[i + 1] << 8 | signal[i]);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        result = transform.FFT(data);

        System.out.println("Generazione file data/freq.csv");
        printToFile(result);

        try {
            System.out.println("Esecuzione script plotter.r");
            Runtime.getRuntime().exec("C:\\Program Files\\R\\R-4.2.3\\bin\\Rscript data/plotter.r").waitFor();
            System.out.println("Generato file data/plot.png");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printToFile(Complex[] freq) {
        printToFile(transform.toMagnitudes(freq));
    }

    private static void printToFile(double[] magnitudes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/freq.csv"))) {

            writer.println("Frequency,Magnitude");
            for (int i = 1; i < magnitudes.length; i++)
                writer.println(i + "," + magnitudes[i]);

        } catch (IOException ignored) {
        }
    }

}