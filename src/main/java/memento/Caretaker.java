package memento;

import java.util.ArrayList;
import java.util.List;

public class Caretaker
{
	private List<UserState> stateList;

	public Caretaker() {
		this.stateList = new ArrayList<>();
	}

	public void addState(UserState state) {
		this.stateList.add(state);
	}

	public UserState getState() {
		return stateList.get(0);
	}
}
