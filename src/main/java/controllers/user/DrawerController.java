package controllers.user;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import controllers.shared.MapDisplayController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

		Platform.runLater(this::fitMapSize);
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
		double potentialY =
				+ mapScroll.getHeight()/2
				- contentAnchor.getHeight()/2;

		double potentialX;
		if(mainDrawer.isShown()) {
			potentialX = (mapScroll.getWidth()+420) / 2
					- contentAnchor.getWidth() / 2;
		}else{
			potentialX = (mapScroll.getWidth()) / 2
					- contentAnchor.getWidth() / 2;
		}

		contentAnchor.setTranslateX(potentialX);
		contentAnchor.setTranslateY(potentialY);
	}
}
