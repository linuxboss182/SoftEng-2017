package AdminPanel;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class Controller {
    @FXML
    private Button addRoomBtn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptField;
    @FXML
    private TextField XcoordField;
    @FXML
    private TextField YcoordField;
    @FXML
    private ImageView imageViewMap;

    @FXML
    private void mapClicked() {

    }




    /////////////////////
    // Stuff Ted Added //
    /////////////////////

    // Also, I changed the parameters for mapClicked
    private ArrayList<Node> nodes = new ArrayList<>();
    private int clickedX;
    private int clickedY;

    // This theoretically should be called
    // It sets up the listeners
    public Controller() {
        // Clicking on the floor image
        imageViewMap.setPickOnBounds(true); // allow detection of clicks on transparent parts of image
        imageViewMap.setOnMouseClicked(e -> {
            mapClicked((int) e.getX(), (int) e.getY());
        });

        // Add Room Button Pressed
        addRoomBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                System.out.println("Clicked the button");
                addRoomBtnClicked();
            }
        });
    }

    @FXML
    private void addRoomBtnClicked() {
        Node n = new Node(readXPosField(), readYPosField(), readNameField(), readDescriptionField());
        nodes.add(n);
        System.out.println("Added a new node to the buffer");
    }

    @FXML
    private void mapClicked(int x, int y) {
        updatePositions(x, y);
        System.out.println("Clicked (" + x + ", " + y + ")");
    }

    //TODO: ADDS THE NODES TO THE DATABASE!!! -- Requires DatabaseController
    private void flushbuffer() {

    }

    private void updatePositions(int x, int y) {
        clickedX = x;
        clickedY = y;
        setXPosText("" + x);
        setYPosText("" + y);
        System.out.println("Updated the X and Y text fields to (" + clickedX + ", " + clickedY + ")");
    }

    private void setXPosText(String text) {
        XcoordField.setText(text);
    }

    private void setYPosText(String text) {
        YcoordField.setText(text);
    }

    private int readXPosField() {
        String raw = XcoordField.getText();
        return Integer.parseInt(raw);
    }

    private int readYPosField() {
        String raw = YcoordField.getText();
        return Integer.parseInt(raw);
    }

    private String readNameField() {
        return nameField.getText();
    }

    private String readDescriptionField() {
        return descriptField.getText();
    }

}
