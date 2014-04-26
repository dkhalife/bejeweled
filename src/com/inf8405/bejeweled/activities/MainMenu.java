package com.inf8405.bejeweled.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.inf8405.bejeweled.R;

/**
 * Cette classe represente notre menu principal
 */
public class MainMenu extends Activity implements View.OnClickListener {

	/**
	 * Cette methode est executee a lorsque le menu est cree
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		// On obtient les references vers tous les boutons
		Button mode1 = (Button) findViewById(R.id.mode1);
		Button mode2 = (Button) findViewById(R.id.mode2);
		Button scores = (Button) findViewById(R.id.scores);
		Button quit = (Button) findViewById(R.id.quit);

		// Et on ecoute les evennements dessus
		mode1.setOnClickListener(this);
		mode2.setOnClickListener(this);
		scores.setOnClickListener(this);
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
		case R.id.mode1:
			startActivity(new Intent("com.inf8405.bejeweled.MODE1"));
			break;

		case R.id.mode2:
			startActivity(new Intent("com.inf8405.bejeweled.MODE2"));
			break;

		case R.id.scores:
			startActivity(new Intent("com.inf8405.bejeweled.LEADERBOARD"));
			break;

		case R.id.quit:
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
		finish();
		return;
	}
}
