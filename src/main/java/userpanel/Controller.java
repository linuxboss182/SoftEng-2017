package userpanel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import entities.Node;


public class Controller implements Initializable
{
	@FXML
	private Button logAsAdmin;
	@FXML
	private ImageView imageViewMap;
	@FXML
	public VBox contentVBox;
	@FXML
	public HBox contentHBox;
	@FXML
	public AnchorPane contentAnchor = new AnchorPane();
	@FXML
	private Slider floorSlider;
	@FXML
	private RadioButton elevatorRadio;
	@FXML
	private RadioButton stairRadio;

	Image map4;
	entities.Node clickNode;
	ArrayList<Node> alon = new ArrayList<Node>();


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Simulate populating the nodes list from database with random nodes
		for (int i = 0; i < 10; i++) {

			Random r = new Random();
			double randX = 0.0 + r.nextDouble() * 750.0;
			double randY = 0.0 + r.nextDouble() * 450.0;
			Node newNode = new Node(randX, randY);
			this.alon.add(newNode);
		}
		//Add map
		this.map4 = new Image("/4_thefourthfloor.png");
		this.imageViewMap.setImage(this.map4);

		//set elevator to clicked
		//this.elevatorRadio.focusedProperty(true);
		//display nodes
		this.displayNodes(this.alon);
	}

	@FXML
	private void logAsAdminClicked()
			throws IOException, InvocationTargetException {
		Parent loginPrompt = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/LoginPrompt.fxml"));
		System.out.print("here");
		Scene loginPromptScene = new Scene(loginPrompt);
		Stage loginPromptStage = new Stage();
		loginPromptStage.setScene(loginPromptScene);


		loginPromptStage.showAndWait();


	}


//	@FXML
//	private void changeFloor()throws Exception{
//
//		int newFloor = (int) floorSlider.getValue();
//		imageViewMap.setImage()
//	}

	@FXML
	private void mapClicked() {
		//System.out.print("Map Clicked");
		this.imageViewMap.setPickOnBounds(true);


		this.imageViewMap.setOnMouseClicked(e -> {
			System.out.println("[" + e.getX() + ", " + e.getY() + "]");
			//Paint something at that location
			this.paintOnLocation(e.getX(), e.getY(), this.alon);
		});
	}


	public void paintOnLocation(double x, double y, ArrayList<entities.Node> alon ) {
		Circle circ;
		circ = new Circle(x, y, 5, Color.web("0x0000FF"));

		this.contentAnchor.getChildren().add(circ);
		//clickNode.setX
		alon.add(this.clickNode);
		circ.setVisible(true);
	}

	public void displayNodes(ArrayList<entities.Node> alon) {

		for (int i = 0; i < alon.size(); i++) {
			Circle circ;
			double nodeX = alon.get(i).getX();
			double nodeY = alon.get(i).getY();

			circ = new Circle(nodeX, nodeY, 5, Color.web("0x0000FF"));
			this.contentAnchor.getChildren().add(circ);
			circ.setVisible(true);
		}


	}
	@FXML
	public void getDirectionsClicked() {
		this.paintPath(this.alon);
	}

	public void paintPath(ArrayList<entities.Node> alon) {
		for (int i = 0; i < alon.size() - 1; i++) {
			double nodeX1 = alon.get(i).getX();
			double nodeY1 = alon.get(i).getY();
			double nodeX2 = alon.get(i+1).getX();
			double nodeY2 = alon.get(i+1).getY();
			final Line line = new Line();
			line.setStartX(nodeX1);
			line.setStartY(nodeY1);
			line.setEndX(nodeX2);
			line.setEndY(nodeY2);

			this.contentAnchor.getChildren().add(line);
		}
	}
}
