package com.xzkj.taskChannel.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BusinessWorker {

	private ThreadPoolExecutor executor;	
	
	private static class Inner {
		private static final BusinessWorker instance = new BusinessWorker();
	}

	private BusinessWorker() {
		executor = new ThreadPoolExecutor(600, 20000, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(200000), new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	public static ThreadPoolExecutor getExecutor() {
		return Inner.instance.executor;
	}

	public static String WorkerInfo(){
		StringBuilder log = new StringBuilder();
		log.append(" 曾计划执行的近似任务总数:").append(getExecutor().getTaskCount())
		.append(" 已完成执行的近似任务总数:").append(getExecutor().getCompletedTaskCount())
		.append(" 池中曾出现过的最大线程数:").append(getExecutor().getLargestPoolSize())
		.append(" 返回线程池中的当前线程数:").append(getExecutor().getPoolSize())
		.append(" 线程池中的当前活动线程数:").append(getExecutor().getActiveCount())
		.append(" 线程池中约定的核心线程数:").append(getExecutor().getCorePoolSize());
		
		return log.toString();
	}
	
}
