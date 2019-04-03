package com.example.demo.exception;

import com.example.demo.entity.GoodsEntity;

public class ExceptionNone extends RuntimeException {
	private GoodsEntity good;

	public ExceptionNone(GoodsEntity good) {
		this.setGood(good);
	}

	public GoodsEntity getGood() {
		return good;
	}

	public void setGood(GoodsEntity good) {
		this.good = good;
	}
}
