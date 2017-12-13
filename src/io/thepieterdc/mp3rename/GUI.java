package io.thepieterdc.mp3rename;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

class GUI extends VBox {
	private final SimpleIntegerProperty counterProperty;
	private final SimpleStringProperty fileNameProperty;
	private final int totalAmt;
	
	public GUI(final int amt) {
		this.totalAmt = amt;
		
		final Label counter = new Label("0/" + totalAmt);
		counter.setContentDisplay(ContentDisplay.CENTER);
		counter.setTextAlignment(TextAlignment.CENTER);
		counter.setTextOverrun(OverrunStyle.CLIP);
		counter.setPadding(new Insets(40, 0, 40, 0));
		
		Font counterFont = Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 32);
		counter.setFont(counterFont);
		
		final Label fileName = new Label("file_name.mp3");
		fileName.setFont(Font.font(14));
		fileName.setTextAlignment(TextAlignment.CENTER);
		
		final ProgressBar progressBar = new ProgressBar(0);
		progressBar.setPrefWidth(200);
		
		this.getChildren().addAll(counter, progressBar, fileName);
		
		this.setAlignment(Pos.CENTER);
		this.setMaxHeight(Double.MIN_VALUE);
		this.setMaxWidth(Double.MIN_VALUE);
		this.setMinHeight(Double.MIN_VALUE);
		this.setMinWidth(Double.MIN_VALUE);
		this.setPadding(new Insets(20, 20, 20, 20));
		
		this.counterProperty = new SimpleIntegerProperty(0);
		this.counterProperty.addListener((o, od, nw) -> {
			counter.setText(nw + "/" + totalAmt);
			progressBar.setProgress(nw.doubleValue() / this.totalAmt);
		});
		
		this.fileNameProperty = new SimpleStringProperty("");
		this.fileNameProperty.addListener((o, od, nw) -> fileName.setText(nw));
	}
	
	public SimpleIntegerProperty counterProperty() {
		return this.counterProperty;
	}
	
	public SimpleStringProperty fileNameProperty() {
		return this.fileNameProperty;
	}
}