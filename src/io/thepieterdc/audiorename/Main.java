package io.thepieterdc.audiorename;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Main extends Application {
	private static final String VERSION = "1.1.1";
	
	private static File doRename(File file) throws Exception {
		final AudioFile audioFile = AudioFileIO.read(file);
		
		final Tag metaTags = audioFile.getTag();
		
		if (!metaTags.hasField(FieldKey.ARTIST)) {
			throw new Exception("Artist not found.");
		}
		String artist = metaTags.getFirst(FieldKey.ARTIST);
		
		if (!metaTags.hasField(FieldKey.TITLE)) {
			throw new Exception("Title not found.");
		}
		String title = metaTags.getFirst(FieldKey.TITLE);
		
		final String oldFileName = file.getAbsolutePath();
		final String extension = oldFileName.substring(oldFileName.lastIndexOf('.'));
		final int fileIdx = oldFileName.lastIndexOf(file.getName());
		final String newFileName = oldFileName.substring(0, fileIdx) + artist + " - " + title + extension;
		final File newFile = new File(newFileName);
		
		if (newFile.exists()) {
			throw new IOException("Output file already exists.");
		}
		
		if (!file.renameTo(newFile)) {
			throw new Exception("Error while renaming.");
		}
		
		return newFile;
	}
	
	private static Collection<File> getFiles(final Stage stage) {
		final FileChooser fs = new FileChooser();
		return fs.showOpenMultipleDialog(stage);
	}
	
	private static void headless(final String[] files) {
		int amountFiles = files.length;
		for (int i = 0; i < amountFiles; ++i) {
			File file = new File(files[i]);
			try {
				File out = doRename(file);
				System.out.println("[" + (i + 1) + "/" + amountFiles + "] " + file.getName() + " -> " + out.getName());
			} catch (Exception e) {
				System.err.println("[" + (i + 1) + "/" + amountFiles + "] FAILED " + file.getName() + " (" + e.getMessage() + ")");
			}
		}
		System.exit(0);
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			launch(args);
		} else {
			headless(args);
		}
	}
	
	@Override
	public void start(final Stage stage) {
		Collection<File> files = getFiles(stage);
		
		GUI gui = new GUI(files.size());
		
		final Scene scene = new Scene(gui);
		
		stage.setScene(scene);
		stage.setTitle("audiorename v" + VERSION);
		stage.centerOnScreen();
		stage.show();
		
		final SimpleIntegerProperty counter = gui.counterProperty();
		final SimpleStringProperty filename = gui.fileNameProperty();
		
		for (File file : files) {
			counter.setValue(counter.get() + 1);
			filename.set(file.getName());
			
			try {
				doRename(file);
			} catch (Exception e) {
				final Alert error = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
				Platform.runLater(error::showAndWait);
			}
		}
//		System.exit(0);
	}
}
