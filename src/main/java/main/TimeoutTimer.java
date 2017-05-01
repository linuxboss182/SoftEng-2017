package main;

import java.util.Timer;
import java.util.TimerTask;

import entities.Directory;

public final class TimeoutTimer
{
	private static class Singleton
	{
		static final TimeoutTimer instance = new TimeoutTimer();
	}

	private Directory directory  = ApplicationController.getDirectory();
	private Timer timer;
	private TimerTask timerTask;

	private TimeoutTimer(){
		this.timer = new Timer();
		System.out.println("TimeoutTimer.TimeoutTimer");
	}

	public void resetTimer(TimerTask timerTask) {
		this.timerTask = timerTask;
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
			if(this.timer == null) return;
			this.timer.cancel();
			this.timer.purge();

			if(this.timerTask == null) return;
			this.timerTask.cancel();
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
