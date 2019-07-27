package com.aip.pillowbuddy;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SecondFragment extends Fragment {
    private String title;
    private int page;
    private int bgMode;

    public static Integer[] bgIDs = {R.drawable.bg_morning, R.drawable.bg_afternoon, R.drawable.bg_evening, R.drawable.bg_night};

    public static SecondFragment newInstance(int page, String title, int bgMode) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putInt("currentBackground", bgMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
        bgMode = getArguments().getInt("currentBackground", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        LinearLayout linearLayoutBg = (LinearLayout) view.findViewById(R.id.linearRoot);

        linearLayoutBg.setBackgroundResource(bgIDs[bgMode]);
        //TODO bgMode가 어두우면 텍스트컬러 흰색, 밝으면 검정색으로 만들어주기

//        EditText et1 = (EditText) view.findViewById(R.id.editText1);
//        et1.setText(page + " -- " + title);
        return view;
    }

}
