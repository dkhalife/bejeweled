package com.inf8405.bejeweled.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;

import com.inf8405.bejeweled.R;

/**
 * Cette classe represente le coeur du jeu
 */
public class Game {
	// Les differents modes de jeu
	public enum MODE {
		STRATEGY, TIME_TRIAL
	}

	// Les differents etats du jeu
	public enum STATE {
		COMBO_ANIMATION, COMBO_VALIDATION_LOOP, COUNTDOWN, GAME_OVER_ANIMATION, GAME_OVER_END, GAME_OVER_START, GAME_PAUSED, GEMS_FALLING, SWAP_ANIMATION, USER_INPUT
	}

	// La vitesse de changement d'opacite
	public static int FADE_SPEED = 15;
	// Le dernier mode de jeu
	public static MODE last_mode;
	// Le dernier score
	public static int last_score;
	// Le nombre maximal de moves
	public static final int MAX_MOVES = 10;
	// Le temps maximal pour joueur
	public static final long MAX_TIME = 60000;
	// Les differentes textures
	public static Bitmap original_tex_black, original_tex_select, original_tex_blue, original_tex_green, original_tex_orange, original_tex_purple, original_tex_red, original_tex_white, original_tex_yellow, original_tex_home,
			original_tex_restart, original_tex_close;
	// La taille de la grille
	public static final int SIZE = 8;

	// La taille des carres en dp
	public static int SQUARE_SIZE = 10;

	public static Bitmap tex_black, tex_select, tex_blue, tex_green, tex_orange, tex_purple, tex_red, tex_white, tex_yellow, tex_home, tex_restart, tex_close;;

	// L'activite qui a lance le jeu
	private Activity activity;

	// Le nombre de chaines formées
	private int chains = 0;
	// La valeur du countdown
	private float countdown = 4000.0f;
	boolean dragging = false;
	// Le temps ecoulé dans le jeu
	private double elapsed_time = 0;
	private long gameOverTime = 0;
	// Les gems selectionnes
	private Gem gem_start, gem_end;
	// Les Gems
	private Gem[] gems = new Gem[SIZE * SIZE];
	// Temps auquel s'est fait la derniere mise a jour
	private long last_update = 0;
	// Le type de jeu
	private MODE mode;
	// Le nombre de moves que le joueur a fait
	private int moves = 0;
	// Le point destination pour l'animation du premier gem
	private float moveToX = 0, moveToY = 0;
	// Le joueur actuel
	private Player player = null;

	// Le score total du joueur
	private int score = 0;

	// Les scores a afficher sur l'ecran
	private ArrayList<Score> scores = new ArrayList<Score>();

	// L'etat couratnt du jeu
	public STATE state = STATE.COUNTDOWN;
	// Est-ce que le swap a ete effectue
	private boolean swap_revert = false;

	/**
	 * Constructeur par defaut
	 */
	public Game(MODE mode, Activity activity) {
		this.mode = mode;
		this.activity = activity;
		player = new Player();
		for (int i = 0; i < SIZE; i += 1) {
			for (int j = 0; j < SIZE; j += 1) {
				gems[i * SIZE + j] = new Gem(i * SQUARE_SIZE, j * SQUARE_SIZE);
			}
		}

		Resources resources = activity.getResources();
		original_tex_black = BitmapFactory.decodeResource(resources, R.drawable.black);
		original_tex_select = BitmapFactory.decodeResource(resources, R.drawable.select);
		original_tex_blue = BitmapFactory.decodeResource(resources, R.drawable.gem_blue);
		original_tex_green = BitmapFactory.decodeResource(resources, R.drawable.gem_green);
		original_tex_orange = BitmapFactory.decodeResource(resources, R.drawable.gem_orange);
		original_tex_purple = BitmapFactory.decodeResource(resources, R.drawable.gem_purple);
		original_tex_red = BitmapFactory.decodeResource(resources, R.drawable.gem_red);
		original_tex_white = BitmapFactory.decodeResource(resources, R.drawable.gem_white);
		original_tex_yellow = BitmapFactory.decodeResource(resources, R.drawable.gem_yellow);
		original_tex_home = BitmapFactory.decodeResource(resources, R.drawable.home);
		original_tex_restart = BitmapFactory.decodeResource(resources, R.drawable.restart);
		original_tex_close = BitmapFactory.decodeResource(resources, R.drawable.close);
	}

	/**
	 * Cette methode permet de verifier si un combo est valide
	 */
	private boolean checkCombo(ArrayList<Gem> combo) {
		int size = combo.size();
		return (size >= 3);
	}

	/**
	 * Cette methode permet de verifier un swap
	 * 
	 * @param x L'abscisse final (ou celle du second gem)
	 * @param y L'ordonnee final (ou celle du second gem)
	 */
	private void checkSwap(float x, float y) {
		gem_end = getGemAt(x, y);

		if (gem_end != null) {
			float dx = Math.abs(gem_end.x - gem_start.x);
			float dy = Math.abs(gem_end.y - gem_start.y);

			// Vertical or Horizontal but not both
			if ((dx == SQUARE_SIZE && dy == 0) || (dx == 0 && dy == SQUARE_SIZE)) {
				// Valid move will cause gems to animate
				moveToX = gem_end.x;
				moveToY = gem_end.y;
				state = STATE.SWAP_ANIMATION;
			} else {
				// Invalid move (Far, no animation)
				gem_start = gem_end = null;
			}
		}
	}

	/**
	 * Cette methode permet de fermer l'application
	 */
	private void close() {
		final STATE previousState = state;
		state = STATE.GAME_PAUSED;

		new AlertDialog.Builder(activity).setMessage("Etes vous sur de vouloir quitter l'application?").setCancelable(false).setPositiveButton("Oui", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.finish();
			}
		}).setNegativeButton("Non", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				state = previousState;
			}
		}).show();
	}

	/**
	 * Methode qui s'occupe de faire animer les gems pendant un combo
	 */
	private void comboAnimation() {
		ArrayList<ArrayList<Gem>> combos = getCombos();
		HashSet<Gem> distintGems = getDistinctGems(combos);
		boolean hasVisible = false;
		for (Gem j : distintGems) {
			if (j.getOpacity() > 0)
				hasVisible = true;
			j.setOpacity(j.getOpacity() - FADE_SPEED);
		}

		if (hasVisible == false) {
			for (Gem j : distintGems)
				j.fakeDestroy();
			state = STATE.GEMS_FALLING;
		}
	}

	/**
	 * Methode qui met a jour les combos suite a la rencontre d'un gem qui brise
	 * une chaine de la meme couleur.
	 * 
	 * @param gem Le gem sur lequel on se situe
	 * @param combo Le combo courant
	 * @param combos Tous les combos
	 */
	private void comboBreaker(Gem gem, ArrayList<Gem> combo, ArrayList<ArrayList<Gem>> combos) {
		// Combo breaker
		if (checkCombo(combo)) {
			combos.add(new ArrayList<Gem>(combo));
		}

		// Start a new combo
		combo.clear();
		if (gem != null)
			combo.add(gem);
	}

	/**
	 * Methode qui valide les combos
	 */
	private void comboValidation() {
		ArrayList<ArrayList<Gem>> combos = getCombos();
		if (combos.size() > 0) {
			createScore(combos);
			chains += combos.size();
			state = STATE.COMBO_ANIMATION;
		} else {
			state = STATE.USER_INPUT;
		}
	}

	/**
	 * Cette methode s'occupe de faire un countdown
	 * 
	 * @param delta Le temps ecoule depuis le dernier appel
	 */
	private void countDown() {
		if (last_update == 0) {
			last_update = System.currentTimeMillis();
		} else {
			long time = System.currentTimeMillis();
			// J'ai mis le countdown 1.5 fois plus rapide
			// pcq je trouvais que c'etait long attendre 3 vrai secondes
			countdown -= 1.5f * (time - last_update);
			last_update = time;

			if (countdown <= 0) {
				countdown = 4000;
				state = STATE.COMBO_VALIDATION_LOOP;
			}
		}
	}

	/**
	 * Methode qui cree les scores pour chaque combo
	 * 
	 * @param combos Les scores obtenus
	 */
	private void createScore(ArrayList<ArrayList<Gem>> combos) {
		for (ArrayList<Gem> combo : combos) {
			int value = 100 + (combo.size() - 3) * 50;
			score += value;
			Score s = new Score(combo, value);
			scores.add(s);
		}
	}

	/**
	 * Cette methode est executee lorsque le jeu prend fin
	 */
	private void gameOverAnimation() {
		boolean hasVisible = false;
		for (int i = 0; i < gems.length; i++) {
			int new_op = gems[i].getOpacity() - i % FADE_SPEED / 2 - 3;
			gems[i].setOpacity(new_op);
			hasVisible |= (new_op > 0);
		}

		if (!hasVisible) {
			state = STATE.GAME_OVER_END;
			gameOverTime = System.currentTimeMillis();
		}
	}

	/**
	 * 
	 */
	private void gameOverEnd() {
		long now = System.currentTimeMillis();

		// On attend 2 secondes avant de rediriger l'utilisateur
		if (now - gameOverTime > 2000) {
			Highscores.getInstance(activity).addScore(player.getName(), player.getScore());
			activity.startActivity(new Intent("com.inf8405.bejeweled.GAME_OVER"));
			activity.finish();
		}
	}

	private void gameOverStart() {
		state = STATE.GAME_OVER_ANIMATION;

		// On sauvegarde le tout avant l'animation comme ca si
		// l'utilisateur est impatient
		// il ne perdera pas son score
		last_mode = mode;
		last_score = score;
		player.setScore(last_score);
	}

	/**
	 * Methode qui s'occupe de faire tomber les gems
	 * 
	 * @param delta
	 */
	private void gemsFalling(float delta) {
		for (Gem j : gems) {
			j.fall(this, delta);
		}

		if (gemsStill()) {
			state = STATE.COMBO_VALIDATION_LOOP;
		}
	}

	/**
	 * Methode d'acces a l'activite
	 * 
	 * @return
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * Cette methode verifie s'il y a des combinaisons formees
	 */
	public ArrayList<ArrayList<Gem>> getCombos() {
		// Si les Gems sont en mouvement on arrete
		if (!gemsStill()) {
			Log.e("Game", "Error getting combos while gems are moving");
			return null;
		}

		ArrayList<ArrayList<Gem>> combos = new ArrayList<ArrayList<Gem>>();
		ArrayList<Gem> combo = new ArrayList<Gem>();
		Gem[][] gemArr = getGemArray();

		// Vertical lines
		for (int i = 0; i < Game.SIZE; i += 1) {
			for (int j = 0; j < Game.SIZE; j += 1) {
				updateCombos(gemArr[i][j], combo, combos);
			}
			comboBreaker(null, combo, combos);
		}

		// Horizontal lines
		for (int j = 0; j < Game.SIZE; j += 1) {
			for (int i = 0; i < Game.SIZE; i += 1) {
				updateCombos(gemArr[i][j], combo, combos);
			}
			comboBreaker(null, combo, combos);
		}

		return combos;
	}

	/**
	 * Methode pour obtenir les Gems distincts a partir des combos
	 * 
	 * @param combos Les combos obtenus
	 * @return Les gems distincts
	 */
	private HashSet<Gem> getDistinctGems(ArrayList<ArrayList<Gem>> combos) {
		if (combos == null)
			return null;
		HashSet<Gem> distinctGems = new HashSet<Gem>();
		for (ArrayList<Gem> combo : combos)
			distinctGems.addAll(combo);
		return distinctGems;
	}

	/**
	 * Methode pour obtenir les Gems en un tableau 2D
	 * 
	 * @return
	 */
	public Gem[][] getGemArray() {
		Gem[][] gemArray = new Gem[SIZE][SIZE];
		int i, j;
		for (Gem g : gems) {
			i = Math.round(g.x / SQUARE_SIZE);
			j = Math.round(g.y / SQUARE_SIZE);

			if (i >= 0 && i < SIZE && j >= 0 && j < SIZE)
				gemArray[i][j] = g;
		}
		return gemArray;
	}

	/**
	 * Cette methode permet d'obtenir un Gem en fonction des coordonnees
	 * 
	 * @param x L'abscisse
	 * @param y L'ordonnee
	 * @return Le Gem correspondant (ou null dans le cas echeant)
	 */
	public Gem getGemAt(float x, float y) {
		for (Gem j : gems) {
			if (j.collidseWith(x, y)) {
				return j;
			}
		}

		return null;
	}

	/**
	 * Methode d'acces au joueur
	 * 
	 * @return Le joueur actuel
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Cette methode permet d'aller au menu principal
	 */
	private void goToMenu() {
		final STATE previousState = state;
		state = STATE.GAME_PAUSED;

		new AlertDialog.Builder(activity).setMessage("Etes vous sur de retourner au menu principal?").setCancelable(true).setPositiveButton("Oui", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.startActivity(new Intent("com.inf8405.bejeweled.MAIN"));
				activity.finish();
			}

		}).setNegativeButton("Non", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				state = previousState;
			}
		}).show();
	}

	/**
	 * Cette methode verifie si les Gems sont en mouvement
	 * 
	 * @return True si tous les Gems sont immobiles
	 */
	private boolean gemsStill() {
		for (Gem g : gems)
			if (g != null && g.getSpeed() > 0)
				return false;
		return true;
	}

	/**
	 * Cette methode est executee lors d'un evennement tactile
	 * 
	 * @param event L'evennement qui a eu lieu
	 * @return Vrai si l'evennement est valide
	 */
	public boolean onTouch(MotionEvent event) {
		if (state == STATE.GAME_PAUSED) {
			return false;
		}

		float x = event.getX() - Canvas.PADDING;
		float y = event.getY() - 2 * SQUARE_SIZE;
		final int action = event.getAction() & MotionEvent.ACTION_MASK;

		// Possible touch on buttons
		if (y < 0 && action == MotionEvent.ACTION_DOWN) {
			int button = (int) (x / SQUARE_SIZE) - (SIZE - 3);

			if (button == 0) {
				goToMenu();
			} else if (button == 1) {
				retry();
			} else if (button == 2) {
				close();
			}

			// Pas un evennement de drag
			return false;
		}

		// Prevent user to make a move while the gems are animating
		// in any way
		if (state != STATE.USER_INPUT) {
			return false;
		}

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				if (gem_start == null) {
					gem_start = getGemAt(x, y);
				} else if (gem_end == null) {
					checkSwap(x, y);
				}
			break;

			case MotionEvent.ACTION_MOVE:
				if (gem_start != null) {
					gem_end = getGemAt(x, y);

					if (gem_end != null) {
						float dx = Math.abs(gem_end.x - gem_start.x);
						float dy = Math.abs(gem_end.y - gem_start.y);

						if ((dx == SQUARE_SIZE && dy == 0) || (dx == 0 && dy == SQUARE_SIZE)) {
							dragging = true;
						} else {
							gem_end = null;
						}
					}
				}
			break;

			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_UP:
				if (dragging) {
					checkSwap(x, y);
				}

				dragging = false;
			break;
		}

		// Return true to have the continuous touch event
		return true;
	}

	/**
	 * Cette methode met a jour l'affichage
	 * 
	 * @param canvas Le canvas dans lequel se fait l'affichage
	 */
	public long render(android.graphics.Canvas canvas) {
		long t = System.currentTimeMillis();

		canvas.drawColor(Color.argb(0, 0, 0, 1), PorterDuff.Mode.CLEAR);

		canvas.save();

		// Translate to gems position
		canvas.translate(0, 2 * SQUARE_SIZE);

		// Draw all gems
		for (Gem gem : gems) {
			Canvas.paint.setAlpha(gem.getOpacity());
			gem.draw(canvas);
		}

		// Draw selectors
		if (gem_start != null)
			canvas.drawBitmap(tex_select, gem_start.x, gem_start.y, Canvas.paint);
		if (gem_end != null)
			canvas.drawBitmap(tex_select, gem_end.x, gem_end.y, Canvas.paint);

		Canvas.paint.setTextAlign(Paint.Align.CENTER);

		// Draw scores in their respective opacity
		Canvas.paint.setColor(Color.WHITE);
		for (Score score : scores) {
			score.draw(canvas);
		}

		// Reset opacity
		Canvas.paint.setAlpha(255);

		// Draw black boxes to hide top gems
		canvas.translate(0, -SQUARE_SIZE);
		for (int i = 0; i <= SIZE; i += 1) {
			canvas.drawBitmap(tex_black, (float) i * SQUARE_SIZE, 0.0f, Canvas.paint);
		}

		// Translate back up
		canvas.translate(0, -0.5f * SQUARE_SIZE);

		Canvas.paint.setTextAlign(Paint.Align.LEFT);

		// Draw current score
		canvas.drawText("Score: " + score, 0, 0, Canvas.paint);
		canvas.translate(0, 1.5f * Canvas.FONT_SIZE);

		// Draw best score
		canvas.drawText("Chaines: " + chains, 0, 0, Canvas.paint);
		canvas.translate(0, 1.5f * Canvas.FONT_SIZE);

		// Draw moves or time
		if (mode == MODE.TIME_TRIAL) {
			double rem = (MAX_TIME - elapsed_time) / 1000;

			if (rem < 10.0) {
				// En bas de 10 secondes, la couleur du temps restant
				// flashera en rouge
				int s = (int) (255 - (255.0f / 1000.0f) * ((MAX_TIME - elapsed_time) % 1000));
				Canvas.paint.setARGB(255, 255, s, s);
			}

			canvas.drawText("Temps restant: " + rem, 0, 0, Canvas.paint);
		} else {
			canvas.drawText("Permutations restantes: " + (MAX_MOVES - moves), 0, 0, Canvas.paint);
		}

		// Restore color to white
		Canvas.paint.setARGB(255, 255, 255, 255);

		// Draw buttons
		canvas.translate((SIZE - 3) * SQUARE_SIZE, -1.5f * SQUARE_SIZE);
		canvas.drawBitmap(tex_home, 0, 0, Canvas.paint);
		canvas.drawBitmap(tex_restart, SQUARE_SIZE, 0, Canvas.paint);
		canvas.drawBitmap(tex_close, 2 * SQUARE_SIZE, 0, Canvas.paint);

		switch (state) {
			case COUNTDOWN:
				// On dessine le countdown
				int second = ((int) Math.ceil(countdown / 1000.0f)) - 1;
				canvas.translate(-1.0f * SQUARE_SIZE, SQUARE_SIZE * (2.0f + ((float) SIZE) / 2.0f));

				// Le scale commence a 4 et atteindra un maximum de 10
				// dependemment des millisecondes qui restent pour atteindre
				// la prochaine seconde
				float countdownScale = 14.0f - ((countdown % 1000) / 100.0f);
				canvas.scale(countdownScale, countdownScale);
				Canvas.paint.setTextAlign(Paint.Align.CENTER);

				if (second > 0) {
					canvas.drawText(second + "", 0, 0, Canvas.paint);
				} else {
					canvas.drawText("GO", 0, 0, Canvas.paint);
				}
			break;

			case GAME_OVER_END:
				Canvas.paint.setTextAlign(Paint.Align.CENTER);
				canvas.translate(-1.0f * SQUARE_SIZE, SQUARE_SIZE * (2.0f + ((float) SIZE) / 2.0f));
				canvas.scale(3, 3);
				canvas.drawText("Game Over", 0, 0, Canvas.paint);
			break;

			default:
			break;
		}

		canvas.restore();

		return System.currentTimeMillis() - t;
	}

	/**
	 * Methode qui permet d'initialiser un jeu
	 * 
	 * @param start Est-ce qu'il faut mettre le jeu en pause
	 */
	public void resetGame(boolean pause) {
		// On construit la grille
		for (int i = 0; i < SIZE; i += 1) {
			for (int j = 0; j < SIZE; j += 1) {
				gems[i * SIZE + j].reset(i * SQUARE_SIZE, j * SQUARE_SIZE);
			}
		}

		// Algorithm to remove initial combos
		ArrayList<ArrayList<Gem>> combos = getCombos();
		while (combos.size() > 0) {
			for (Gem j : getDistinctGems(combos))
				j.setColor(Gem.getRandColor());
			combos = getCombos();
			// System.out.println("reset combo");
		}

		// Necessaire pour eliminer les scores pendant l'algorithm de detection
		// des combos initiaux
		scores.clear();
		score = 0;
		chains = 0;
		moves = 0;
		elapsed_time = last_update = 0;
		state = pause ? STATE.GAME_PAUSED : STATE.COUNTDOWN;
	}

	/**
	 * Cette methode permet de recommencer le jeu
	 */
	private void retry() {
		final STATE previousState = state;
		state = STATE.GAME_PAUSED;

		new AlertDialog.Builder(activity).setMessage("Etes vous sur de vouloir recommencer le jeu?").setCancelable(false).setPositiveButton("Oui", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				resetGame(false);
			}
		}).setNegativeButton("Non", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				state = previousState;
			}
		}).show();
	}

	/**
	 * Methode de modification du nom de joueur
	 * 
	 * @param playerName
	 */
	public void setPlayerName(String playerName) {
		player.setName(playerName);
	}

	/**
	 * Methode qui s'occupe de l'animation de Gem
	 */
	private void swapAnimation() {
		ArrayList<ArrayList<Gem>> combos;
		float dx = moveToX - gem_start.x;
		float dy = moveToY - gem_start.y;
		float threshold = Gem.SPEED_BOOST / 2;

		if (Math.abs(dx) < threshold && Math.abs(dy) < threshold) {
			gem_end.snapPosition();
			gem_start.snapPosition();

			// On valide le move
			combos = getCombos();

			if (combos.size() == 0) {
				if (!swap_revert) {
					// On revient a la position initiale
					moveToX = gem_end.x;
					moveToY = gem_end.y;
					swap_revert = true;
				} else {
					gem_start = gem_end = null;
					state = STATE.USER_INPUT;
					swap_revert = false;
				}
			} else {
				++moves;
				gem_end.snapPosition();
				gem_start.snapPosition();
				gem_start = gem_end = null;
				state = STATE.GEMS_FALLING;
			}

			return;
		}

		// Move horizontal
		if (dx != 0) {
			// dx = SIGN * SPEED
			dx = (dx / Math.abs(dx)) * Gem.SPEED_BOOST;
			gem_start.x = gem_start.x + dx;
			gem_end.x = gem_end.x - dx;
		}
		// Move vertical
		else if (dy != 0) {
			dy = (dy / Math.abs(dy)) * Gem.SPEED_BOOST;
			gem_start.y = gem_start.y + dy;
			gem_end.y = gem_end.y - dy;
		} else {
			gem_start = gem_end = null;
			state = STATE.COMBO_VALIDATION_LOOP;
		}
	}

	/**
	 * Cette methode met a jour l'espace du jeu
	 * 
	 * @param delta Le temps ecoule depuis le dernier appel
	 */
	public void update(float delta) {
		if (state == STATE.GAME_PAUSED)
			return;
		if (state == STATE.COUNTDOWN) {
			countDown();
			return;
		}

		switch (mode) {
			case TIME_TRIAL:
				updateTimeTrial();
			break;
			case STRATEGY:
				if (moves >= MAX_MOVES && state == STATE.USER_INPUT)
					state = STATE.GAME_OVER_START;
			break;
			default:
			break;
		}

		updateScores();

		switch (state) {
			case COMBO_VALIDATION_LOOP:
				comboValidation();
			break;

			case COMBO_ANIMATION:
				comboAnimation();
			break;

			case GEMS_FALLING:
				gemsFalling(delta);
			break;

			case SWAP_ANIMATION:
				swapAnimation();
			break;

			case GAME_OVER_START:
				gameOverStart();
				// Pas de break expres!
			case GAME_OVER_ANIMATION:
				gameOverAnimation();
			break;

			case GAME_OVER_END:
				gameOverEnd();
			break;

			default:
			break;
		}
	}

	/**
	 * Cette methode permet de verifier s'il y a des combinaisons aux alentours
	 * d'un Gem
	 * 
	 * @param gem Le Gem en question
	 */
	private void updateCombos(Gem gem, ArrayList<Gem> combo, ArrayList<ArrayList<Gem>> combos) {

		if (gem != null && combo.size() > 0 && gem.getColor() == combo.get(0).getColor()) {
			// Continue saving line
			combo.add(gem);
		} else {
			comboBreaker(gem, combo, combos);
		}
	}

	/**
	 * Methode de mise a jour des scores déssinés à l'écran pour les combos
	 */
	private void updateScores() {
		Iterator<Score> iter = scores.iterator();
		while (iter.hasNext()) {
			Score s = iter.next();
			if (!s.update())
				iter.remove();
		}
	}

	/**
	 * Methode de mise a jour du temps
	 */
	private void updateTimeTrial() {
		if (last_update == 0) {
			last_update = System.currentTimeMillis();
		} else {
			long time = System.currentTimeMillis();
			elapsed_time += time - last_update;
			last_update = time;

			if (elapsed_time >= MAX_TIME) {
				elapsed_time = MAX_TIME;

				if (state == STATE.USER_INPUT) {
					state = STATE.GAME_OVER_START;
				}
			}
		}
	}
}
