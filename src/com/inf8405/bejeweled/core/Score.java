package com.inf8405.bejeweled.core;

import java.util.ArrayList;

/**
 * Cette classe represente un score a afficher sur l'ecran
 */
public class Score {
	private int opacity = 255;
	private int score;
	private float x = 0;
	private float y = 0;

	public Score(ArrayList<Gem> combo, int value) {
		Gem first = combo.get(0);
		Gem last = combo.get(combo.size() - 1);
		x = (first.x + last.x) / 2 + Game.SQUARE_SIZE / 2;
		y = (first.y + last.y) / 2 + Game.SQUARE_SIZE / 2;
		score = value;
	}

	void draw(android.graphics.Canvas c) {
		Canvas.paint.setAlpha(opacity);
		c.drawText(score + "", x, y, Canvas.paint);
	}

	/**
	 * Updates the score
	 * 
	 * @return true if the score should stay alive and continue to be updated,
	 *         false if the score should be destroyed
	 */
	public boolean update() {
		opacity -= Game.FADE_SPEED / 2;
		y -= 1;
		return (opacity > 0);
	}
}
