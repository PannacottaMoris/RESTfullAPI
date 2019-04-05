package com.example.demo.exception;

public class ExceptionNone extends RuntimeException {
	private String comment;

	public ExceptionNone(String comment) {
		this.setComment(comment);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
