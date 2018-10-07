package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {

    private static final String TAG = "NerdLauncherFragment";

    private static RecyclerView recyclerView;

    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        recyclerView = v.findViewById(R.id.app_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();

        return v;
    }

    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo resolveInfo, ResolveInfo t1) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        resolveInfo.loadLabel(pm).toString(),
                        t1.loadLabel(pm).toString());
            }
        });

        Log.i(TAG, "Found " + activities.size() + " activities.");
        recyclerView.setAdapter(new ActivityAdapter(activities));
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ResolveInfo resolveInfo;
        private TextView nameTextView;

        public ActivityHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView;
            nameTextView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = resolveInfo.loadLabel(pm).toString();
            nameTextView.setText(appName);

        }

        @Override
        public void onClick(View view) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            Intent i = new Intent(Intent.ACTION_MAIN).setClassName(activityInfo.applicationInfo.packageName,
                    activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private final List<ResolveInfo> activities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            this.activities = activities;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = activities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return activities.size();
        }
    }
}
