package com.example.fitx;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class PageAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public PageAdapter(@NonNull FragmentManager fm, @NonNull Lifecycle lf) {
        super(fm, lf);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new ExercisesFragment();
            case 2:
                return new ProgramsFragment();
            case 3:
                return new ProfileFragment();
            case 4:
                return new SocialFragment();

        }
        return null;
    }


    @Override
    public int getItemCount() {
        return 5;
    }
}
