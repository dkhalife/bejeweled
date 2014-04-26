package com.inf8405.bejeweled.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Cette classe represente le canvas dans lequel on dessine le jeu
 */
public class Canvas extends SurfaceView implements Runnable {
	// Le surface holder qui permet d'obtenir access au vrai canvas
	private SurfaceHolder holder;
	// On a besoin d'un thread pour faire le dessin/calcul en continu
	private Thread t;
	// L'instance du jeu
	private Game game;
	// L'etat du jeu
	private boolean isRunning = false;

	// Un paint pour les dessins
	public static final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
	// La taille de la police
	public static float FONT_SIZE = 10;
	// La police utilisée
	public static Typeface FONT;

	// La taille du canvas
	public static float PADDING = 0;

	/**
	 * Constructeur par paramtre
	 * 
	 * @param context Le contexte Android
	 * @param game L'instance du jeu
	 */
	public Canvas(Context context, Game game) {
		super(context);

		holder = getHolder();
		this.game = game;

		Game.SQUARE_SIZE = 128;
		FONT_SIZE = 30;

		Typeface fontFace = Typeface.createFromAsset(context.getAssets(), "fonts/snapit.ttf");
		FONT = Typeface.create(fontFace, Typeface.NORMAL);

		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.WHITE);
		paint.setTextSize(FONT_SIZE);
		paint.setTypeface(FONT);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
	}

	/**
	 * Cette methdoe est executee lorsque le jeu se met en pause
	 */
	public void pause() {
		// On met a jour l'etat du jeu
		isRunning = false;

		// Et on arrete le thread
		while (true) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			break;
		}

		t = null;
	}

	/**
	 * Cette methode est executee lorsque le jeu revient du mode pause
	 */
	public void resume() {
		// On met a jour l'etat du jeu
		isRunning = true;
		// Et on redemarre le thread
		t = new Thread(this);
		t.start();
	}

	// 17ms pour ~ 60FPS
	private static final long REFRESH_INTERVAL_MS = 17;

	/**
	 * Cette methode est executee par le thread
	 */
	@Override
	public void run() {
		while (isRunning) {
			if (!holder.getSurface().isValid()) {
				continue;
			}

			// On redessine le canvas et on met a jour le jeu
			android.graphics.Canvas c = holder.lockCanvas();
			game.update(REFRESH_INTERVAL_MS / 1000);
			// On introduit une marge a gauche pour centrer le tout au milieu
			c.translate(PADDING, 0);
			long durationMs = game.render(c);
			holder.unlockCanvasAndPost(c);

			try {
				Thread.sleep(Math.max(0, REFRESH_INTERVAL_MS - durationMs));
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Methode pour convertir des pixels en dp
	 * 
	 * @param px Les pixels
	 * @return Les dp correspondants
	 */
	public float px2dp(int px) {
		return ((float) px) / getContext().getResources().getDisplayMetrics().density;
	}

	/**
	 * Methode pour convertir des pixels en sp
	 * 
	 * @param px Les pixels
	 * @return Les sp correspondants
	 */
	public float px2sp(int px) {
		return ((float) px) / getContext().getResources().getDisplayMetrics().scaledDensity;
	}

	/**
	 * Cette methode permet de redimenssionner une bitmap
	 * 
	 * @param bm L'image a redimensionner
	 * @param newHeight La nouvelle hauteur
	 * @param newWidth La nouvelle largeur
	 * @return La nouvelle image
	 */
	private static Bitmap resizeBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

	/**
	 * Methode qui permet de redimensionner le canvas
	 * 
	 * @param width La nouvelle largeur
	 * @param height La nouvelle hauteurF
	 */
	public void setSize(int width, int height) {
		// On veut 8 carre sur la largeur
		// On garde un marge de 10px
		int smallest = Math.min(width, height);
		int columns = Game.SIZE + 2;
		// On determine la taille des Gems en PX
		Gem.SCALE = (smallest / columns) / Gem.ORIGINAL_TEX_RES;

		// On divise la taille sur 10 blocs
		// 1 bloc de chaque coté (droite et gauche) sera utilisé comme padding
		// 2 blocs seront utilisés en haut pour le text
		Game.SQUARE_SIZE = smallest / columns;

		PADDING = (width - Game.SIZE * Game.SQUARE_SIZE) / 2;

		// On scale les bitmaps
		Game.tex_black = resizeBitmap(Game.original_tex_black, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_select = resizeBitmap(Game.original_tex_select, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_red = resizeBitmap(Game.original_tex_red, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_green = resizeBitmap(Game.original_tex_green, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_blue = resizeBitmap(Game.original_tex_blue, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_purple = resizeBitmap(Game.original_tex_purple, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_orange = resizeBitmap(Game.original_tex_orange, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_purple = resizeBitmap(Game.original_tex_purple, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_white = resizeBitmap(Game.original_tex_white, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_home = resizeBitmap(Game.original_tex_home, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_restart = resizeBitmap(Game.original_tex_restart, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
		Game.tex_close = resizeBitmap(Game.original_tex_close, Game.SQUARE_SIZE, Game.SQUARE_SIZE);
	}
}
