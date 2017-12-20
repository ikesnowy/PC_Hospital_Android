package cc.pchospital.app.util;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import cc.pchospital.app.R;
import cc.pchospital.app.TicketDetailActivity;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Context context;
    private List<Card> cards;
    private static HashMap<String, String> labelDictionary;
    private static HashMap<String, Integer> colorDictionary;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cardID;
        TextView cardDate;
        TextView cardNote;
        TextView cardStates;
        TextView cardLocation;
        ImageView cardPicture;

        ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            cardID = view.findViewById(R.id.card_id);
            cardDate = view.findViewById(R.id.card_date);
            cardNote = view.findViewById(R.id.card_notes);
            cardStates = view.findViewById(R.id.card_states);
            cardLocation = view.findViewById(R.id.card_location);
            cardPicture = view.findViewById(R.id.card_picture);
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
        View view = LayoutInflater.from(context)
                .inflate(R.layout.main_card_item, parent, false);
        // 初始化 HashMap
        labelDictionary = DictionaryUtil.initLabelHashMaps(context);
        colorDictionary = DictionaryUtil.initColorHashMap(context);

        final ViewHolder holder = new ViewHolder(view);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                TicketDetailActivity.actionStart(context, cards.get(position).getTicketID());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardID.setText(card.getTicketID());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(card.getTicketDate());
        holder.cardDate.setText(date);
        holder.cardLocation.setText(card.getTicketLocation());
        holder.cardNote.setText(card.getTicketNote());
        holder.cardStates.setTextColor(colorDictionary.get(card.getTicketStates()));
        holder.cardStates.setText(labelDictionary.get(card.getTicketStates()));
        if (card.getTicketIcon() != null) {
            Glide.with(context).load(card.getTicketIcon()).into(holder.cardPicture);
        } else {
            holder.cardPicture.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}
