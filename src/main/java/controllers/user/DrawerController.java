package controllers.user;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import controllers.shared.MapDisplayController;
import entities.Directory;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.stage.Stage;

import java.io.IOException;

abstract public class DrawerController
	extends MapDisplayController
{
	@FXML protected SplitPane mapSplitPane;
	@FXML protected JFXDrawer mainDrawer;
	@FXML protected Pane drawerParentPane;
	@FXML protected VBox drawerVBox;

	@FXML protected JFXHamburger directionsHamburgerButton;
	protected HamburgerBackArrowBasicTransition backTransition;

	protected double clickedX;
	protected double clickedY;


	protected void initialize() {
		super.initialize();
		this.mainDrawer.setContent(mapSplitPane);
		this.mainDrawer.setSidePane(drawerParentPane);
		this.mainDrawer.setOverLayVisible(false);
		this.mainDrawer.open();

		mapScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		this.setUpContentAnchorListeners();

		backTransition = new HamburgerBackArrowBasicTransition();
		backTransition.setRate(-1);


	}


	@FXML
	private void onHamburgerBtnClicked() throws IOException {
		backTransition.setRate(backTransition.getRate() * -1);
		backTransition.play();
		if(mainDrawer.isShown()) {
			mainDrawer.close();
		} else {
			mainDrawer.open();
		}
	}

	@FXML
	private void helpBtnClicked()
			throws IOException {
		UserHelpController helpController = new UserHelpController();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource("/UserHelp.fxml"));
		Scene userHelpScene = new Scene(loader.load());
		Stage userHelpStage = new Stage();
		userHelpStage.initOwner(contentAnchor.getScene().getWindow());
		userHelpStage.setScene(userHelpScene);
		userHelpStage.showAndWait();
	}

	private void setUpContentAnchorListeners() {
		contentAnchor.setOnMousePressed(event -> {
			clickedX = event.getX();
			clickedY = event.getY();
		});

		contentAnchor.setOnMouseDragged(event -> {
			// Limits the dragging for x and y coordinates. (panning I mean)
			if (event.getSceneX() >= mapSplitPane.localToScene(mapSplitPane.getBoundsInLocal()).getMinX() && event.getSceneX() <= mapScroll.localToScene(mapScroll.getBoundsInLocal()).getMaxX()) {
				contentAnchor.setTranslateX(contentAnchor.getTranslateX() + event.getX() - clickedX);
			}
			if (event.getSceneY() >= mapSplitPane.localToScene(mapSplitPane.getBoundsInLocal()).getMinY() && event.getSceneY() <= mapScroll.localToScene(mapScroll.getBoundsInLocal()).getMaxY()) {
				contentAnchor.setTranslateY(contentAnchor.getTranslateY() + event.getY() - clickedY);
			}
			event.consume();
		});
	}

	@Override
	public void fitMapSize() {
		Directory.Viewport defaultView = directory.getDefaultView();

		if (defaultView == null) {
			if ("Faulkner".equals(directory.getFloor().getName())) {
				defaultView = new Directory.Viewport(70, 480, 107, 348);
			} else if ("Belkin".equals(directory.getFloor().getName())) {
				defaultView = new Directory.Viewport(300, 400, 230, 280);
			} else if ("Outside".equals(directory.getFloor().getName())) {
				defaultView = new Directory.Viewport(0, 675, 0, 486);
			}
		}

		double potentialScaleY =
				mapScroll.getHeight() / (defaultView.maxY - defaultView.minY);

		double potentialScaleX =
				mapScroll.getWidth() / (defaultView.maxX - defaultView.minX);

		double offsetX = contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinX();
		double offsetY = contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinY();

		if(potentialScaleX < potentialScaleY) {
			mapScroll.setScaleX(potentialScaleX);
			mapScroll.setScaleY(potentialScaleX);
		}else{
			mapScroll.setScaleX(potentialScaleY);
			mapScroll.setScaleY(potentialScaleY);
		}

		offsetX = contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinX() - offsetX;
		offsetY = contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinY() - offsetY;

		contentAnchor.setTranslateX(-defaultView.minX - offsetX / potentialScaleX);
		contentAnchor.setTranslateY(-defaultView.minY - offsetY / potentialScaleY);
	}

}
