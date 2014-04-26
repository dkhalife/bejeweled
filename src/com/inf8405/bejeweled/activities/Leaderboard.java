package com.inf8405.bejeweled.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.inf8405.bejeweled.R;
import com.inf8405.bejeweled.core.Highscores;

/**
 * Cette classe represente notre table de statistiques
 */
public class Leaderboard extends Activity {

	private final int SIZE_MAX = 10;
	ListView listScores;
	ListView listHeader;
	ListAdapter adapter_title;
	ListAdapter adapter_scores;

	/**
	 * Cette methode est execute lorsque l'activite est creee
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard);

		// On charge la liste de scores
		HashMap<String, Integer> map = Highscores.getInstance(getApplicationContext()).getScores();
		Map<String, Integer> scores = Highscores.getInstance(getApplicationContext()).getSortedScores(map);

		// On obtient l'element qui va afficher la liste de scores
		listScores = (ListView) findViewById(R.id.scoresList);
		listHeader = (ListView) findViewById(R.id.listHeader);
		showActivity(scores);
	}

	public void showActivity(Map<String, Integer> scores) {

		// ajouter les titres
		List<Map<String, String>> list_title = new ArrayList<Map<String, String>>();
		Map<String, String> map_title = new HashMap<String, String>();
		map_title.put("rank", "Rang");
		map_title.put("name", "Nom");
		map_title.put("points", "Points");
		list_title.add(map_title);
		adapter_title = new SimpleAdapter(this, list_title, R.layout.row, new String[] { "rank", "name", "points" }, new int[] { R.id.rank, R.id.name, R.id.points });
		listHeader.setAdapter(adapter_title);
		listHeader.setFocusable(false);
		listHeader.setFocusableInTouchMode(false);

		// ajouter les scores
		int count = 1;
		int size = Math.min(SIZE_MAX, scores.size());
		List<Map<String, String>> list_scores = new ArrayList<Map<String, String>>();
		Iterator<Map.Entry<String, Integer>> it = scores.entrySet().iterator();
		while (it.hasNext() && count <= size) {
			Map<String, String> map_score = new TreeMap<String, String>();
			Map.Entry<String, Integer> entry = it.next();
			String name = entry.getKey();
			String score = entry.getValue().toString();
			map_score.put("rank", String.valueOf(count));
			map_score.put("name", name);
			map_score.put("points", score);
			list_scores.add(map_score);
			count++;
		}
		adapter_scores = new SimpleAdapter(this, list_scores, R.layout.row, new String[] { "rank", "name", "points" }, new int[] { R.id.rank, R.id.name, R.id.points });
		listScores.setAdapter(adapter_scores);
		listScores.setFocusable(false);
		listScores.setFocusableInTouchMode(false);
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
