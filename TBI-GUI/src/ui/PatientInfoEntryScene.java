package ui;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import utils.Holder;
import utils.Patient;
import utils.PatientManagement;
import utils.Scan;

public class PatientInfoEntryScene {
	static boolean analyzeFailed = false;

	public static Scene initializeScene(StateManager manager) {
		BorderPane layout = new BorderPane();
		GridPane contentGrid = new GridPane();
		GridPane mainGrid;
		GridPane pointerGrid = new GridPane();
		TextField patFNameField = new TextField();
		TextField patLNameField = new TextField();
		TextField fileField = new TextField();
		TextField pictureField = new TextField();
		TextField notesField = new TextField();
		FileChooser pictureChooser = new FileChooser();
		FileChooser fileChooser = new FileChooser();
		DatePicker datePicker = new DatePicker();
		Button finishBtn = new Button("Finish");
		Button fileSelect = new Button();
		Button pictureSelect = new Button();
		HBox picGrid = new HBox();
		HBox fileGrid = new HBox();

		//Text fields set up and design
		patFNameField.setMaxSize(200, 10);
		patFNameField.setPromptText("Patient First Name");
		StateManager.textMaxLength(patFNameField, 50);

		patLNameField.setMaxSize(200, 10);
		patLNameField.setPromptText("Patient Last Name");
		StateManager.textMaxLength(patLNameField, 50);

		pictureField.setMaxSize(180, 10);
		pictureField.setEditable(false);
		pictureField.setPromptText("Select Picture");
		StateManager.textMaxLength(pictureField, 100);

		fileField.setMaxSize(180, 10);
		fileField.setEditable(false);
		fileField.setPromptText("Select File(s)");
		StateManager.textMaxLength(fileField, 100);

		notesField.setMaxSize(200, 10);
		notesField.setPromptText("Notes");
		StateManager.textMaxLength(notesField, 256);

		datePicker.setPromptText("Select Date of Scan(s)");

		//Add required indicator tags
		StackPane fNameStackPane = makeRequiredSVG();
		GridPane.setConstraints(fNameStackPane, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);
		pointerGrid.getChildren().add(fNameStackPane);

		StackPane lNameStackPane = makeRequiredSVG();
		GridPane.setConstraints(lNameStackPane, 0, 1, 1, 1, HPos.LEFT, VPos.CENTER);
		pointerGrid.getChildren().add(lNameStackPane);

		StackPane fileStackPane = makeRequiredSVG();
		GridPane.setConstraints(fileStackPane, 0, 3, 1, 1, HPos.LEFT, VPos.CENTER);
		pointerGrid.getChildren().add(fileStackPane);

		fNameStackPane.setVisible(false);
		lNameStackPane.setVisible(false);
		fileStackPane.setVisible(false);

		//Construct Grid
		contentGrid.setVgap(15);
		pointerGrid.setVgap(15);
		
		patFNameField.setMaxWidth(Double.MAX_VALUE);
		patLNameField.setMaxWidth(Double.MAX_VALUE);
		fileField.setMaxWidth(Double.MAX_VALUE);
		pictureField.setMaxWidth(Double.MAX_VALUE);
		notesField.setMaxWidth(Double.MAX_VALUE);
		datePicker.setMaxWidth(Double.MAX_VALUE);

		GridPane.setConstraints(patFNameField, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(patLNameField, 1, 3, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(picGrid, 1, 4, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(fileGrid, 1, 5, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);		
		GridPane.setConstraints(datePicker, 1, 6, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(notesField, 1, 7, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(finishBtn, 1, 8, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		GridPane.setConstraints(pointerGrid, 2, 2, 2, 4, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		contentGrid.getChildren().addAll(patFNameField, patLNameField, picGrid, fileGrid, datePicker, notesField, finishBtn, pointerGrid);

		RowConstraints rowCon = new RowConstraints();
		rowCon.setPercentHeight(100.0/11);
		contentGrid.getRowConstraints().add(rowCon);
		ColumnConstraints columnCon = new ColumnConstraints();
		columnCon.setPercentWidth(100.0/3);
		ColumnConstraints columnCon2 = new ColumnConstraints();
		contentGrid.getColumnConstraints().addAll(columnCon, columnCon, columnCon2);

		rowCon = new RowConstraints();
		rowCon.setPercentHeight(100.0);
		pointerGrid.getRowConstraints().addAll(rowCon, rowCon, rowCon, rowCon);
		columnCon = new ColumnConstraints();
		columnCon.setPercentWidth(100);
		pointerGrid.getColumnConstraints().add(columnCon);

		LinkedList<File> newFiles = new LinkedList<File>();
		Holder holder = new Holder();

		//Picture Chooser Setup
		pictureChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("IMAGE", "*.png", "*.jpg"));
		pictureSelect.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				File picture = pictureChooser.showOpenDialog(manager.getStage());
				if (picture != null) {
					pictureField.setText(picture.getName());
					holder.setFile(picture);
				}
			}			
		});

		//File Chooser Setup
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SCAN", "*.nii", "*.nifti", "*.png"));
		fileSelect.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				List<File> files = fileChooser.showOpenMultipleDialog(manager.getStage());
				if (files != null && files.size() > 0) {
					if (files.size() == 1) {
						fileField.setText(files.get(0).getName());
					}
					else {
						fileField.setText(files.size() + " files");
					}
					for (int i = 0; i < files.size(); ++i) {
						newFiles.add(files.get(i));
					}
				}
			}
		});
		
		ColumnConstraints fileSelecColumn = new ColumnConstraints();
		fileSelecColumn.setPercentWidth(100.0/5*4);
		picGrid.setAlignment(Pos.CENTER);
		HBox.setHgrow(pictureField, Priority.ALWAYS);
		fileGrid.setAlignment(Pos.CENTER);
		HBox.setHgrow(fileField, Priority.ALWAYS);

		Image iconImage = new Image("resources/icon.png", 15, 15, false, false);
		pictureSelect.setGraphic(new ImageView(iconImage));
		fileSelect.setGraphic(new ImageView(iconImage));
		
		picGrid.getChildren().addAll(pictureField, pictureSelect);
		fileGrid.getChildren().addAll(fileField, fileSelect);
		
		//Date Picker Setup
		datePicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				holder.setDate(java.sql.Date.valueOf(datePicker.getValue()));
			}
		});

		//Finish Button Setup
		finishBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		finishBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {

				boolean complete = true;
				if(patFNameField.getText().equals("")) {
					complete = false;
					fNameStackPane.setVisible(true);
				} else {
					fNameStackPane.setVisible(false);
				}	
				if(patLNameField.getText().equals("")) {
					complete = false;
					lNameStackPane.setVisible(true);
				} else {
					lNameStackPane.setVisible(false);
				}

				//If Date selected, a File must also be selected
				if (holder.getDate() != null && newFiles.size() == 0) {
					complete = false;
					fileStackPane.setVisible(true);
				} else {
					fileStackPane.setVisible(false);
				}

				//Switch to proper scene
				if(complete) {
					Date dateCreated = java.sql.Date.valueOf(LocalDate.now()); //get current date
					Patient patient = new Patient(patFNameField.getText(), patLNameField.getText(), dateCreated, notesField.getText());
					if (newFiles.size() > 0) {
						if (holder.getDate() == null) {
							manager.makeDialog("No date was selected for the scan(s). Today's date will be used.");
							holder.setDate(java.sql.Date.valueOf(LocalDate.now()));
						}
						for (int i = 0; i < newFiles.size(); ++i) {
							patient.addScan(new Scan(holder.getDate(), newFiles.get(i)));
						}
					}
					if (holder.getFile() != null) {
						patient.setPicture(holder.getFile());
					}
					try {
						PatientManagement.exportPatient(patient);
					} catch (IOException e) {
						manager.makeError("Creating new patient failed. There is an issue with the file structure of the database. \n"
								+ "Check utils.PatientManagement exportPatient().", e);
					}
					manager.getSceneStack().push(manager.getSceneID());
					manager.paintScene("PreviousPatient");
				}
			}

		});
		finishBtn.setTooltip(new Tooltip("Create a new patient in the system."));

		//Merge Vertical Side Menu and Content
		mainGrid = VerticalSideMenu.newSideBar(manager);
		GridPane.setConstraints(contentGrid, 1, 0, 1, 1, HPos.CENTER, VPos.CENTER);
		mainGrid.getChildren().add(contentGrid);
		layout.getStyleClass().add("content-pane");
		layout.setCenter(mainGrid);
		layout.setTop(TopMenuBar.newMenuBar(manager));

		//Return constructed scene
		return new Scene(layout, manager.getStage().getWidth(), manager.getStage().getHeight());
	}

	//Construct SVG image for pointing to empty elements
	public static StackPane makeRequiredSVG() {
		StackPane stackPane = new StackPane();
		SVGPath svg = new SVGPath();
		svg.setStroke(new Color(.949019607, .30980392157, .227450980392, 1));
		svg.setFill(new Color(.949019607, .30980392157, .227450980392, 1));
		svg.setContent("M200 5h-150v20h150l7-10z");
		svg.setRotate(180);
		svg.setStyle("-fx-background-color: #f24f3a");
		Label dialogLabel = new Label("This Field is Required");
		stackPane.getChildren().addAll(svg, dialogLabel);
		return stackPane;
	}
}