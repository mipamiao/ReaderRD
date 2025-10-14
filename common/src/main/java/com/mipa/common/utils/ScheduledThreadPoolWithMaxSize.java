package com.mipa.common.utils;

import com.mipa.common.Constant.ExMsg;
import com.mipa.common.exception.BizException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduledThreadPoolWithMaxSize {

	private AtomicInteger size;

	private ScheduledExecutorService scheduler;

	private Integer threadNum;

	private final ConcurrentHashMap<String, ScheduledFuture<?>> taskNameMap = new ConcurrentHashMap<String, ScheduledFuture<?>>();

	public ScheduledThreadPoolWithMaxSize(Integer threadNum, String poolName, Integer maxSize){
		this.threadNum = threadNum;
		scheduler = Executors.newScheduledThreadPool(threadNum);
		size = new AtomicInteger(maxSize);
	}

	public void scheduleAtFixedRate(String name, Runnable command, long initialDelay, long period, TimeUnit unit) {
		if (!tryDec()) {
			throw BizException.internalServerError(ExMsg.TASK_REJECT_LIMIT_SIZE);
		}
		taskNameMap.put(name, scheduler.scheduleAtFixedRate(command, initialDelay, period, unit));
	}

	public void scheduleOneShot(String name, Runnable command, long delay, TimeUnit unit) {
		if (!tryDec()) {
			throw BizException.internalServerError(ExMsg.TASK_REJECT_LIMIT_SIZE);
		}

		ScheduledFuture<?> future = scheduler.schedule(() -> {
			try {
				command.run();
			} finally {
				// 任务执行完后回收名额
				size.incrementAndGet();
				taskNameMap.remove(name);
			}
		}, delay, unit);
		taskNameMap.put(name, future);
	}

	public boolean cancleTask(String name) {
		var task = taskNameMap.remove(name);
		if(task == null)return true;
		if (task.cancel(true)) {
			size.incrementAndGet();
			return true;
		}
		return false;
	}

	public Integer getThreadNum() {
		return threadNum;
	}

	private boolean tryDec() {
		while (true) {
			int current = size.get();
			if (current <= 0) {
				return false;
			}
			if (size.compareAndSet(current, current - 1)) {
				return true;
			}
		}
	}
}
