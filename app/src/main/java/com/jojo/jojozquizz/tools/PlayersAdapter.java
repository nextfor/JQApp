package com.jojo.jojozquizz.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jojo.jojozquizz.R;
import com.jojo.jojozquizz.model.Player;

import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.UsersViewHolder> {

	private Context context;
	private int[] mIds;
	private String[] mName;
	private long[] mScore;

	private List<Player> mPlayers;

	public PlayersAdapter(Context ct, List<Player> players) {
		this.context = ct;
		this.mPlayers = players;
	}

	@NonNull
	@Override
	public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.users_recycler_layout, parent, false);
		return new UsersViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
		holder.updateWithPlayer(this.mPlayers.get(position));
	}

	public Player getUser(int position) {
		return this.mPlayers.get(position);
	}

	@Override
	public int getItemCount() {
		return this.mPlayers.size();
	}

	public class UsersViewHolder extends RecyclerView.ViewHolder {

		TextView textId, textName, textScore;
		ConstraintLayout mConstraintLayout;

		public UsersViewHolder(@NonNull View v) {
			super(v);

			mConstraintLayout = v.findViewById(R.id.user_layout);

			textName = v.findViewById(R.id.user_name);
			textScore = v.findViewById(R.id.user_score);
		}

		public void updateWithPlayer(Player player) {
			this.textName.setText(player.getName());
			this.textScore.setText(String.valueOf(player.getScore()));
		}
	}
}
