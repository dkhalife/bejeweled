package com.inf8405.bejeweled.core;

import java.util.Random;

import android.graphics.Bitmap;

/**
 * Cette classe represente un Gem du jeu
 */
public class Gem {
	// Les differentes couleurs possibles
	protected static enum GemColor {
		BLUE, GREEN, ORANGE, PURPLE, RED, WHITE, YELLOW
	}

	// Constantes pour la physique
	private static final float ACCELERATION = 180f;
	// La resolution des textures en pixels
	public static final int ORIGINAL_TEX_RES = 128;

	private static Random rand = new Random();
	// Le facteur d'aggrandissement pour le dessin
	public static float SCALE = 1;

	public static final float SPEED_BOOST = ACCELERATION / 10;
	
	private static final float MAX_SPEED = Game.SQUARE_SIZE;

	/**
	 * Cette methode permet d'obtenir une couleur aleatoire
	 * 
	 * @return Une couleur aleatoire
	 */
	public static GemColor getRandColor() {
		GemColor[] colors = GemColor.values();
		int max = colors.length;
		max = 5;
		return colors[rand.nextInt(max)];
	}

	// La couleur du gem
	private GemColor color;
	// L'opacite du gem
	private int opacity = 255;

	// Le score genere par ce gem
	public Score score = null;
	// La vitesse du gem
	private float speed = 0;
	// Et sa texture
	private Bitmap texture;
	// La position du gem
	public float x, y;

	/**
	 * Constructeur
	 * 
	 * @param i L'abscisse du gem
	 * @param j L'ordonnee du gem
	 */
	public Gem(float i, float j) {
		this.x = i;
		this.y = j;
		setColor(getRandColor());
	}

	/**
	 * Permet de savoir si un point se retrouve dans la Bounding Box de cet
	 * objet
	 * 
	 * @param x L'abscisse du point
	 * @param y L'ordonnee du point
	 * @return True si le point est a l'interieur du Bounding Box
	 */
	public boolean collidseWith(float i, float j) {
		return i >= x && i < x + Game.SQUARE_SIZE && j >= y && j < y + Game.SQUARE_SIZE;
	}

	/**
	 * Dessine cet objet
	 */
	public void draw(android.graphics.Canvas canvas) {
		canvas.drawBitmap(texture, x, y, Canvas.paint);
	}

	public void fakeDestroy() {
		// Au lieu de les detruire, on ne fait que les replacer en haut
		y = (-Game.SQUARE_SIZE);
		// Et generer une nouvelle couleur
		setColor(Gem.getRandColor());
		setOpacity(255);
	}

	/**
	 * Fait tomber cet objet sur l'ecran
	 * 
	 * @param delta Le temps ecoule depuis le dernier appel
	 */
	public void fall(Game game, float delta) {
		Gem under = game.getGemAt(x, y + Game.SQUARE_SIZE);

		if (y < (Game.SIZE - 1) * Game.SQUARE_SIZE && under == null) {
			// Small initial speed boost
			if (speed == 0)
				speed = SPEED_BOOST;

			speed = Math.min(MAX_SPEED, speed + ACCELERATION * delta);

			y += speed;
			return;
		}

		// Stop falling
		speed = 0;
		snapPosition();
	}

	/**
	 * Methode d'acces au bas du Gem
	 * 
	 * @return Le bas du Gem
	 */
	public float getBottom() {
		return y + Game.SQUARE_SIZE;
	}

	/**
	 * Methode d'acces a la couleur du Gem
	 * 
	 * @return
	 */
	public GemColor getColor() {
		return color;
	}

	/**
	 * Methode d'acces a l'opacite du Gem
	 * 
	 * @return L'opacite du Gem
	 */
	public int getOpacity() {
		return opacity;
	}

	/**
	 * Methode d'acces a la droite du Gem
	 * 
	 * @return La droite du Gem
	 */
	public float getRight() {
		return x + Game.SQUARE_SIZE;
	}

	/**
	 * Methode d'acces a la vitesse du Gem
	 * 
	 * @return
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * Methode d'acces a l'ordonnee du Gem
	 * 
	 * @return L'ordonnee du Gem
	 */
	public float getTop() {
		return y;
	}

	/**
	 * Permet de facilement réinitialiser un Gem
	 */
	public void reset(float x, float y) {
		setPosition(x, y);
		setOpacity(255);
		setSpeed(0);
		setColor(getRandColor());
	}

	/**
	 * Methode de modification de la couleur du Gem
	 * 
	 * @param color La nouvelle couleur du Gem
	 */
	public void setColor(GemColor color) {
		this.color = color;

		switch (this.color) {
			case BLUE:
				texture = Game.tex_blue;
			break;

			case GREEN:
				texture = Game.tex_green;
			break;

			case ORANGE:
				texture = Game.tex_orange;
			break;

			case PURPLE:
				texture = Game.tex_purple;
			break;

			case RED:
				texture = Game.tex_red;
			break;

			case WHITE:
				texture = Game.tex_white;
			break;

			case YELLOW:
				texture = Game.tex_yellow;
			break;
		}
	}

	/**
	 * Methode de modification de l'opacite du Gem
	 * 
	 * @param opacity La nouvelle opacite du Gem
	 */
	public void setOpacity(int opacity) {
		this.opacity = Math.max(0, Math.min(255, opacity));
	}

	/**
	 * Methode de modification de la position du Gem
	 * 
	 * @param x La nouvelle abscisse
	 * @param y La nouvelle ordonnee
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Methode de moficiation de la vitesse du Gem
	 * 
	 * @param speed
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Cette methode permet de fixer le Gem dans une position
	 */
	public void snapPosition() {
		float dx = Game.SQUARE_SIZE;
		float dy = Game.SQUARE_SIZE;

		x = (float) Math.floor((x + dx / 2) / dx) * dx;
		y = (float) Math.floor((y + dy / 2) / dy) * dy;
	}
}
