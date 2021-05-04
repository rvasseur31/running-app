package com.raftls.running.history.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.swipe.SimpleSwipeCallback;
import com.raftls.running.R;
import com.raftls.running.app.activities.MainActivity;
import com.raftls.running.app.models.ApiResponse;
import com.raftls.running.app.models.ResponseError;
import com.raftls.running.databinding.FragmentHistoryBinding;
import com.raftls.running.history.adapters.HistoryItem;
import com.raftls.running.history.models.DeletedElement;
import com.raftls.running.history.services.HistoryService;

import org.jetbrains.annotations.NotNull;

public class HistoryFragment extends Fragment implements SimpleSwipeCallback.ItemSwipeCallback {

    private FragmentHistoryBinding binding;
    private ItemAdapter<HistoryItem> runs;
    private FastAdapter<HistoryItem> adapter;
    private DeletedElement recentlyRemoveElement;

    private final HistoryService historyService = HistoryService.getInstance();

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
        historyService.getAllRuns(getContext(), new ApiResponse<ItemAdapter<HistoryItem>>() {
            @Override
            public void success(ItemAdapter<HistoryItem> response) {
                runs = response;
                adapter = FastAdapter.with(response);
                binding.history.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.history.setItemAnimator(new DefaultItemAnimator());
                binding.history.setAdapter(adapter);


                adapter.setOnClickListener((view, historyItemIAdapter, historyItem, integer) -> {
                    if (getActivity() != null && getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).openFragment(RunDetailFragment.newInstance(historyItem.getRun()));
                    }
                    return true;
                });

                Drawable trash = ContextCompat.getDrawable(container.getContext(), R.drawable.ic_delete);
                SimpleSwipeCallback touchCallback = new SimpleSwipeCallback(
                        HistoryFragment.this,
                        trash,
                        ItemTouchHelper.LEFT,
                        Color.RED
                )
                        .withBackgroundSwipeRight(Color.RED)
                        .withLeaveBehindSwipeRight(trash)
                        .withSensitivity(10f)
                        .withSurfaceThreshold(0.8f);
                ItemTouchHelper touchHelper = new ItemTouchHelper(touchCallback);
                touchHelper.attachToRecyclerView(binding.history);
            }

            @Override
            public void failure(ResponseError response) {

            }
        });
        return binding.getRoot();
    }

    private void showUndoSnackbar() {
        if (getActivity() != null) {
            View view = getActivity().findViewById(R.id.main_fragment);
            Snackbar snackbar = Snackbar.make(view, R.string.run_deleted,
                    Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, v -> undoDelete());
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        historyService.removeRun(recentlyRemoveElement.getItem().getRun().getId(),
                                new ApiResponse<Void>() {
                                    @Override
                                    public void success(Void response) {
                                        recentlyRemoveElement = null;
                                    }

                                    @Override
                                    public void failure(ResponseError response) {
                                        Toast.makeText(getContext(), R.string.error_run_deleted, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });
            snackbar.show();
        }
    }

    private void undoDelete() {
        runs.add(recentlyRemoveElement.getItemPosition(),
                recentlyRemoveElement.getItem());
        adapter.notifyItemInserted(recentlyRemoveElement.getItemPosition());
    }

    @Override
    public void itemSwiped(int position, int direction) {
        HistoryItem item = adapter.getItem(position);
        if (item == null) {
            return;
        } else {
            recentlyRemoveElement = new DeletedElement(item, position);
        }
        runs.remove(position);
        adapter.notifyItemRemoved(position);
        showUndoSnackbar();
    }
}