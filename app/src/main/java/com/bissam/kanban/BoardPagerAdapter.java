package com.bissam.kanban;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BoardPagerAdapter extends FragmentStateAdapter {

    public BoardPagerAdapter(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public Fragment createFragment(int position) {
        String status;
        switch (position) {
            case 1: status = "inprogress"; break;
            case 2: status = "done"; break;
            default: status = "todo";
        }
        return TaskListFragment.newInstance(status);
    }

    @Override
    public int getItemCount() { return 3; }
}