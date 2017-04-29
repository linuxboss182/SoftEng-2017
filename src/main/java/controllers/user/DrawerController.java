package controllers.user;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import controllers.shared.MapDisplayController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
		System.out.println("contentAnchorListener");
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



	// TODO: See if this really has to override
	@Override
	public void fitMapSize() {
		double potentialScaleX = mapScroll.getViewportBounds().getWidth() / contentAnchor.getWidth(); //Gets the ratio to default to
		double potentialScaleY = mapScroll.getViewportBounds().getHeight() / contentAnchor.getHeight();

		double potentialX = contentAnchor.getTranslateX() + mapScroll.localToScene(mapScroll.getViewportBounds()).getMinX() - contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinX();
		double potentialY = contentAnchor.getTranslateY() + mapScroll.localToScene(mapScroll.getViewportBounds()).getMinY() - contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinY();

		//Fixes the offset to center
		if (mainDrawer.isShown()) {
			potentialX += 420;
			potentialScaleX = (mapScroll.getViewportBounds().getWidth() - 420) / contentAnchor.getWidth();
		} else {
			potentialX = contentAnchor.getTranslateX() + mapScroll.localToScene(mapScroll.getViewportBounds()).getMinX() - contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinX();
			potentialScaleX = mapScroll.getViewportBounds().getWidth() / contentAnchor.getWidth(); //Gets the ratio to default to
		}

		if(potentialScaleX < potentialScaleY) { //Preserves the ratio by taking the minimum
			contentAnchor.setScaleX(potentialScaleX);
			contentAnchor.setScaleY(potentialScaleX);
			currentScale = potentialScaleX;
		} else {
			contentAnchor.setScaleX(potentialScaleY);
			contentAnchor.setScaleY(potentialScaleY);
			currentScale = potentialScaleY;
		}

		contentAnchor.setTranslateX(potentialX);
		contentAnchor.setTranslateY(potentialY);

		zoomSlider.setValue(0);
	}

}
