package com.example.eventplanner.fragments.homepage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.Top5EventsAdapter;
import com.example.eventplanner.databinding.Top5EventsCarouselBinding;
import com.example.eventplanner.viewmodels.Top5EventsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Top5EventsFragment extends Fragment {

    private Top5EventsAdapter adapter;
    private Top5EventsCarouselBinding binding;
    private Top5EventsViewModel viewModel;
    private List<ImageView> dots;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = Top5EventsCarouselBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(Top5EventsViewModel.class);
        viewModel.setContext(requireContext());

        if (binding != null) {
            setupRecyclerView();
        }

        observeViewModel();

        viewModel.fetchTop5Events();
    }

    private void setupRecyclerView() {
        adapter = new Top5EventsAdapter(getContext(), new ArrayList<>());

        binding.top5EventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.top5EventsRecyclerView.setAdapter(adapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.top5EventsRecyclerView);

        binding.top5EventsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                View centerView = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (centerView != null) {
                    int centerPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).getPosition(centerView);
                    updatePaginationDots(centerPosition);
                }
            }
        });
    }

    private void setupPaginationDots(int size) {
        dots = new ArrayList<>();
        binding.paginationDotsContainer.removeAllViews();

        for (int i = 0; i < size; i++) {
            ImageView dot = new ImageView(getContext());
            dot.setImageResource(R.drawable.inactive_dot);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            binding.paginationDotsContainer.addView(dot);
            dots.add(dot);
        }

        if (!dots.isEmpty()) {
            dots.get(0).setImageResource(R.drawable.active_dot);
        }
    }

    private void updatePaginationDots(int activePosition) {
        for (int i = 0; i < dots.size(); i++) {
            if (i == activePosition) {
                dots.get(i).setImageResource(R.drawable.active_dot);
            } else {
                dots.get(i).setImageResource(R.drawable.inactive_dot);
            }
        }
    }

    private void observeViewModel() {
        viewModel.getTop5Events().observe(getViewLifecycleOwner(), events -> {
            if (events != null && !events.isEmpty()) {
                binding.noEventsMessage.setVisibility(View.GONE);
                binding.top5EventsRecyclerView.setVisibility(View.VISIBLE);
                adapter.updateEventList(events);

                setupPaginationDots(events.size());
            } else {
                binding.noEventsMessage.setVisibility(View.VISIBLE);
                binding.top5EventsRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingEventsMessage.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
