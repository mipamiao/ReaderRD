package com.mipa.common.utils;

import java.util.Random;

public class TimeStamp<T> {
	public T data;
	public Integer timeStamp;

	public TimeStamp(){};

	public TimeStamp(T data){
		this.data = data;
		timeStamp = new Random().nextInt();
	}

	public TimeStamp(T data, Integer timeStamp){
		this.data = data;
		this.timeStamp = timeStamp;
	}
}
