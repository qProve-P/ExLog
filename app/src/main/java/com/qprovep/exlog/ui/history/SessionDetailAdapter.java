package com.qprovep.exlog.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qprovep.exlog.R;
import com.qprovep.exlog.data.entity.SetLog;

import java.util.ArrayList;
import java.util.List;

public class SessionDetailAdapter extends RecyclerView.Adapter<SessionDetailAdapter.ViewHolder> {

    private List<SessionDetailViewModel.DetailExerciseEntry> exercises = new ArrayList<>();

    public void setExercises(List<SessionDetailViewModel.DetailExerciseEntry> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_detail_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(exercises.get(position));
    }

    @Override
    public int getItemCount() {
        return exercises == null ? 0 : exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final LinearLayout setsContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.exercise_name);
            setsContainer = itemView.findViewById(R.id.sets_container);
        }

        void bind(SessionDetailViewModel.DetailExerciseEntry entry) {
            nameText.setText(entry.exercise.getName());
            setsContainer.removeAllViews();

            if (entry.sets.isEmpty()) {
                TextView emptyText = new TextView(itemView.getContext());
                emptyText.setText("Not Completed");
                emptyText.setTypeface(null, android.graphics.Typeface.ITALIC);
                emptyText.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(),
                        android.R.color.darker_gray));
                emptyText.setPadding(0, 8, 0, 8);
                setsContainer.addView(emptyText);
            } else {
                LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
                for (SetLog set : entry.sets) {
                    View setRow = inflater.inflate(R.layout.item_session_detail_set, setsContainer, false);
                    TextView numText = setRow.findViewById(R.id.set_number);
                    TextView weightText = setRow.findViewById(R.id.set_weight);
                    TextView repsText = setRow.findViewById(R.id.set_reps);

                    numText.setText(String.valueOf(set.getSetNumber()));

                    if (set.getWeight() < 0 || set.getReps() < 0) {
                        weightText.setText("-");
                        weightText.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(),
                                android.R.color.darker_gray));
                        repsText.setText("-");
                        repsText.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(),
                                android.R.color.darker_gray));
                    } else {
                        String weightStr = (set.getWeight() == (int) set.getWeight())
                                ? String.valueOf((int) set.getWeight())
                                : String.valueOf(set.getWeight());

                        weightText.setText(weightStr + " kg");
                        weightText.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(),
                                android.R.color.tab_indicator_text));
                        repsText.setText(String.valueOf(set.getReps()));
                        repsText.setTextColor(androidx.core.content.ContextCompat.getColor(itemView.getContext(),
                                android.R.color.tab_indicator_text));
                    }

                    setsContainer.addView(setRow);
                }
            }
        }
    }
}
