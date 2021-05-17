package com.raftls.running.app.fragment;

import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.MenuRes;
import androidx.fragment.app.Fragment;

import com.raftls.running.R;
import com.raftls.running.authentification.events.AuthenticationEvent;
import com.raftls.running.history.events.DeleteRun;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseToolbarFragment extends Fragment {
    protected void showMenu(View view, @MenuRes int menuRes) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.logout) {
                EventBus.getDefault().post(new AuthenticationEvent(null));
                return true;
            }
            if (menuItem.getItemId() == R.id.delete_run) {
                EventBus.getDefault().post(new DeleteRun());
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
}
