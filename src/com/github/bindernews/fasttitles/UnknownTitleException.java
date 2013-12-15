package com.github.bindernews.fasttitles;

public class UnknownTitleException extends Exception {

	private static final long serialVersionUID = 1587800714816244052L;

	public UnknownTitleException() {
	}

	public UnknownTitleException(String message) {
		super(message);
	}

	public UnknownTitleException(Throwable cause) {
		super(cause);
	}

	public UnknownTitleException(String message, Throwable cause) {
		super(message, cause);
	}

}
