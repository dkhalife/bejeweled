package com.inf8405.bejeweled.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.inf8405.bejeweled.core.Canvas;
import com.inf8405.bejeweled.core.Game;
import com.inf8405.bejeweled.core.PlayersNameDialog;

/**
 * Cette classe represente le mode 1 du jeu
 */
public class Mode1 extends Activity implements OnTouchListener {
	// Wake lock (pour que le cell ne sleep pas pendant le jeu)
	private WakeLock wL;
	// Le canvas dans lequel on dessine le jeu
	private Canvas canvas;
	// Les classe qui s'occupe des regles du jeu
	private Game game;
	
	/**
	 * Cette methode est appellee lors de la creation de l'activite
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// On obtient le lock pour prevenir le sleep pendant le jeu
		PowerManager pM = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wL = pM.newWakeLock(PowerManager.FULL_WAKE_LOCK, "tag");
		super.onCreate(savedInstanceState);
		wL.acquire();

		game = new Game(Game.MODE.TIME_TRIAL, this);
		canvas = new Canvas(this, game);
		canvas.setOnTouchListener(this);

		setContentView(canvas);

		Display display = getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);
		canvas.setSize(outMetrics.widthPixels, outMetrics.heightPixels);
		game.resetGame(true);
	}

	/**
	 * Cette methode est appelle lorsque le jeu se met en pause
	 */
	@Override
	protected void onPause() {
		// Il ne faut pas oublier de relacher le lock
		wL.release();

		super.onPause();
		canvas.pause();
	}

	/**
	 * Cette methode est appelle lorsque l'application revient du mode pause
	 */
	@Override
	protected void onResume() {
		// Il faut qu'on obtient de nouveau le wake lock
		wL.acquire();

		super.onResume();
		canvas.resume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Dialog dialog = new PlayersNameDialog(this, game);
		dialog.show();
	}
	
	/**
	 * On ecoute les evennements tactiles
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return game.onTouch(event);
	}

	/**
	 * Cette fonction est appellee lorsqu'on appuie sur le bouton back
	 */
	public void onBackPressed() {
		// Le bouton back menera au menu principal
		startActivity(new Intent("com.inf8405.bejeweled.MAIN"));
		finish();
		return;
	}
}