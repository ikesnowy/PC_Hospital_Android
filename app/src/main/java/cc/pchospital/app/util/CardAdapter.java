package cc.pchospital.app.util;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cc.pchospital.app.R;

/**
 * Created by ikesn on 2017/10/14.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private Context context;
    private List<Card> cards;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cardLabel;
        TextView cardText;

        public ViewHolder (View view){
            super(view);
            cardView = (CardView) view;
            cardLabel = (TextView) view.findViewById(R.id.cardLabel);
            cardText = (TextView) view.findViewById(R.id.cardMainText);
        }
    }

    public CardAdapter(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.main_card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardText.setText(Integer.toString(card.getNumber()));
        holder.cardLabel.setText(card.getLabel());
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
