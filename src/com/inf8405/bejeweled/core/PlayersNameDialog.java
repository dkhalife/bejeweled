package com.inf8405.bejeweled.core;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inf8405.bejeweled.R;

/**
 * Cette classe s'occupe de demander a l'utilisateur son nom
 */
public class PlayersNameDialog extends Dialog implements View.OnClickListener {
	// Le widget ou l'utilisateur ecrit son nom
	private EditText playerName;
	// Le jeu en cours
	private Game game;
	// Le context android
	private Context context;

	/**
	 * Constructeur par parametres
	 * 
	 * @param context Le contexte android
	 * @param game Le jeu en cours
	 */
	public PlayersNameDialog(Context context, Game game) {
		super(context);

		setCancelable(false);
		setCanceledOnTouchOutside(false);
		this.context = context;
		this.game = game;
	}

	/**
	 * Cette methode est appelle suite a la creation du dialog
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.playersNameDialog_title);
		setContentView(R.layout.player_name);

		Button cancel = (Button) findViewById(R.id.cancel_button);
		Button confirm = (Button) findViewById(R.id.confirm_button);
		playerName = (EditText) findViewById(R.id.nameEditText);

		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	/**
	 * Cette methode est appelee suite a un click sur un des boutons
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cancel_button:
				// On redirige vers le menu principal
				game.getActivity().startActivity(new Intent("com.inf8405.bejeweled.MAIN"));
				game.getActivity().finish();
				cancel();
			break;

			case R.id.confirm_button:
				String name = playerName.getText().toString();
				if (name.length() > 12) {
					Toast.makeText(context, R.string.longPlayerName, Toast.LENGTH_SHORT).show();
				} else if(name.length() < 3) {
					Toast.makeText(context, R.string.shortPlayerName, Toast.LENGTH_SHORT).show();
				} else {
					game.setPlayerName(name);
					game.state = Game.STATE.COUNTDOWN;
					cancel();
				}
			break;
		}
	}
}
