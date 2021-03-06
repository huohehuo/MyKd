package com.kd.mykd.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kd.mykd.R;
import com.kd.mykd.databinding.FragmentMsgBinding;

/**
 * Created by LINS on 2017/2/22.
 */

public class MsgFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMsgBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_msg,container,false);
        return binding.getRoot();

    }
}
