package com.inf8405.bejeweled.core;

public class Player {
	private String name;
	private int score;

	public Player(String name) {
		if (name != null) {
			this.name = name;
		} else {
			name = "";
		}
		this.score = 0;
	}
	
	public Player() {
		this.name = "";
		this.score = 0;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}
}
