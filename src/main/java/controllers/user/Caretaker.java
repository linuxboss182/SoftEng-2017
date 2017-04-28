package controllers.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tbasl_000 on 4/27/2017.
 */
public class Caretaker
{
	private List<UserState> stateList;

	public Caretaker() {
		this.stateList = new ArrayList<>();
	}

	public void addState(UserState state) {
		this.stateList.add(state);
	}

	public List<UserState> getStateList() {
		return stateList;
	}
}
