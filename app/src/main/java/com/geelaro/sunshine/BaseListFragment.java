package com.geelaro.sunshine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geelaro.sunshine.beans.ImageBean;
import com.geelaro.sunshine.images.ImageAdapter;
import com.geelaro.sunshine.images.presenter.ImagePresenter;
import com.geelaro.sunshine.utils.SunLog;
import com.geelaro.sunshine.utils.SunshineApp;

import java.util.List;

/**
 * Created by geelaro on 2018/1/10.
 */

public abstract class BaseListFragment extends Fragment{
    protected RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    protected SwipeRefreshLayout mRefreshLayout;

    protected abstract RecyclerView.Adapter bindAdapter();
    protected abstract void loadFromNet();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frame_common_list, container, false);

        mAdapter = bindAdapter();

        manager = new LinearLayoutManager(getActivity());
        // SwipeRefreshLayout
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.common_refresh);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFromNet();
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleView_common);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadFromNet();

        return rootView;
    }

}
