package com.qprovep.exlog.ui.templates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.qprovep.exlog.R;
import com.qprovep.exlog.ui.exercise.ExerciseListFragment;
import com.qprovep.exlog.ui.workout.WorkoutListFragment;

public class TemplatesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_templates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.templates_tabs);
        ViewPager2 viewPager = view.findViewById(R.id.templates_view_pager);

        viewPager.setAdapter(new TemplatesPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.tab_workouts);
            } else {
                tab.setText(R.string.tab_exercises);
            }
        }).attach();
    }

    private static class TemplatesPagerAdapter extends FragmentStateAdapter {

        public TemplatesPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new WorkoutListFragment();
            } else {
                return new ExerciseListFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
