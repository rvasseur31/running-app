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
import com.raftls.running.history.events.HistoryRefresh;
import com.raftls.running.history.models.DeletedElement;
import com.raftls.running.history.services.HistoryService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HistoryFragment extends Fragment implements SimpleSwipeCallback.ItemSwipeCallback {

    private FragmentHistoryBinding binding;
    private ItemAdapter<HistoryItem> runs;
    private FastAdapter<HistoryItem> adapter;
    private Snackbar undoDelete;
    private final ArrayList<DeletedElement> recentlyRemoveElement = new ArrayList<>();

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
        onHistoryRefresh(null);
        binding.swipeToRefreshHistory.setOnRefreshListener(() -> onHistoryRefresh(null));
        return binding.getRoot();
    }

    private void showUndoSnackbar() {
        if (getActivity() != null) {
            View view = getActivity().findViewById(R.id.main_fragment);
            if (undoDelete != null && undoDelete.isShown()) {
                undoDelete.dismiss();
            }
            undoDelete = Snackbar.make(view, R.string.run_deleted,
                    Snackbar.LENGTH_LONG);
            undoDelete.setAction(R.string.undo, v -> undoDelete());
            undoDelete.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        ArrayList<String> runsToDelete = new ArrayList<>();
                        for (int index = recentlyRemoveElement.size() - 1; index >= 0; index--) {
                            DeletedElement deletedElement = recentlyRemoveElement.get(index);
                            runsToDelete.add(deletedElement.getItem().getRun().getId());
                        }
                        recentlyRemoveElement.clear();
                        historyService.removeMultipleRuns(runsToDelete, new ApiResponse<Void>() {
                            @Override
                            public void success(Void response) {

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
            undoDelete.show();
        }
    }

    private void undoDelete() {
        for (int index = recentlyRemoveElement.size() - 1; index >= 0; index--) {
            DeletedElement deletedElement = recentlyRemoveElement.get(index);
            runs.add(deletedElement.getItemPosition(),
                    deletedElement.getItem());
            recentlyRemoveElement.remove(index);
        }
        adapter.notifyAdapterDataSetChanged();
    }

    @Override
    public void itemSwiped(int position, int direction) {
        HistoryItem item = adapter.getItem(position);
        if (item == null) {
            return;
        } else {
            recentlyRemoveElement.add(new DeletedElement(item, position));
        }
        runs.remove(position);
        adapter.notifyAdapterDataSetChanged();
        showUndoSnackbar();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHistoryRefresh(HistoryRefresh event) {
        historyService.getAllRuns(getContext(), new ApiResponse<ItemAdapter<HistoryItem>>() {
            @Override
            public void success(ItemAdapter<HistoryItem> response) {
                binding.swipeToRefreshHistory.setRefreshing(false);
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

                Drawable trash = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete);
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
    }
}