package cc.pchospital.app.util;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cc.pchospital.app.R;

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

        ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            cardID = view.findViewById(R.id.card_id);
            cardDate = view.findViewById(R.id.card_date);
            cardNote = view.findViewById(R.id.card_notes);
            cardStates = view.findViewById(R.id.card_states);
            cardLocation = view.findViewById(R.id.card_location);
        }
    }

    public CardAdapter(List<Card> cards) {
        this.cards = cards;
        labelDictionary = new HashMap<>();
        colorDictionary = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context)
                .inflate(R.layout.main_card_item, parent, false);
        // 初始化 HashMap
        initHashMaps();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardID.setText(card.getTicketID());
        holder.cardDate.setText(card.getTicketDate());
        holder.cardLocation.setText(card.getTicketLocation());
        holder.cardNote.setText(card.getTicketNote());
        holder.cardStates.setTextColor(colorDictionary.get(card.getTicketStates()));
        holder.cardStates.setText(labelDictionary.get(card.getTicketStates()));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    private void initHashMaps() {
        // label
        labelDictionary.put(
                context.getString(R.string.app_ticket_states_uploading),
                context.getString(R.string.label_ticket_uploading)
        );
        labelDictionary.put(
                context.getString(R.string.app_ticket_states_uploaded),
                context.getString(R.string.label_ticket_uploaded)
        );
        labelDictionary.put(
                context.getString(R.string.app_ticket_states_unread),
                context.getString(R.string.label_ticket_unread)
        );
        labelDictionary.put(
                context.getString(R.string.app_ticket_states_noticed),
                context.getString(R.string.label_ticket_noticed)
        );
        labelDictionary.put(
                context.getString(R.string.app_ticket_states_accepted),
                context.getString(R.string.label_ticket_accepted)
        );
        labelDictionary.put(
                context.getString(R.string.app_ticket_states_complete),
                context.getString(R.string.label_ticket_complete)
        );
        labelDictionary.put(
                context.getString(R.string.app_ticket_states_canceled),
                context.getString(R.string.label_ticket_canceled)
        );
        labelDictionary.put(
                context.getString(R.string.app_ticket_states_failed),
                context.getString(R.string.label_ticket_upload_failed)
        );

        // color
        colorDictionary.put(
                context.getString(R.string.app_ticket_states_uploading),
                R.color.textDefault
        );
        colorDictionary.put(
                context.getString(R.string.app_ticket_states_uploaded),
                R.color.textDefault
        );
        colorDictionary.put(
                context.getString(R.string.app_ticket_states_unread),
                R.color.States_unread
        );
        colorDictionary.put(
                context.getString(R.string.app_ticket_states_noticed),
                R.color.States_noticed
        );
        colorDictionary.put(
                context.getString(R.string.app_ticket_states_accepted),
                R.color.States_accepted
        );
        colorDictionary.put(
                context.getString(R.string.app_ticket_states_complete),
                R.color.States_complete
        );
        colorDictionary.put(
                context.getString(R.string.app_ticket_states_canceled),
                R.color.States_canceled
        );
        colorDictionary.put(
                context.getString(R.string.app_ticket_states_failed),
                R.color.States_failed
        );
    }
}
