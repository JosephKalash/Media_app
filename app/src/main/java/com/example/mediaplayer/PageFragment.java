package com.example.mediaplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class PageFragment extends Fragment {
    private static final String FRAGMENT_INDEX = "index";

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<String> dummyData;

    public static PageFragment newInstance(int index) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putSerializable(FRAGMENT_INDEX, index);
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // TODO: dummy data here, watinig to be removed and replaced with model layer
        dummyData = new ArrayList<>();
        for (int i = 0; i < 100; i ++) {
            dummyData.add("HOLA" + i + 1);
        }
        return inflater.inflate(R.layout.fragment_page_view, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            int pageNumber = (int) getArguments().getSerializable(FRAGMENT_INDEX);
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //to avoid lack in scrolling
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerViewAdapter = new RecyclerViewAdapter(dummyData, getActivity());
        recyclerView.setAdapter(recyclerViewAdapter);
    }

}
