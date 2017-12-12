package io.thepieterdc.mp3rename;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;

public class Main {
	public static File rename(File file) throws Exception {
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

	public static void main(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			File file = new File(args[i]);
			try {
				File out = rename(file);
				System.out.println("[" + (i + 1) + "/" + args.length + "] " + file.getName() + " -> " + out.getName());
			} catch (Exception e) {
				System.err.println("[" + (i + 1) + "/" + args.length + "] FAILED " + file.getName() + " (" + e.getMessage() + ")");
			}
		}
	}
}
