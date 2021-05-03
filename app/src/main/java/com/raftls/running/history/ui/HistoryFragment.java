package com.raftls.running.history.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.raftls.running.app.models.ApiResponse;
import com.raftls.running.app.models.ResponseError;
import com.raftls.running.databinding.FragmentHistoryBinding;
import com.raftls.running.history.adapters.HistoryItem;
import com.raftls.running.history.services.HistoryService;

import org.jetbrains.annotations.NotNull;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        HistoryService.getInstance().getAllRuns(getContext(), new ApiResponse<FlexibleAdapter<HistoryItem>>() {
            @Override
            public void success(FlexibleAdapter<HistoryItem> response) {
                LinearLayoutManager layoutManager
                        = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                binding.history.setLayoutManager(layoutManager);
                binding.history.setAdapter(response);
            }

            @Override
            public void failure(ResponseError response) {

            }
        });
        return binding.getRoot();
    }
}