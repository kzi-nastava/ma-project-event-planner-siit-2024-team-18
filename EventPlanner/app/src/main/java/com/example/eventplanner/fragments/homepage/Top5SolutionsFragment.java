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
import com.example.eventplanner.adapters.Top5SolutionsAdapter;
import com.example.eventplanner.databinding.Top5SolutionsCarouselBinding;
import com.example.eventplanner.viewmodels.Top5SolutionsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Top5SolutionsFragment extends Fragment {

    private Top5SolutionsAdapter adapter;
    private Top5SolutionsCarouselBinding binding;
    private Top5SolutionsViewModel viewModel;
    private List<ImageView> dots;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = Top5SolutionsCarouselBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(Top5SolutionsViewModel.class);
        viewModel.setContext(requireContext());

        if (binding != null) {
            setupRecyclerView();
        }

        observeViewModel();

        viewModel.fetchTop5Solutions();
    }

    private void setupRecyclerView() {
        adapter = new Top5SolutionsAdapter(getContext(), new ArrayList<>());

        binding.top5SolutionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.top5SolutionsRecyclerView.setAdapter(adapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.top5SolutionsRecyclerView);

        binding.top5SolutionsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        viewModel.getTop5Solutions().observe(getViewLifecycleOwner(), solutions -> {
            if (solutions != null && !solutions.isEmpty()) {
                binding.noSolutionsMessage.setVisibility(View.GONE);
                binding.top5SolutionsRecyclerView.setVisibility(View.VISIBLE);
                adapter.updateSolutionList(solutions);

                setupPaginationDots(solutions.size());
            } else {
                binding.noSolutionsMessage.setVisibility(View.VISIBLE);
                binding.top5SolutionsRecyclerView.setVisibility(View.GONE);
            }
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingSolutionsMessage.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
