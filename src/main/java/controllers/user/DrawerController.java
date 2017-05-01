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
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.stage.Stage;
import main.TimeoutTimer;

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
		userHelpStage.setTitle("Faulkner Hospital Navigator Help Screen");
		userHelpStage.getIcons().add(new Image("/bwhIcon.png"));
		userHelpStage.setResizable(false);
		userHelpStage.initOwner(contentAnchor.getScene().getWindow());
		userHelpStage.setScene(userHelpScene);
		userHelpStage.addEventFilter(MouseEvent.ANY, e-> {
			timer.resetTimer();
		});
		userHelpStage.addEventFilter(KeyEvent.ANY, e ->{
			timer.resetTimer();
		});
		TimeoutTimer.getTimeoutTimer().registerTask(() -> {
			userHelpStage.getScene().getWindow().hide();

		});
		userHelpStage.showAndWait();
		timer.emptyTasks();
		TimeoutTimer.getTimeoutTimer().registerTask(() -> {
			setState(directory.getCaretaker().getState());
		});
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
				defaultView = new Directory.Viewport(140, 540, 30, 150);
			} else if ("Outside".equals(directory.getFloor().getName())) {
				defaultView = new Directory.Viewport(12, 622, 13, 342);
			}
		}

		double potentialScaleY =
				mapScroll.getHeight() / (defaultView.maxY - defaultView.minY);

		double potentialScaleX =
				mapScroll.getWidth() / (defaultView.maxX - defaultView.minX);

		contentAnchor.setTranslateY(0);
		contentAnchor.setTranslateX(0);
		mapScroll.setScaleX(1);
		mapScroll.setScaleY(1);

		double offsetX = contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinX();
		double offsetY = contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinY();

		if(potentialScaleX < potentialScaleY) {
			mapScroll.setScaleX(potentialScaleX);
			mapScroll.setScaleY(potentialScaleX);
			currentScale = potentialScaleX;
		}else{
			mapScroll.setScaleX(potentialScaleY);
			mapScroll.setScaleY(potentialScaleY);
			currentScale = potentialScaleY;
		}

		if (currentScale <= 1) {
			zoomSlider.setValue(0);

		}else if(currentScale >= 5.5599173134922495) {
			zoomSlider.setValue(100);
		}else {
			zoomSlider.setValue(((currentScale - 1)/4.5599173134922495) * 100);
		}

		offsetX = contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinX() - offsetX;
		offsetY = contentAnchor.localToScene(contentAnchor.getBoundsInLocal()).getMinY() - offsetY;

		contentAnchor.setTranslateX(-defaultView.minX - offsetX / potentialScaleX);
		contentAnchor.setTranslateY(-defaultView.minY - offsetY / potentialScaleY);



//
//		if (scaleFactor * currentScale >= 1 && scaleFactor * currentScale <= 6) {
//			Bounds viewPort = mapScroll.getViewportBounds();
//			Bounds contentSize = contentAnchor.getBoundsInParent();
//
//			double centerPosX = (contentSize.getWidth() - viewPort.getWidth()) * mapScroll.getHvalue() + viewPort.getWidth() / 2;
//			double centerPosY = (contentSize.getHeight() - viewPort.getHeight()) * mapScroll.getVvalue() + viewPort.getHeight() / 2;
//
//			mapScroll.setScaleX(mapScroll.getScaleX() * scaleFactor);
//			mapScroll.setScaleY(mapScroll.getScaleY() * scaleFactor);
//			currentScale *= scaleFactor;
//
//			double newCenterX = centerPosX * scaleFactor;
//			double newCenterY = centerPosY * scaleFactor;
//
//			mapScroll.setHvalue((newCenterX - viewPort.getWidth() / 2) / (contentSize.getWidth() * scaleFactor - viewPort.getWidth()));
//			mapScroll.setVvalue((newCenterY - viewPort.getHeight() / 2) / (contentSize.getHeight() * scaleFactor - viewPort.getHeight()));
//		}
//
//		if (scaleFactor * currentScale <= 1) {
//			zoomSlider.setValue(0);
//
//		}else if(scaleFactor * currentScale >= 5.5599173134922495) {
//			zoomSlider.setValue(100);
//		}else {
//			zoomSlider.setValue(((currentScale - 1)/4.5599173134922495) * 100);
//		}
	}

}
