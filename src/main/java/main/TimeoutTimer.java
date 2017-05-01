package main;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import entities.Directory;
import javafx.application.Platform;

public final class TimeoutTimer
{
	private static class Singleton
	{
		static final TimeoutTimer instance = new TimeoutTimer();
	}

	private Directory directory  = ApplicationController.getDirectory();
	private Timer timer;
	private TimerTask timerTask;
	private Set<Runnable> tasks;

	private TimeoutTimer(){
		this.tasks = new HashSet<>();
		this.timer = new Timer();
	}

	public void registerTask(Runnable task) {
		this.tasks.add(task);
	}

	private TimerTask getTaskTimer(){
		return new TimerTask() {
			@Override
			public void run() {
				for (Runnable task : tasks) {
					Platform.runLater(task::run);
				}
			}
		};
	}

	public void resetTimer() {
		try {
			this.timer.cancel();
		} catch(Exception e) {
			e.printStackTrace();}

		try {
			this.timer = new Timer();
			this.timer.schedule(getTaskTimer(), directory.getTimeout());
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void emptyTasks(){
		tasks.clear();
	}

	public void cancelTimer() {
		try{
			if(this.timer == null) return;
			this.timer.cancel();

			if(this.timerTask == null) return;
			this.timerTask.cancel();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static TimeoutTimer getTimeoutTimer() {
		return Singleton.instance;
	}
}
