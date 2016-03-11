package ru.ivanp.vibro.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.data.TriggerAdapter;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.Vibration;
import ru.ivanp.vibro.views.DividerItemDecoration;

/**
 * @author Posohov Ivan (posohof@gmail.com)
 */
public class MainFragment extends Fragment {
    // ============================================================================================
    // FIELDS
    // ============================================================================================
    private TriggerAdapter triggerAdapter;

    // ============================================================================================
    // LIFECYCLE
    // ============================================================================================
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        triggerAdapter = new TriggerAdapter(getActivity());
        triggerAdapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(triggerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        triggerAdapter.update(App.getTriggerManager().getTriggers());
    }

    @Override
    public void onPause() {
        super.onPause();
        App.getPlayer().stop();
    }

    // ============================================================================================
    // WIDGET CALLBACKS
    // ============================================================================================
    private final TriggerAdapter.OnItemClickListener onItemClickListener = new TriggerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Trigger _trigger) {
            Vibration vibration = App.getVibrationManager().getVibration(_trigger.vibrationID);
            App.getPlayer().playOrStop(vibration);
        }

        @Override
        public void onItemLongClick(Trigger _trigger) {
            SelectVibrationActivity.startActivity(getActivity(), _trigger.id);
        }
    };

    // ============================================================================================
    // METHODS
    // ============================================================================================
    public static MainFragment newInstance() {
        return new MainFragment();
    }
}