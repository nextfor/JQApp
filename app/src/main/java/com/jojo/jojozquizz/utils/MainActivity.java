package com.jojo.jojozquizz.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.jojo.jojozquizz.model.Player;

import java.util.HashMap;

public class MainActivity {

	private static final MainActivity instance = new MainActivity();

	private Context context;
	private Player player;
	private SharedPreferences preferences;
	private HashMap<String, String> args;

	private MainActivity() {
	}

	public static MainActivity getInstance() {
		return instance;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Player getPlayer() {
		return player;
	}

	public Player requirePlayer() {
		if (player == null) {
			throw new NullPointerException();
		} else {
			return player;
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public HashMap<String, String> getArgs() {
		if (args == null || args.isEmpty()) {
			return new HashMap<>();
		} else {
			return args;
		}
	}

	public void setArgs(HashMap<String, String> args) {
		this.args = args;
	}
}
