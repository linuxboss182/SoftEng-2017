package controllers.currentlydonothing;

import entities.Node;
import entities.Professional;
import entities.Room;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;


public class EventHandlers
{


//	public void addLisenter(EditorController that) {
//		this.botPane.setOnMouseClicked(e -> {
//
//			this.setFields(e.getX(), e.getY());
//
//			//Create node on double click
//			if(e.getClickCount() == 2) {
//				this.addNode(e.getX(), e.getY());
//			}
//
//			if(this.selectedShape != null) {
//				if(this.selectedNode.getRoom() != null) {
//					if (this.selectedNode.getRoom().getName().equalsIgnoreCase(this.KIOSK_NAME)) {
//						this.selectedShape.setFill(this.KIOSK_COLOR);
//					} else {
//						this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
//					}
//				}
//			}
//
//			this.selectedNode = null;
//			this.selectedShape = null;
//		});


//
//		contentAnchor.setOnScroll(new EventHandler<ScrollEvent>() {
//			@Override public void handle(ScrollEvent event) {
//				event.consume();
//				if (event.getDeltaY() == 0) {
//					return;
//				}
//				double scaleFactor =
//						(event.getDeltaY() > 0)
//								? SCALE_DELTA
//								: 1/SCALE_DELTA;
//				contentAnchor.setScaleX(contentAnchor.getScaleX() * scaleFactor);
//				contentAnchor.setScaleY(contentAnchor.getScaleY() * scaleFactor);
//			}
//		});
//
//		contentAnchor.setOnMousePressed(new EventHandler<MouseEvent>() {
//			public void handle(MouseEvent event) {
//				clickedX = event.getX();
//				clickedY = event.getY();
//			}
//		});
//
//		contentAnchor.setOnMouseDragged(new EventHandler<MouseEvent>() {
//			public void handle(MouseEvent event) {
//				if(!beingDragged) {
//					contentAnchor.setTranslateX(contentAnchor.getTranslateX() + event.getX() - clickedX);
//					contentAnchor.setTranslateY(contentAnchor.getTranslateY() + event.getY() - clickedY);
//				}
//				event.consume();
//			}
//		});
//	}




//
//
//
//		@Override
//	public void addRoomListener(Room r){
//		r.getShape().setOnMouseClicked((MouseEvent e) ->{
//			EditorController.this.clickNodeListener(e, r);
//			this.nameField.setText(r.getName());
//			this.descriptField.setText((r.getDescription()));
//		});
//
//		r.getShape().setOnMouseDragged(e->{
//			beingDragged = true;
//			EditorController.this.onRectangleDrag(e, r);
//		});
//
//		// Working as intended
//		r.getShape().setOnMousePressed(e->{
//			this.primaryPressed = e.isPrimaryButtonDown();
//			this.secondaryPressed = e.isSecondaryButtonDown();
//		});
//
//
//		r.getShape().setOnMouseReleased(e->{
//			beingDragged = false;
//			EditorController.this.releaseNodeListener(e, r);
//		});
//	}




//	public void clickNodeListener(MouseEvent e, Node n) {
//
//		// update text fields
//		this.setFields(n.getX(), n.getY());
//
//		// check if you single click
//		// so, then you are selecting a node
//		if(e.getClickCount() == 1 && this.primaryPressed) {
//			if(this.selectedShape != null) {
//				// TODO: Change use of instanceof to good coding standards
//				if(this.selectedShape != null) {
//					if(this.selectedNode.getRoom() != null) {
//						if (this.selectedNode.getRoom().getName().equalsIgnoreCase(this.KIOSK_NAME)) {
//							this.selectedShape.setFill(this.KIOSK_COLOR);
//						} else {
//							this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
//						}
//					}
//				}
//
//				} else {
//					this.selectedShape.setFill(this.DEFAULT_SHAPE_COLOR);
//				}
//
//			this.selectedShape = (Shape) e.getSource();
//			this.selectedNode = n;
//			this.selectedShape.setFill(this.SELECTED_SHAPE_COLOR);
//		} else if(this.selectedNode != null && !this.selectedNode.equals(n) && this.secondaryPressed) {
//			// ^ checks if there has been a node selected,
//			// checks if the node selected is not the node we are clicking on
//			// and checks if the button pressed is the right mouse button (secondary)
//
//			// finally check if they are connected or not
//			// if they are connected, remove the connection
//			// if they are not connected, add a connection
//			this.selectedNode.connectOrDisconnect(n);
//			this.redrawLines();
//		}
//	}
//
//	// This is going to allow us to drag a node!!!
//	public void onCircleDrag(MouseEvent e, Node n) {
//		beingDragged = true;
//		if(this.selectedNode != null && this.selectedNode.equals(n)) {
//			if(this.primaryPressed) {
//				this.selectedShape = (Shape) e.getSource();
//				this.updateSelectedNode(e.getX(), e.getY());
//				this.setFields(this.selectedNode.getX(), this.selectedNode.getY());
//				this.redrawLines();
//			} else if(this.secondaryPressed) {
//				// right click drag on the selected node
//			}
//		}
//	}
//
//	public void releaseNodeListener(MouseEvent e, Node n) {
//		this.releasedX = e.getX();
//		this.releasedY = e.getY();
//
//		// if the releasedX or Y is negative we want to remove the node
//
//		if(this.releasedX < 0 || this.releasedY < 0) {
//			this.deleteSelectedNode();
//		}
//	}


//	public void onRectangleDrag(MouseEvent e, Room r) {
//		if(this.selectedNode != null && this.selectedNode.equals(r)) {
//			if(this.primaryPressed) {
//				this.selectedShape = (Rectangle) e.getSource();
//				this.updateSelectedRoom(e.getX()-this.RECTANGLE_WIDTH/2, e.getY()-this.RECTANGLE_HEIGHT/2, r.getName(), r.getDescription());
//				this.setFields(this.selectedNode.getX(), this.selectedNode.getY());
//				this.redrawLines();
//			} else if(this.secondaryPressed) {
//				// right click drag on the selected node
//			}
//		}

}
