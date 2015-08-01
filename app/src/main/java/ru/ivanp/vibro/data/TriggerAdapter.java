package ru.ivanp.vibro.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.ivanp.vibro.App;
import ru.ivanp.vibro.R;
import ru.ivanp.vibro.vibrations.Trigger;
import ru.ivanp.vibro.vibrations.Vibration;

// TODO add selection aka playing position, but before fix models and manager
public class TriggerAdapter extends RecyclerView.Adapter {
    // ============================================================================================
    // INTERFACE
    // ============================================================================================
    public interface OnItemClickListener {
        void onItemClick(Trigger _trigger);

        void onItemLongClick(Trigger _trigger);
    }

    // ============================================================================================
    // CONSTANTS
    // ============================================================================================

    // ============================================================================================
    // FIELDS
    // ============================================================================================
    private final Context context;
    private final LayoutInflater inflater;
    private List<Trigger> triggers;
    private OnItemClickListener onItemClickListener;
    //private int playingPosition;

    // ============================================================================================
    // GETTERS/SETTERS
    // ============================================================================================
    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        onItemClickListener = _onItemClickListener;
    }

    /*public void setPlaying(int _position) {
        if (playingPosition != -1) {
            notifyItemChanged(playingPosition);
        }
        playingPosition = _position;
        notifyItemChanged(playingPosition);
    }*/

    // ============================================================================================
    // CONSTRUCTOR
    // ============================================================================================
    public TriggerAdapter(Context _context) {
        context = _context;
        inflater = LayoutInflater.from(_context);
    }

    // ============================================================================================
    // METHODS OF BASE CLASS
    // ============================================================================================
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup _parent, int _viewType) {
        return new TriggerViewHolder(inflater.inflate(R.layout.view_trigger, _parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder _viewHolder, int _position) {
        ((TriggerViewHolder) _viewHolder).bind(triggers.get(_position), _position);
    }

    @Override
    public int getItemCount() {
        return triggers != null ? triggers.size() : 0;
    }

    // ============================================================================================
    // METHODS
    // ============================================================================================
    public void update(List<Trigger> _triggers) {
        triggers = _triggers;
        notifyDataSetChanged();
    }

    // ============================================================================================
    // VIEW HOLDER
    // ============================================================================================
    private class TriggerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        View isPayingView;
        TextView triggerNameTextView;
        TextView vibrationNameTextView;

        public TriggerViewHolder(View _view) {
            super(_view);
            _view.setOnClickListener(this);
            _view.setOnLongClickListener(this);
            isPayingView = _view.findViewById(R.id.isPlayingView);
            triggerNameTextView = (TextView) _view.findViewById(R.id.triggerNameTextView);
            vibrationNameTextView = (TextView) _view.findViewById(R.id.vibrationNameTextView);
        }

        public void bind(Trigger _trigger, int _position) {
            //isPayingView.setVisibility(_position == playingPosition ? View.VISIBLE : View.GONE);
            triggerNameTextView.setText(_trigger.name);
            // fixme refactor this
            Vibration vibration = App.getVibrationManager().getVibration(_trigger.vibrationID);
            vibrationNameTextView.setText(vibration != null ? vibration.getName() : context.getString(R.string.do_not_vibrate));
        }

        @Override
        public void onClick(View _view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                onItemClickListener.onItemClick(triggers.get(position));
            }
        }

        @Override
        public boolean onLongClick(View _view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                onItemClickListener.onItemLongClick(triggers.get(position));
                return true;
            }
            return false;
        }
    }
}
