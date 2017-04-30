package main;

import controllers.shared.MapDisplayController;
import controllers.user.UserState;
import entities.Directory;

import java.util.Timer;
import java.util.TimerTask;

public final class TimeoutTimer
{
	private static class Singleton
	{
		static final TimeoutTimer instance = new TimeoutTimer();
	}

	private Directory directory = ApplicationController.getDirectory();
	private Timer timer;

	private TimeoutTimer(){
		this.timer = new Timer();
		System.out.println("TimeoutTimer.TimeoutTimer");
	}

	public void resetTimer(TimerTask timerTask) {
		try {
			this.timer.cancel();
		} catch(Exception e) {
			e.printStackTrace();}

		try {
			this.timer = new Timer();
			this.timer.schedule(timerTask, directory.getTimeout());
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void cancelTimer() {
		try{
			this.timer.cancel();
			this.timer.purge();
			this.resetTimer(new TimerTask() {
				public void run() {

				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Timer getTimer() {
		return this.timer;
	}

	public static TimeoutTimer getTimeoutTimer() {
		return Singleton.instance;
	}
}
