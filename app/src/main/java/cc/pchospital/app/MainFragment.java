package cc.pchospital.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cc.pchospital.app.util.Card;
import cc.pchospital.app.util.CardAdapter;

import static android.app.Activity.RESULT_OK;


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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCards();
        RecyclerView recyclerView = getView().findViewById(R.id.mainCard);
        cardAdapter = new CardAdapter(cards);
        recyclerView.setAdapter(cardAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initCards() {
        cards.add(new Card(1526, "2017/12/07", "学9 207", "求重装系统", getString(R.string.app_ticket_states_uploading)));
        cards.add(new Card(1526, "2017/12/07", "学9 207", "求重装系统", getString(R.string.app_ticket_states_uploaded)));
        cards.add(new Card(1526, "2017/12/07", "学9 207", "求重装系统", getString(R.string.app_ticket_states_unread)));
        cards.add(new Card(1526, "2017/12/07", "学9 207", "求重装系统", getString(R.string.app_ticket_states_noticed)));
        cards.add(new Card(1526, "2017/12/07", "学9 207", "求重装系统", getString(R.string.app_ticket_states_accepted)));
        cards.add(new Card(1526, "2017/12/07", "学9 207", "求重装系统", getString(R.string.app_ticket_states_complete)));
        cards.add(new Card(1526, "2017/12/07", "学9 207", "求重装系统", getString(R.string.app_ticket_states_failed)));
    }

}
