package com.inf8405.bejeweled.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.inf8405.bejeweled.R;
import com.inf8405.bejeweled.core.Canvas;
import com.inf8405.bejeweled.core.Game;

/**
 * Cette classe represente notre menu principal
 */
public class GameOver extends Activity implements View.OnClickListener {
	/**
	 * Cette methode est executee a lorsque le menu est cree
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_over);

		// On obtient les references vers tous les boutons
		Button replay = (Button) findViewById(R.id.replay);
		Button menu = (Button) findViewById(R.id.main_menu);
		Button quit = (Button) findViewById(R.id.quit);

		// Add score gained in last game
		TextView score = (TextView) findViewById(R.id.score);
		score.setText(Game.last_score + "");

		// On ajuste la police pour les 2 labels
		TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
		scoreLabel.setTypeface(Canvas.FONT);
		score.setTypeface(Canvas.FONT);

		// Et on ecoute les evennements dessus
		replay.setOnClickListener(this);
		menu.setOnClickListener(this);
		quit.setOnClickListener(this);
	}

	/**
	 * Cette methode permet de gerer les evennements de click sur les boutons
	 */
	@Override
	public void onClick(View v) {
		// Dependemment du bouton clique on redirige vers l'activite
		// correspondante
		switch (v.getId()) {
			case R.id.replay:
				String mode = Game.last_mode == Game.MODE.TIME_TRIAL ? "MODE1" : "MODE2";
				startActivity(new Intent("com.inf8405.bejeweled." + mode));
			break;

			case R.id.main_menu:
				startActivity(new Intent("com.inf8405.bejeweled.MAIN"));
			break;
		}

		// Tous les boutons doivent terminer l'activite courante
		// Le bouton quitter termine la derniere activite ce qui ferme
		// l'application
		finish();
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
