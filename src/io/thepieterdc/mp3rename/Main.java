package io.thepieterdc.mp3rename;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class Main extends Application {
	private static final String VERSION = "1.0.0";
	
	private static File doRename(File file) throws Exception {
		final Mp3File mp3File = new Mp3File(file);
		
		String artist;
		String title;
		
		if (mp3File.hasId3v1Tag()) {
			ID3v1 tag = mp3File.getId3v1Tag();
			artist = tag.getArtist();
			title = tag.getTitle();
		} else if (mp3File.hasId3v2Tag()) {
			ID3v2 tag = mp3File.getId3v2Tag();
			artist = tag.getArtist();
			title = tag.getTitle();
		} else {
			throw new UnsupportedTagException("Unreadable tag format.");
		}
		
		final String oldFileName = file.getAbsolutePath();
		final int fileIdx = oldFileName.lastIndexOf(file.getName());
		final String newFileName = oldFileName.substring(0, fileIdx) + artist + " - " + title + ".mp3";
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
		stage.setTitle("mp3rename v" + VERSION);
		stage.centerOnScreen();
		stage.show();
		
		final SimpleIntegerProperty counter = gui.counterProperty();
		final SimpleStringProperty filename = gui.fileNameProperty();
		
		for (File file : files) {
			counter.add(1);
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
