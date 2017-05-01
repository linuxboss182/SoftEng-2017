package main;

import java.util.HashSet;
import java.util.Set;
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
	private Set<Runnable> tasks;

	private TimeoutTimer(){
		this.tasks = new HashSet<>();
		this.timer = new Timer();
		System.out.println("TimeoutTimer.TimeoutTimer");
	}

	public void registerTask(Runnable task) {
		this.tasks.add(task);
	}

	private TimerTask getTaskTimer(){
		return new TimerTask() {
			@Override
			public void run() {
				for (Runnable task : tasks) {
					task.run();
				}
			}
		};
	}

	public void resetTimer() {
		System.out.println(tasks);
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

//	public Timer getTimer() {
//		return this.timer;
//	}

	public static TimeoutTimer getTimeoutTimer() {
		return Singleton.instance;
	}
}
