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

    private static final int TYPE_ADD_TICKET = 1;

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
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.mainCard);
        cardAdapter = new CardAdapter(cards);
        recyclerView.setAdapter(cardAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTicketActivity.class);
                startActivityForResult(intent, TYPE_ADD_TICKET);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TYPE_ADD_TICKET:
                if (resultCode == RESULT_OK) {

                }
                break;
        }
    }

    private void initCards() {
        cards.add(new Card(getString(R.string.label_main_ticket_num), 5));
        cards.add(new Card(getString(R.string.label_main_active_staff), 5));
    }

}
