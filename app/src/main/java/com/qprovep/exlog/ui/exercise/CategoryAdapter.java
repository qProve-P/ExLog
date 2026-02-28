package com.qprovep.exlog.ui.exercise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qprovep.exlog.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryListener {
        void onCategorySelected(String category);

        void onCategoryDeleted(String category);
    }

    private final List<String> categories = new ArrayList<>();
    private final OnCategoryListener listener;
    private String selectedCategory;

    public CategoryAdapter(OnCategoryListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<String> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    public void addCategory(String category) {
        if (!categories.contains(category)) {
            categories.add(category);
            notifyItemInserted(categories.size() - 1);
        }
    }

    public void setSelectedCategory(String category) {
        this.selectedCategory = category;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final ImageButton deleteBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.category_name);
            deleteBtn = itemView.findViewById(R.id.btn_delete_category);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onCategorySelected(categories.get(pos));
                }
            });

            deleteBtn.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    String cat = categories.get(pos);
                    categories.remove(pos);
                    notifyItemRemoved(pos);
                    listener.onCategoryDeleted(cat);
                }
            });
        }

        void bind(String category) {
            nameText.setText(category);
            if (category.equals(selectedCategory)) {
                nameText.setTextColor(itemView.getContext().getColor(R.color.primary));
            } else {
                android.util.TypedValue typedValue = new android.util.TypedValue();
                itemView.getContext().getTheme().resolveAttribute(
                        android.R.attr.textColorPrimary, typedValue, true);
                nameText.setTextColor(itemView.getContext().getColor(typedValue.resourceId));
            }
        }
    }
}
