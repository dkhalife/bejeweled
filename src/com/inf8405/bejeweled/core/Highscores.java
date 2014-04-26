package com.inf8405.bejeweled.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

/**
 * Cette classe permet de gerer les scores du jeu
 */
public class Highscores {
	// On garde une seule instance
	private static Highscores instance = null;

	// Les scores seront sauvegardes en fonction du nom du joueur
	private static HashMap<String, Integer> scores = null;

	// Le context Android
	private Context context;

	/**
	 * Constructeur par parametre
	 * 
	 * @param context Le contexte Android
	 * @param size La taille maximale de la liste
	 */
	private Highscores(Context context) {
		if (context != null)
			this.context = context;

		// On essaye de voir s'il y a des scores existants
		loadScores();

		// Si ce n'est pas le cas, on cree des scores
		if (scores == null) {
			scores = new HashMap<String, Integer>();
			
			// Ajout d'au moins 5 scores (requis fonctionnel)
			scores.put("Chuck Norris", 5000);
			scores.put("Linus Torvalds", 4000);
			scores.put("Julius Caesar", 3000);
			scores.put("Mona Lisa", 2000);
			scores.put("Omastar", 1000);
			
			saveScores();
		}
	}

	/**
	 * Methode pour obtenir l'instance unique
	 * 
	 * @param context Le context Android
	 * @return L'instance unique
	 */
	public static Highscores getInstance(Context context) {
		if (instance == null)
			instance = new Highscores(context);

		return instance;
	}

	/**
	 * Methode d'acces au scores
	 * 
	 * @return Les scores
	 */
	public HashMap<String, Integer> getScores() {
		return scores;
	}

	/**
	 * Cette methode trie une map en fonction des valeurs et non pas des cles
	 * 
	 * @param map La map a trier
	 * @return La map triee en fonction des valeurs
	 */
	public <K, V extends Comparable<V>> Map<K, V> getSortedScores(final Map<K, V> map) {
		// Ce comparateur fait la comparaison des cles
		Comparator<K> valueComparator = new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				if (compare == 0)
					return 1;
				else
					return compare;
			}
		};

		Map<K, V> sorted = new TreeMap<K, V>(valueComparator);
		sorted.putAll(map);

		return sorted;
	}

	/**
	 * Methode pour ajouter un score
	 * 
	 * @param name Le nom du joueur
	 * @param score Son score
	 */
	public void addScore(String name, Integer score) {
		// TODO: Garder juste les 10 premiers scores

		if (name.length() > 20) {
			name = name.substring(0, 19);
		}

		// Keep only the best score per name
		int best = 0;
		if (scores.containsKey(name)) {
			best = scores.get(name);
		}

		if (score > best) {
			scores.put(name, score);
		}

		saveScores();
	}

	/**
	 * Cette methode permet de sauvegarder les scores en memoire
	 */
	private void saveScores() {
		LocalStorage.writeObjectToFile(context, scores, "scores.dat");
	}

	/**
	 * Cette methode permet de charger les scores en memoire
	 */
	@SuppressWarnings("unchecked")
	public void loadScores() {
		Object object = LocalStorage.readObjectFromFile(context, "scores.dat");
		if (object instanceof HashMap<?, ?>) {
			scores = (HashMap<String, Integer>) object;
		}
	}
}