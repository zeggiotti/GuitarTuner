package zeggiotti.guitartuner;

import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import zeggiotti.fft.FastFourierTransform;

import javax.sound.sampled.*;
import java.io.IOException;

public class TunerRunnable implements Runnable {

    private final int SAMPLES = 32768;
    private final int THRESHOLD = 30;

    private final AudioFormat format;
    private final TargetDataLine targetLine;
    private final Rectangle rectangle;
    private final GaussianBlur effect;

    private final byte[] signal = new byte[SAMPLES * 2];
    private final short[] data;

    private final Note note;
    private volatile boolean searching = true;

    public final Text offset;

    private static final Tuner tuner = new Tuner();

    public TunerRunnable (Note note, AudioFormat format, TargetDataLine targetLine, Rectangle rectangle, Text offset) {
        this.format = format;
        this.targetLine = targetLine;
        this.offset = offset;
        this.rectangle = rectangle;
        this.effect = new GaussianBlur();
        this.rectangle.setEffect(this.effect);
        this.rectangle.setVisible(false);

        data = new short[signal.length / 2];
        this.note = note;
    }

    @Override
    public void run() {

        try {

            targetLine.open();

            targetLine.start();

            AudioInputStream audioStream = new AudioInputStream(targetLine);
            FastFourierTransform transform = new FastFourierTransform();

            int bytesRead;
            while (searching) {

                bytesRead = audioStream.read(signal);

                for (int i = 0; i < signal.length; i += 2) {
                    data[i >> 1] = (short) (signal[i + 1] << 8 | signal[i]);
                }

                double[] magnitudes = transform.toMagnitudes(transform.FFT(data));
                
                // 32 e 8000 sono le frequenze delle note C1 e B8.
                int maxFreq = getMaxFreq(magnitudes);

                // double freqMagnitude = transform.getFrequencyMagnitude(data, 500, SAMPLE_RATE);

                /*
                if (Math.abs(tuner.getFrequency(note) - maxFreq) < THRESHOLD)
                    System.out.println(note + ": " + (maxFreq - tuner.getFrequency(note)));
                else System.out.println(note + " Found: " + maxFreq + "Hz");
                */

                if(Math.abs(tuner.getFrequency(note) - maxFreq) < THRESHOLD){
                    rectangle.setVisible(true);
                    this.effect.setRadius(Math.abs(tuner.getFrequency(note) - maxFreq) * 20 / THRESHOLD);
                    offset.setText(String.valueOf(maxFreq - tuner.getFrequency(note)));
                }

            }

            targetLine.flush();
            targetLine.stop();
            targetLine.close();

        } catch (IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }

    }

    public void stopSearching() {
        searching = false;
    }

    private int getMaxFreq(double[] magnitudes) {
        int lowestFrequencyBin = (int) (32 * magnitudes.length / format.getSampleRate());
        int highestFrequencyBin = (int) (8000 * magnitudes.length / format.getSampleRate());
        int maxBin = 1;
        for (int i = lowestFrequencyBin; i <= highestFrequencyBin; i++) {
            if (magnitudes[i] > magnitudes[maxBin]) {
                maxBin = i;
            }
        }

        return (int) (maxBin * format.getSampleRate() / magnitudes.length);
    }

}
