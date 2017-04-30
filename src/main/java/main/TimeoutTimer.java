package main;


import controllers.shared.MapDisplayController;
import controllers.user.UserState;
import entities.Directory;

import java.util.Timer;
import java.util.TimerTask;

public final class TimeoutTimer
{
	private Directory directory  = ApplicationController.getDirectory();
	private TimeoutTimer timeoutTimer;
	private Timer timer;

	private TimeoutTimer(){
		this.timer = new Timer();
	}

	public void resetTimer(TimerTask timerTask) {
		if(this.timeoutTimer == null) {
			timeoutTimer = new TimeoutTimer();
		}
		timer.cancel();
		timer = new Timer();
		timer.schedule(timerTask, directory.getTimeout());
	}

	public Timer getTimer(){
		if(timeoutTimer == null){
			this.timeoutTimer = new TimeoutTimer();
		}
		return this.timer;
	}
}
