package com.example.eventplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventplanner.adapters.Top5EventsAdapter;
import com.example.eventplanner.models.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Top5EventsFragment extends Fragment {

    private RecyclerView top5EventsRecyclerView;
    private List<Event> top5EventsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        top5EventsList = new ArrayList<>();
        top5EventsList.add(new Event("Event 1", "Description for Event 1", R.drawable.event_placeholder));
        top5EventsList.add(new Event("Event 2", "Description for Event 2", R.drawable.event_placeholder2));
        top5EventsList.add(new Event("Event 3", "Description for Event 3", R.drawable.event_placeholder));
        top5EventsList.add(new Event("Event 4", "Description for Event 4", R.drawable.event_placeholder3));
        top5EventsList.add(new Event("Event 5", "Description for Event 5", R.drawable.event_placeholder3));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_5_events_carousel, container, false);

        top5EventsRecyclerView = view.findViewById(R.id.top_5_events_recycler_view);
        top5EventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        Top5EventsAdapter top5EventsAdapter = new Top5EventsAdapter(getContext(), top5EventsList);
        top5EventsRecyclerView.setAdapter(top5EventsAdapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(top5EventsRecyclerView);

        top5EventsAdapter.setSnapHelper(snapHelper);
        top5EventsAdapter.setRecyclerView(top5EventsRecyclerView);

        top5EventsRecyclerView.post(() -> top5EventsRecyclerView.scrollToPosition(0));

        top5EventsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View centerView = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (centerView != null) {
                    int centerPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(centerView);

                    for (int i = 0; i < recyclerView.getChildCount(); i++) {
                        View child = recyclerView.getChildAt(i);
                        if (child != null) {
                            float scaleFactor = (recyclerView.getChildAdapterPosition(child) == centerPosition) ? 1.0f : 0.8f;
                            child.setScaleX(scaleFactor);
                            child.setScaleY(scaleFactor);
                        }
                    }
                }
            }
        });

        return view;
    }
}