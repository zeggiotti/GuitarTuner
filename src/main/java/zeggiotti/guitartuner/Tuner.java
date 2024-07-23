package zeggiotti.guitartuner;

import java.util.HashMap;
import java.util.Map;

public class Tuner {

    private final Map<Note, Double> notes = new HashMap<>();

    // https://www.seventhstring.com/resources/notefrequencies.html
    public Tuner() {
        notes.put(Note.C2, 65.41);
        notes.put(Note.CS2, 69.30);
        notes.put(Note.D2, 73.42);
        notes.put(Note.EB2, 77.78);
        notes.put(Note.E2, 82.41);
        notes.put(Note.F2, 87.31);
        notes.put(Note.FS2, 92.5);
        notes.put(Note.G2, 98.0);
        notes.put(Note.GS2, 103.8);
        notes.put(Note.A2, 110.0);
        notes.put(Note.BB2, 116.5);
        notes.put(Note.B2, 123.5);

        notes.put(Note.C3, 130.8);
        notes.put(Note.CS3, 138.6);
        notes.put(Note.D3, 146.8);
        notes.put(Note.EB3, 155.6);
        notes.put(Note.E3, 164.8);
        notes.put(Note.F3, 174.6);
        notes.put(Note.FS3, 185.0);
        notes.put(Note.G3, 196.0);
        notes.put(Note.GS3, 207.7);
        notes.put(Note.A3, 220.0);
        notes.put(Note.BB3, 233.1);
        notes.put(Note.B3, 246.9);

        notes.put(Note.C4, 261.6);
        notes.put(Note.CS4, 277.2);
        notes.put(Note.D4, 293.7);
        notes.put(Note.EB4, 311.1);
        notes.put(Note.E4, 329.6);
        notes.put(Note.F4, 349.2);
        notes.put(Note.FS4, 370.0);
        notes.put(Note.G4, 392.0);
        notes.put(Note.GS4, 415.3);
        notes.put(Note.A4, 440.0);
        notes.put(Note.BB4, 466.2);
        notes.put(Note.B4, 493.9);

    }

    public double getFrequency(Note note) {
        return notes.get(note);
    }

    public Note getNearestNote(double frequency) {
        Note nearest = Note.C2;
        for(Note note : notes.keySet()) {
            if(Math.abs(notes.get(nearest) - frequency) > Math.abs(notes.get(note) - frequency))
                nearest = note;
        }
        return nearest;
    }

}
