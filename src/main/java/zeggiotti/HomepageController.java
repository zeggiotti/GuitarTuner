package zeggiotti;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import zeggiotti.guitartuner.Note;
import zeggiotti.guitartuner.TunerRunnable;

import javax.sound.sampled.*;
import java.io.*;

public class HomepageController {

    private AudioFormat format;
    private TargetDataLine targetLine;

    private final int SAMPLE_RATE = 44100;

    private final Note[] strings = new Note[6];
    private Thread tunerThread;
    private TunerRunnable tunerRunnable;

    private boolean setHidden = false;

    private Button searchButton = null;

    @FXML
    private AnchorPane firstStringPane, secondStringPane, thirdStringPane, fourthStringPane, fifthStringPane, sixthStringPane;

    @FXML
    private ComboBox<Note> firstStringBox, secondStringBox, thirdStringBox, fourthStringBox, fifthStringBox, sixthStringBox;

    @FXML
    private ImageView imageView;

    @FXML
    private Rectangle rectangle;

    @FXML
    private Text offsetText;

    @FXML
    private MenuItem openItem, saveItem;

    @FXML
    public void initialize() {

        format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        rectangle.setVisible(false);
        offsetText.setText("");

        try {
            targetLine = (TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        imageView.setImage(new Image("file:data/background.png"));

        strings[0] = Note.E4;
        strings[1] = Note.B3;
        strings[2] = Note.G3;
        strings[3] = Note.D3;
        strings[4] = Note.A2;
        strings[5] = Note.E2;

        for (Note note : Note.values()) {
            firstStringBox.getItems().add(note);
            secondStringBox.getItems().add(note);
            thirdStringBox.getItems().add(note);
            fourthStringBox.getItems().add(note);
            fifthStringBox.getItems().add(note);
            sixthStringBox.getItems().add(note);
        }

        firstStringBox.setOnAction(this::onSelectedNoteChanged);
        secondStringBox.setOnAction(this::onSelectedNoteChanged);
        thirdStringBox.setOnAction(this::onSelectedNoteChanged);
        fourthStringBox.setOnAction(this::onSelectedNoteChanged);
        fifthStringBox.setOnAction(this::onSelectedNoteChanged);
        sixthStringBox.setOnAction(this::onSelectedNoteChanged);

        firstStringBox.getSelectionModel().select(strings[0]);
        secondStringBox.getSelectionModel().select(strings[1]);
        thirdStringBox.getSelectionModel().select(strings[2]);
        fourthStringBox.getSelectionModel().select(strings[3]);
        fifthStringBox.getSelectionModel().select(strings[4]);
        sixthStringBox.getSelectionModel().select(strings[5]);

        firstStringPane.getChildren().add(new NoteButton(strings[0]));
        secondStringPane.getChildren().add(new NoteButton(strings[1]));
        thirdStringPane.getChildren().add(new NoteButton(strings[2]));
        fourthStringPane.getChildren().add(new NoteButton(strings[3]));
        fifthStringPane.getChildren().add(new NoteButton(strings[4]));
        sixthStringPane.getChildren().add(new NoteButton(strings[5]));

        ((NoteButton) firstStringPane.getChildren().get(1)).setOnAction(this::onStringBtnClicked);
        ((NoteButton) secondStringPane.getChildren().get(1)).setOnAction(this::onStringBtnClicked);
        ((NoteButton) thirdStringPane.getChildren().get(1)).setOnAction(this::onStringBtnClicked);
        ((NoteButton) fourthStringPane.getChildren().get(1)).setOnAction(this::onStringBtnClicked);
        ((NoteButton) fifthStringPane.getChildren().get(1)).setOnAction(this::onStringBtnClicked);
        ((NoteButton) sixthStringPane.getChildren().get(1)).setOnAction(this::onStringBtnClicked);

        AnchorPane.setRightAnchor((NoteButton) sixthStringPane.getChildren().get(1), 0d);
        AnchorPane.setRightAnchor((NoteButton) fifthStringPane.getChildren().get(1), 0d);
        AnchorPane.setRightAnchor((NoteButton) fourthStringPane.getChildren().get(1), 0d);

        saveItem.setOnAction(this::onSave);
        openItem.setOnAction(this::onOpen);

    }

    private void onSelectedNoteChanged(ActionEvent event) {
        ComboBox<Note> comboBox = (ComboBox<Note>) event.getSource();
        Note selectedNote = comboBox.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            ((NoteButton) ((AnchorPane) comboBox.getParent()).getChildren().get(1)).setNote(selectedNote);
        }
    }

    private void onStringBtnClicked(ActionEvent event) {
        if (!setHidden) {
            ((Stage) ((NoteButton) event.getSource()).getScene().getWindow()).setOnHidden(this::onClosing);
            setHidden = true;
        }

        if (tunerThread != null) {
            tunerRunnable.stopSearching();
            try {
                tunerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (searchButton != event.getSource()) {
            if (searchButton != null) {
                searchButton.getStyleClass().remove("searching");
                ((ComboBox<Note>) ((AnchorPane) searchButton.getParent()).getChildren().get(0)).setDisable(false);
            }
            searchButton = (Button) event.getSource();
            searchButton.getStyleClass().add("searching");
            ((ComboBox<Note>) ((AnchorPane) searchButton.getParent()).getChildren().get(0)).setDisable(true);

            Note note = ((NoteButton) event.getSource()).getNote();

            tunerRunnable = new TunerRunnable(note, format, targetLine, rectangle, offsetText);

            tunerThread = new Thread(tunerRunnable);
            tunerThread.start();
        } else {
            searchButton.getStyleClass().remove("searching");
            ((ComboBox<Note>) ((AnchorPane) searchButton.getParent()).getChildren().get(0)).setDisable(false);
            searchButton = null;
        }
    }

    private void onClosing(WindowEvent event) {
        if (tunerThread != null) {
            tunerRunnable.stopSearching();
            try {
                tunerThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void onSave(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Guitar tuning", "*.tng");
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialFileName("*.tng");
        File file = fileChooser.showSaveDialog(imageView.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {

                String result = "{";

                result += replaceSubscriptChar(sixthStringBox.getSelectionModel().getSelectedItem().toString()) + ",";
                result += replaceSubscriptChar(fifthStringBox.getSelectionModel().getSelectedItem().toString()) + ",";
                result += replaceSubscriptChar(fourthStringBox.getSelectionModel().getSelectedItem().toString()) + ",";
                result += replaceSubscriptChar(thirdStringBox.getSelectionModel().getSelectedItem().toString()) + ",";
                result += replaceSubscriptChar(secondStringBox.getSelectionModel().getSelectedItem().toString()) + ",";
                result += replaceSubscriptChar(firstStringBox.getSelectionModel().getSelectedItem().toString()) + "}";

                writer.println(result);


            } catch (FileNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore in apertura file.");
                alert.setHeaderText("Errore in scrittura del file scelto.");

                alert.showAndWait();
            }
        }
    }

    private void onOpen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Guitar tuning", "*.tng");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                String line = reader.readLine();
                if(line.charAt(0) != '{' || line.charAt(line.length() - 1) != '}' || reader.readLine() != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore in apertura file.");
                    alert.setHeaderText("Errore in lettura del file scelto.");

                    alert.showAndWait();
                    return;
                }

                line = line.substring(1, line.length() - 1);
                String[] data = line.split(",");

                sixthStringBox.getSelectionModel().select(Note.valueOf(data[0]));
                fifthStringBox.getSelectionModel().select(Note.valueOf(data[1]));
                fourthStringBox.getSelectionModel().select(Note.valueOf(data[2]));
                thirdStringBox.getSelectionModel().select(Note.valueOf(data[3]));
                secondStringBox.getSelectionModel().select(Note.valueOf(data[4]));
                firstStringBox.getSelectionModel().select(Note.valueOf(data[5]));

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore in apertura file.");
                alert.setHeaderText("Errore in lettura del file scelto.");

                alert.showAndWait();
            }
        }
    }

    private static class NoteButton extends Button {
        private Note note;

        public NoteButton(Note note) {
            super();
            this.note = note;
            super.setText(note.toString());
            this.setPrefWidth(40);
            this.setPrefHeight(40);
        }

        public Note getNote() {
            return note;
        }

        public void setNote(Note note) {
            this.note = note;
            super.setText(note.toString());
        }
    }

    private String replaceSubscriptChar(String text) {
        if(text.length() == 3){
            if(text.charAt(1) == '#')
                text = text.charAt(0) + "S" + text.charAt(2);
            else text = text.charAt(0) + "B" + text.charAt(2);
        }
        String result = text.substring(0, text.length() - 1);
        return switch (text.charAt(text.length() - 1)) {
            case '\u2081' -> result + "1";
            case '\u2082' -> result + "2";
            case '\u2083' -> result + "3";
            case '\u2084' -> result + "4";
            case '\u2085' -> result + "5";
            case '\u2086' -> result + "6";
            case '\u2087' -> result + "7";
            case '\u2088' -> result + "8";
            case '\u2089' -> result + "9";
            default -> text;
        };
    }

}