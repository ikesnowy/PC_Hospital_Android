package cc.pchospital.app;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cc.pchospital.app.util.Card;
import cc.pchospital.app.util.CardAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private List<Card> cards = new ArrayList<>();
    private CardAdapter cardAdapter;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initCards();
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.mainCard);
        cardAdapter = new CardAdapter(cards);
        recyclerView.setAdapter(cardAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void initCards() {
        cards.add(new Card(getString(R.string.label_main_ticket_num), 5));
        cards.add(new Card(getString(R.string.label_main_active_staff), 5));
    }

}
