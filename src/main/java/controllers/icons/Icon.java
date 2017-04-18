package controllers.icons;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;

public class Icon
		extends Group
{
	private Node symbol;
	private Text label;
	
	public Icon(Node symbol, Text label) {
		super(symbol, label);
		this.symbol = symbol;
		this.label = label;
	}
	
	public void relocateSymbol(double x, double y) {
		this.symbol.relocate(x, y);
	}
	
	public void relocateLabel(double x, double y) {
		this.label.relocate(x, y);
	}

	public Text getLabel() {
		return this.label;
	}
}
