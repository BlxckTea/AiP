package com.aip.pillowbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.StrictMath.abs;

public class SecondFragment extends Fragment {
    private String title;
    private int page;
    private int bgMode;

    View dialogConsult, dialogFeedback;
    private CombinedChart chart;
    private HorizontalBarChart chartConsult;

    //combined chart's line & bar entries
    static ArrayList<Entry> lineEntries;
    static ArrayList<BarEntry> barEntries;

    static ArrayList<BarEntry> consultBarEntries;
//    static ArrayList<String> consultBarEntryLabels;

    static ArrayList<String> descGraph = new ArrayList<>();

    BarDataSet consultBarDataset;
    BarData consultBarData;

    static Integer timeLength = 0;
    static Integer lastday = 0;

    TimePicker setTargetTime;
    String tpAp, tpHour, tpMinute;
    static Boolean cSwitch = false;
    static String fSwitch = "False";
    static Integer fState = 0;

    static Integer weekCount = 6;
    static Float sleepScore;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;


    private boolean refreshOnce = true;
//    private static boolean calledAlready = false;

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

        /*if (!calledAlready) {
            mFirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }*/

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        lineEntries = new ArrayList<Entry>();
        barEntries = new ArrayList<BarEntry>();
        consultBarEntries = new ArrayList<BarEntry>();
//        consultBarEntryLabels = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        final View viewInit = view;
        if(refreshOnce) loadPosts(viewInit);

        LinearLayout linearLayoutBg = (LinearLayout) view.findViewById(R.id.linearRoot);

        linearLayoutBg.setBackgroundResource(bgIDs[bgMode]);

        LinearLayout linearGraph = (LinearLayout) view.findViewById(R.id.linearGraph);
        LinearLayout linearConsult = (LinearLayout) view.findViewById(R.id.linearConsult);
        TextView tvTitleGraph = (TextView) view.findViewById(R.id.titleGraph);
        TextView tvTitleConsult = (TextView) view.findViewById(R.id.titleConsult);
        final TextView tvGraph = (TextView) view.findViewById(R.id.tvGraph);
        final TextView tvConsult = (TextView) view.findViewById(R.id.tvConsult);
        TextView cSleptText = (TextView) view.findViewById(R.id.consultSleptText);
        TextView cSleptValue = (TextView) view.findViewById(R.id.consultSleptValue);
        TextView cRatioText = (TextView) view.findViewById(R.id.consultRatioText);
        TextView cRatioValue = (TextView) view.findViewById(R.id.consultRatioValue);
        TextView cScoreText = (TextView) view.findViewById(R.id.consultScoreText);
        TextView cScoreValue = (TextView) view.findViewById(R.id.consultScoreValue);
        TextView cTimeText = (TextView) view.findViewById(R.id.consultTimeText);
        TextView cTimeAp = (TextView) view.findViewById(R.id.consultTimeAp);
        TextView cTimeHour = (TextView) view.findViewById(R.id.consultTimeHour);
        TextView cTimeMinute = (TextView) view.findViewById(R.id.consultTimeMinute);
        TextView cWeekCount = (TextView) view.findViewById(R.id.consultCount);

        Button btnConsult = (Button) view.findViewById(R.id.btnConsult);
        final Button btnFeedback = (Button) view.findViewById(R.id.btnFeedback);
        TextView tvQ1 = (TextView) view.findViewById(R.id.tvQ1);
        TextView tvQ2 = (TextView) view.findViewById(R.id.tvQ2);
        Button btnYes1 = (Button) view.findViewById(R.id.btnYes1);
        Button btnNo1 = (Button) view.findViewById(R.id.btnNo1);
        Button btnYes2 = (Button) view.findViewById(R.id.btnYes2);
        Button btnNo2 = (Button) view.findViewById(R.id.btnNo2);
        TextView tvCoaching = (TextView) view.findViewById(R.id.tvCoaching);
        TextView tvCoachTips = (TextView) view.findViewById(R.id.tvCoachTips);

        chart = (CombinedChart) view.findViewById(R.id.chart);
        chartConsult = (HorizontalBarChart) view.findViewById(R.id.chartConsult);

        Legend legend1 = chart.getLegend();
        YAxis rightAxis1 = chart.getAxisRight();
        YAxis leftAxis1 = chart.getAxisLeft();
        XAxis xAxis1 = chart.getXAxis();
        Legend legend2 = chartConsult.getLegend();
        XAxis xAxis2 = chartConsult.getXAxis();

        if(bgMode < 2) {
            tvTitleGraph.setTextColor(Color.BLACK);
            tvTitleConsult.setTextColor(Color.BLACK);
            tvGraph.setTextColor(Color.BLACK);
            tvConsult.setTextColor(Color.BLACK);
            cSleptText.setTextColor(Color.BLACK);
            cRatioText.setTextColor(Color.BLACK);
            cRatioValue.setTextColor(Color.BLACK);
            cScoreText.setTextColor(Color.BLACK);
            cScoreValue.setTextColor(Color.BLACK);
            cTimeText.setTextColor(Color.BLACK);
            cTimeAp.setTextColor(Color.BLACK);
            cTimeHour.setTextColor(Color.BLACK);
            cTimeMinute.setTextColor(Color.BLACK);
            cWeekCount.setTextColor(Color.BLACK);
            btnFeedback.setTextColor(Color.BLACK);
            btnFeedback.setBackgroundResource(R.drawable.button_round_bk);
            btnFeedback.setEnabled(true);
            btnFeedback.setVisibility(View.VISIBLE);
            btnConsult.setTextColor(Color.BLACK);
            btnConsult.setBackgroundResource(R.drawable.button_round_bk);
            legend1.setTextColor(Color.BLACK);
            rightAxis1.setTextColor(Color.BLACK);
            leftAxis1.setTextColor(Color.BLACK);
            xAxis1.setTextColor(Color.BLACK);
            legend2.setTextColor(Color.BLACK);
            xAxis2.setTextColor(Color.BLACK);
        } else {
            tvTitleGraph.setTextColor(Color.WHITE);
            tvTitleConsult.setTextColor(Color.WHITE);
            tvGraph.setTextColor(Color.WHITE);
            tvConsult.setTextColor(Color.WHITE);
            cSleptText.setTextColor(Color.WHITE);
            cSleptValue.setTextColor(Color.WHITE);
            cRatioText.setTextColor(Color.WHITE);
            cRatioValue.setTextColor(Color.WHITE);
            cScoreText.setTextColor(Color.WHITE);
            cScoreValue.setTextColor(Color.WHITE);
            cTimeText.setTextColor(Color.WHITE);
            cTimeAp.setTextColor(Color.WHITE);
            cTimeHour.setTextColor(Color.WHITE);
            cTimeMinute.setTextColor(Color.WHITE);
            cWeekCount.setTextColor(Color.WHITE);
            btnFeedback.setTextColor(Color.WHITE);
            btnFeedback.setBackgroundResource(R.drawable.button_round_wh);
            btnFeedback.setEnabled(false);
            btnFeedback.setVisibility(View.INVISIBLE);
            btnConsult.setTextColor(Color.WHITE);
            btnConsult.setBackgroundResource(R.drawable.button_round_wh);
            legend1.setTextColor(Color.WHITE);
            rightAxis1.setTextColor(Color.WHITE);
            leftAxis1.setTextColor(Color.WHITE);
            xAxis1.setTextColor(Color.WHITE);
            legend2.setTextColor(Color.WHITE);
            xAxis2.setTextColor(Color.WHITE);
        }
        if(fSwitch.equals("False")) {
            btnFeedback.setBackgroundResource(R.drawable.button_round_gr);
            btnFeedback.setTextColor(Color.GRAY);
        }

        //Combined chart
        chart.setDrawBarShadow(false);
        chartConsult.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setDrawValueAboveBar(true);
        chart.setMaxVisibleValueCount(240);
        chart.setPinchZoom(false);
        chartConsult.setPinchZoom(false);
        chart.setDrawGridBackground(true);
        chartConsult.setDrawGridBackground(true);
        if(bgMode == 0 || bgMode == 1) chart.setGridBackgroundColor(Color.parseColor("#337182a2"));//337182a2 //33:20%, 66:40%, 99:60%
        else chart.setGridBackgroundColor(Color.parseColor("#669fb5dd"));
//        chart.setBackgroundColor(Color.parseColor("#99ffffff"));
        chartConsult.setGridBackgroundColor(Color.parseColor("#33000000"));
        chart.setHighlightFullBarEnabled(true);
        chartConsult.setHighlightFullBarEnabled(true);
        chart.setHorizontalScrollBarEnabled(true);
        chartConsult.setHorizontalFadingEdgeEnabled(true);
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });
        chart.getDescription().setText("");
        chartConsult.getDescription().setText("숫자 * 5분 = 실제시간");

        //setContentDescription
        if(!descGraph.isEmpty()){
            linearGraph.setContentDescription("수면 그래프 입니다." + descGraph + "순서로 수면단계가 진행되었습니다.");
        } else linearGraph.setContentDescription("수면 그래프 입니다. 아직 수면기록이 없어 그래프가 없습니다.");



        final String[] states, times;
        states = new String[]{"", "DEEP", "LIGHT", "REM", "AWAKE", ""};

        ArrayList<String> timesList = new ArrayList<String>();
        for(Integer i = 1; i<= timeLength; i++){
            timesList.add(i.toString());
            if(i == timeLength) timesList.add("");
        }
        times = new String[timesList.size()];
        timesList.toArray(times);

        legend1.setWordWrapEnabled(true);
        legend1.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend1.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend1.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend1.setDrawInside(false);
//        legend2.setDrawInside(true);
        legend2.setEnabled(false);

        //Set right axis
        rightAxis1.setDrawGridLines(true);
        rightAxis1.setAxisMinimum(0.0f);
        rightAxis1.setAxisMaximum(11.0f);
        rightAxis1.setGranularity(1.0f);
        //Set left axis
        leftAxis1.setDrawGridLines(true);
        leftAxis1.setAxisMinimum(0);
        leftAxis1.setAxisMaximum(5);
        leftAxis1.setGranularity(1);
        leftAxis1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return states[(int)value];
            }
        });
        //Set chart-x1 axis
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setAxisMinimum(1.0f);
        xAxis1.setGranularity(0.5f);
        xAxis1.setCenterAxisLabels(true);
        xAxis1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
//                Log.d("times: ", "timeLength: "+ timeLength);
//                Log.d("times: ", "value: "+value);
//                Log.d("times: ", "value % timeLength: "+value% timeLength);
                return times[(int)value % timeLength];
//                return times[(int)value];
            }
        });


        //Set consult-x2 axis
        final String[] cLabels;
        cLabels = new String[]{"깊은 잠", "얕은 잠", "총 수면"};
        //Set consult-x axis
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setAxisMinimum(0.0f);
        xAxis2.setAxisMaximum(2.0f);
        xAxis2.setGranularity(1.0f);
        xAxis2.setCenterAxisLabels(true);
        xAxis2.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
//                Log.d("clabel", "value :"+value);
//                Log.d("clabel", "v%len :"+value%cLabels.length);
//                return cLabels[(int)value];
                return cLabels[abs((int)value%cLabels.length)];
            }
        });

        //Create CombinedData
        CombinedData data1 = new CombinedData();
        data1.setData(generateLineData(lineEntries));
        data1.setData(generateBarData(barEntries));
        chart.setData(data1);
        chart.invalidate();

        //Create ConsultBarData
        generateConsultBarData();
        chartConsult.setData(consultBarData);
        chartConsult.animateY(3000);

        //Create chart
        xAxis1.setAxisMaximum(data1.getXMax());
        xAxis2.setAxisMaximum(consultBarData.getXMax());

        if(lastday>0){
            cSleptValue.setText(timeLength*5 + "분");
            cRatioValue.setText(String.valueOf(calcSleepScore() + "%"));
            cScoreValue.setText(String.valueOf(sleepScore + "점"));
        }

        if(fSwitch.equals("True")) btnFeedback.setEnabled(true);
        else btnFeedback.setEnabled(false);

        if(cSwitch.equals(true)){
            cTimeText.setTextColor(Color.WHITE);
            cTimeAp.setVisibility(View.VISIBLE);
            cTimeHour.setVisibility(View.VISIBLE);
            cTimeMinute.setVisibility(View.VISIBLE);
            cWeekCount.setVisibility(View.VISIBLE);
            cTimeText.setText("목표 취침 시간 : ");
            if(bgMode<2)cTimeText.setTextColor(Color.BLACK);
            else cTimeText.setTextColor(Color.WHITE);
            cTimeAp.setText(tpAp+" ");
            cTimeHour.setText(tpHour+"시 ");
            cTimeMinute.setText(tpMinute+"분");
            cWeekCount.setText("컨설팅 "+weekCount+"일차 입니다.");

        } else {
            cTimeText.setTextColor(Color.GRAY);
            cTimeText.setText("목표 취침시간을 설정하시면 컨설팅 서비스를 받으실 수 있습니다.");
            cTimeAp.setVisibility(View.GONE);
            cTimeHour.setVisibility(View.GONE);
            cTimeMinute.setVisibility(View.GONE);
            cWeekCount.setVisibility(View.GONE);
        }

        final View viewFromFrag = view;
        if(fSwitch.equals("False")) btnFeedback.setEnabled(false);
        else btnFeedback.setEnabled(true);
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFeedback = (View) View.inflate(getActivity(), R.layout.dialog_feedback, null);

                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

                dlg.setView(dialogFeedback);

                final TextView tvCoachTitle = (TextView) dialogFeedback.findViewById(R.id.tvCoachTitle);
                final TextView tvQ1 = (TextView) dialogFeedback.findViewById(R.id.tvQ1);
                final TextView tvQ2 = (TextView) dialogFeedback.findViewById(R.id.tvQ2);
                final Button btnYes1 = (Button) dialogFeedback.findViewById(R.id.btnYes1);
                final Button btnNo1 = (Button) dialogFeedback.findViewById(R.id.btnNo1);
                final Button btnYes2 = (Button) dialogFeedback.findViewById(R.id.btnYes2);
                final Button btnNo2 = (Button) dialogFeedback.findViewById(R.id.btnNo2);
                final TextView tvCoaching = (TextView) dialogFeedback.findViewById(R.id.tvCoaching);
                final TextView tvCoachTips = (TextView) dialogFeedback.findViewById(R.id.tvCoachTips);

                //fState //0:nothing to do
                //1:y,y //2:y,n //3:n,y //4:n,n
                tvQ2.setVisibility(View.GONE);
                btnYes2.setVisibility(View.GONE);
                btnNo2.setVisibility(View.GONE);
                tvCoaching.setVisibility(View.GONE);
                tvCoachTips.setVisibility(View.GONE);

                btnYes1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvQ1.setVisibility(View.GONE);
                        btnYes1.setVisibility(View.GONE);
                        btnNo1.setVisibility(View.GONE);
                        tvQ2.setVisibility(View.VISIBLE);
                        btnYes2.setVisibility(View.VISIBLE);
                        btnNo2.setVisibility(View.VISIBLE);
                        fState = 1;
                    }
                });
                btnNo1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvQ1.setVisibility(View.GONE);
                        btnYes1.setVisibility(View.GONE);
                        btnNo1.setVisibility(View.GONE);
                        tvQ2.setVisibility(View.VISIBLE);
                        btnYes2.setVisibility(View.VISIBLE);
                        btnNo2.setVisibility(View.VISIBLE);
                        fState = 3;
                    }
                });
                btnYes2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvQ1.setVisibility(View.GONE);
                        btnYes1.setVisibility(View.GONE);
                        btnNo1.setVisibility(View.GONE);
                        tvQ2.setVisibility(View.GONE);
                        btnYes2.setVisibility(View.GONE);
                        btnNo2.setVisibility(View.GONE);
                        tvCoaching.setVisibility(View.VISIBLE);
                        tvCoachTips.setVisibility(View.VISIBLE);
                        tvCoachTitle.setText("피드백 결과");
                        if(fState == 1) {
                            fState = 1;
                            tvCoaching.setText("이 구역의 수면대장이시네요! 적절하게 잘 숙면하고 계세요. 지금과 같이 취침시간을 유지해주세요!");
                        }
                        else if(fState == 3) {
                            fState = 3;
                            tvCoaching.setText("잠이 부족하셨나봐요. 일단은 지금과 같이 취침시간을 유지하시고, "+(7-weekCount)+"일 뒤에도 여전히 피곤하시다면 15분~30분정도 일찍 잠자리에 들어주세요!");
                        }

                        mFirebaseDatabase.getReference().child("consult").child("fState").setValue(fState);
                        mFirebaseDatabase.getReference().child("consult").child("feedbackSwitch").setValue("False");
                        fSwitch = "False";
                        btnFeedback.setEnabled(false);

                    }
                });
                btnNo2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvQ1.setVisibility(View.GONE);
                        btnYes1.setVisibility(View.GONE);
                        btnNo1.setVisibility(View.GONE);
                        tvQ2.setVisibility(View.GONE);
                        btnYes2.setVisibility(View.GONE);
                        btnNo2.setVisibility(View.GONE);
                        tvCoaching.setVisibility(View.VISIBLE);
                        tvCoachTips.setVisibility(View.VISIBLE);
                        tvCoachTitle.setText("피드백 결과");
                        if(fState == 1) fState = 2;
                        else if(fState == 3) fState = 4;

                        else if(fState == 2) tvCoaching.setText("잘 따라오고 계세요 수면 우등생이십니다! 필로우버디가 쭉 숙면을 책임져드릴테니 맡겨만 주세요!");
                        else if(fState == 4) tvCoaching.setText("수면시간이 조금 많은 것 같아요. "+(7-weekCount)+"일 뒤에도 여전히 피곤하시다면 15분~30분정도 늦게 잠자리에 드시는걸 추천드려요!");

                        mFirebaseDatabase.getReference().child("consult").child("fState").setValue(fState);
                        mFirebaseDatabase.getReference().child("consult").child("feedbackSwitch").setValue("False");
                        fSwitch = "False";
                        btnFeedback.setEnabled(false);


                    }
                });


                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getContext().getApplicationContext(), ".", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        btnConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConsult = (View) View.inflate(getActivity(), R.layout.dialog_consult, null);
                setTargetTime = (TimePicker) dialogConsult.findViewById(R.id.tpTarget);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

                dlg.setView(dialogConsult);
                dlg.setPositiveButton("시간지정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView cTimeText = (TextView) viewInit.findViewById(R.id.consultTimeText);
                        TextView cTimeAp = (TextView) viewFromFrag.findViewById(R.id.consultTimeAp);
                        TextView cTimeHour = (TextView) viewFromFrag.findViewById(R.id.consultTimeHour);
                        TextView cTimeMinute = (TextView) viewFromFrag.findViewById(R.id.consultTimeMinute);
                        TextView cWeekCount = (TextView) viewFromFrag.findViewById(R.id.consultCount);

                        tpHour = setTargetTime.getCurrentHour().toString();
                        tpMinute = setTargetTime.getCurrentMinute().toString();
                        tpAp = Integer.parseInt(tpHour)>12? "오후": "오전";

                        weekCount = 1; //일주일 카운팅 할 변수
                        cSwitch = true;

                        cTimeAp.setText(tpAp+" ");
                        cTimeHour.setText(tpHour+"시 ");
                        cTimeMinute.setText(tpMinute+"분");
                        cWeekCount.setText("컨설팅 "+weekCount+"일차 입니다.");
                        cTimeText.setText("목표 취침 시간 : ");
                        if(bgMode<2)cTimeText.setTextColor(Color.BLACK);
                        else cTimeText.setTextColor(Color.WHITE);
                        cTimeAp.setVisibility(View.VISIBLE);
                        cTimeHour.setVisibility(View.VISIBLE);
                        cTimeMinute.setVisibility(View.VISIBLE);
                        cWeekCount.setVisibility(View.VISIBLE);

                        mFirebaseDatabase.getReference().child("consult").child("targetAP").setValue(tpAp);
                        mFirebaseDatabase.getReference().child("consult").child("targetHour").setValue(tpHour);
                        mFirebaseDatabase.getReference().child("consult").child("targetMinutes").setValue(tpMinute);
                        mFirebaseDatabase.getReference().child("consult").child("weekCount").setValue(weekCount);
                        mFirebaseDatabase.getReference().child("consult").child("cSwitch").setValue(cSwitch);

                        Toast.makeText(getContext().getApplicationContext(),
                                "일주일 동안 "+tpAp+" "+tpHour.toString()+"시 "+tpMinute.toString()+"분에 잠들 수 있도록 노력해주세요!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.setNegativeButton("컨설팅취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cSwitch = false;
                        mFirebaseDatabase.getReference().child("consult").child("cSwitch").setValue(cSwitch);

                        TextView cTimeText = (TextView) viewInit.findViewById(R.id.consultTimeText);
                        TextView cTimeAp = (TextView) viewFromFrag.findViewById(R.id.consultTimeAp);
                        TextView cTimeHour = (TextView) viewFromFrag.findViewById(R.id.consultTimeHour);
                        TextView cTimeMinute = (TextView) viewFromFrag.findViewById(R.id.consultTimeMinute);
                        TextView cWeekCount = (TextView) viewFromFrag.findViewById(R.id.consultCount);

                        cTimeText.setText("목표 취침시간을 설정하시면 컨설팅 서비스를 받으실 수 있습니다.");
                        cTimeText.setTextColor(Color.GRAY);
                        cTimeAp.setVisibility(View.GONE);
                        cTimeHour.setVisibility(View.GONE);
                        cTimeMinute.setVisibility(View.GONE);
                        cWeekCount.setVisibility(View.GONE);

                        Toast.makeText(getContext().getApplicationContext(), "컨설팅이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });

        return view;
    }

    private void refresh() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
        refreshOnce = false;
    }

    private void loadPosts(final View viewInit) {
        //db저장된거 가져와서 graph값 바꿔주기 db에는 쓰기x
        mFirebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            CombinedChart chart = (CombinedChart) viewInit.findViewById(R.id.chart);
            HorizontalBarChart chartConsult = (HorizontalBarChart) viewInit.findViewById(R.id.chartConsult);
            TextView tvGraph = (TextView) viewInit.findViewById(R.id.tvGraph);
            TextView tvConsult = (TextView) viewInit.findViewById(R.id.tvConsult);
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("model/posts") == false) {
                    Log.d("loadPosts: ", "posts 일자기록없음: "+dataSnapshot.child("model/posts").getChildrenCount());
                    chart.setVisibility(View.GONE);
                    chartConsult.setVisibility(View.GONE);
                    tvGraph.setVisibility(View.VISIBLE);
                    tvConsult.setVisibility(View.VISIBLE);

                } else {
                    Log.d("loadPosts: ", "posts 일자기록있음: "+dataSnapshot.child("model/posts").getChildrenCount());
                    chart.setVisibility(View.VISIBLE);
                    chartConsult.setVisibility(View.VISIBLE);
                    tvGraph.setVisibility(View.GONE);
                    tvConsult.setVisibility(View.GONE);

                    Long lday = (Long) dataSnapshot.child("model/posts/lastday").getValue();
                    lastday = lday.intValue();
                    Long tlen = (Long) dataSnapshot.child("model/posts/"+ lastday.toString()).getChildrenCount();
                    timeLength = tlen.intValue();
                    Log.d("posts lastday: ", lastday.toString());
                    Log.d("posts time length: ", timeLength.toString());



                    int i=0, stVal=4, rem = 0, nrem = 0;
                    String descGraphBefore = "DEEP";
                    if(!descGraph.isEmpty()) for (String s : descGraph) descGraph.remove(s);//init ArrayList
                    for (DataSnapshot s : dataSnapshot.child("model/posts/"+lastday.toString()).getChildren()) {
                        String key = (String) s.getKey();
                        String ax = (String) s.child("axisten/").getValue();
                        String st = (String) s.child("state/").getValue();
                        Log.d("posts", "key: "+key);
                        Log.d("posts", "axisten: "+ax);
                        Log.d("posts", "state: "+st);

                        if(st.equals("DEEP")) {stVal = 1; nrem++;}
                        else if(st.equals("LIGHT")) {stVal = 2; rem++;}
                        else if(st.equals("REM")) {stVal = 3; rem++;}
                        else stVal = 4; //state없는 처음 3번은 AWAKE로 출력

                        if(!st.equals(descGraphBefore) && !st.equals("")) descGraph.add(st);
                        if(!st.equals(""))descGraphBefore = st;
                        Log.d("posts", "desc : "+descGraphBefore + "state: "+st);

                        if(i == 0) {
                            consultBarEntries.add(new BarEntry(3f, timeLength));
                        }
                        else if (i == timeLength-1) {
                            consultBarEntries.add(new BarEntry(2f, rem));
                            consultBarEntries.add(new BarEntry(1f, nrem));
                        }

                        lineEntries.add(new Entry(++i, Float.parseFloat(ax)));
                        barEntries.add(new BarEntry(i, stVal));
                        Log.d("posts: ", "i: "+i+", stVal:"+stVal);


                    }
                }

                //파베에 저장된 목표시간 가져와서 보여주기
                TextView cTimeText = (TextView) viewInit.findViewById(R.id.consultTimeText);
                TextView cTimeAp = (TextView) viewInit.findViewById(R.id.consultTimeAp);
                TextView cTimeHour = (TextView) viewInit.findViewById(R.id.consultTimeHour);
                TextView cTimeMinute = (TextView) viewInit.findViewById(R.id.consultTimeMinute);
                TextView cWeekCount = (TextView) viewInit.findViewById(R.id.consultCount);
                Button btnFeedback = (Button) viewInit.findViewById(R.id.btnFeedback);
                if(dataSnapshot.hasChild("consult") != false){ //경로에 내용이 있으면
                    cTimeText.setText("목표 취침 시간 : ");
                    cTimeAp.setVisibility(View.VISIBLE);
                    cTimeHour.setVisibility(View.VISIBLE);
                    cTimeMinute.setVisibility(View.VISIBLE);
                    cWeekCount.setVisibility(View.VISIBLE);

                    cSwitch = (Boolean) dataSnapshot.child("consult/cSwitch").getValue();
                    Log.d("값 있음, cSwitch: ", cSwitch.toString());
                    if(cSwitch.equals(true)) {
                        tpAp = (String) dataSnapshot.child("consult/targetAP").getValue();
                        tpHour = (String) dataSnapshot.child("consult/targetHour").getValue();
                        tpMinute = (String) dataSnapshot.child("consult/targetMinutes").getValue();
                        Long wCount = (Long) dataSnapshot.child("consult/weekCount").getValue();
                        fSwitch = (String) dataSnapshot.child("consult/feedbackSwitch").getValue();
                        Log.d("fSwitch: ", fSwitch);

                        if(fSwitch.equals("True")) {
                            btnFeedback.setEnabled(true);
                            btnFeedback.setVisibility(View.VISIBLE);
                        } else {
                            btnFeedback.setEnabled(false);
                            btnFeedback.setVisibility(View.INVISIBLE);
                        }

                        cTimeAp.setText(tpAp+" ");
                        cTimeHour.setText(tpHour+"시 ");
                        cTimeMinute.setText(tpMinute+"분");
                        cWeekCount.setText("컨설팅 "+wCount.toString()+"일차 입니다.");
                        weekCount = wCount.intValue(); //일주일 카운팅 할 변수
                    }

                } else { //경로에 내용이 없으면
                    cTimeText.setText("목표 취침시간을 설정하시면 컨설팅 서비스를 받으실 수 있습니다.");
                    cTimeAp.setVisibility(View.GONE);
                    cTimeHour.setVisibility(View.GONE);
                    cTimeMinute.setVisibility(View.GONE);
                    cWeekCount.setVisibility(View.GONE);

                    cSwitch = false;
                    mFirebaseDatabase.getReference().child("consult").child("cSwitch").setValue(cSwitch);
                    Log.d("값 없음, cSwitch: ", cSwitch.toString());
                }


                //fragment refresh
                if(refreshOnce) refresh();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("yeji", "loadStateList:onCancelled", databaseError.toException());
            }
        });
    }

    private Float calcSleepScore() {
        Float remRatio = 0f, remScore = 0f, durationScore = 0f;
        for(BarEntry e : consultBarEntries){
            if(e.getX() == 2.0f) remRatio = (Float) e.getY() / timeLength * 100;
            Log.d("calc rem ratio: ", String.valueOf(remRatio));

            if(remRatio<9.5f) remScore = 80f;
            else if(remRatio<19.5f) remScore = 90f;
            else if(remRatio<25.5f) remScore = 100f;
            else if(remRatio<35.5f) remScore = 90f;
            else if(remRatio<45.5f) remScore = 80f;
            else if(remRatio<55.5f) remScore = 70f;
            else if(remRatio<65.5f) remScore = 60f;
            else if(remRatio<75.5f) remScore = 50f;
            else if(remRatio<85.5f) remScore = 40f;
            else if(remRatio<95.5f) remScore = 30f;

            if(timeLength<3) durationScore = 10f;
            else if(timeLength<6) durationScore = 20f;
            else if(timeLength<12) durationScore = 30f;
            else if(timeLength<24) durationScore = 40f;
            else if(timeLength<36) durationScore = 50f;
            else if(timeLength<48) durationScore = 60f;
            else if(timeLength<60) durationScore = 70f;
            else if(timeLength<72) durationScore = 80f;
            else if(timeLength<84) durationScore = 90f;
            else if(timeLength<96) durationScore = 100f;//84~96(7~8시간)
            else if(timeLength<108) durationScore = 90f;
            else if(timeLength<110) durationScore = 85f;
            else if(timeLength<122) durationScore = 75f;
            else if(timeLength<134) durationScore = 65f;
            else if(timeLength<146) durationScore = 50f;
            else if(timeLength<158) durationScore = 40f;
            else if(timeLength<170) durationScore = 30f;
            else if(timeLength<182) durationScore = 20f;
            else if(timeLength<194) durationScore = 10f;
            else if(timeLength<206) durationScore = 5f; //17시간 취침까지 점수매김

            sleepScore = (0.8f*remScore) + (0.2f*durationScore);

        }

        mFirebaseDatabase.getReference().child("consult").child("score").setValue(sleepScore);

        return remRatio;
    }

    private LineData generateLineData(ArrayList<Entry> lineEntries) {
        LineData d = new LineData();

        LineDataSet set = new LineDataSet(lineEntries, "움직임");;
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.1f);

        set.setLineWidth(2.5f);
        set.setColor(Color.rgb(245, 214, 105));

        set.setCircleColor(Color.rgb(215, 203, 181));
        set.setCircleRadius(2f);

        set.setDrawFilled(true);
//        set.setFillDrawable(getResources().getDrawable(lineIDs[bgMode]));
//        set.setFillColor(Color.rgb(245, 214, 105));

        if(bgMode == 0 || bgMode == 1) set.setGradientColor(Color.parseColor("#33b78221"), Color.parseColor("#FFb78221"));
        else if(bgMode == 2 || bgMode == 3) set.setGradientColor(Color.parseColor("#33d7cbb5"), Color.parseColor("#FFd7cbb5"));
//        set.setFillAlpha(65);

        set.setDrawValues(false);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(215, 203, 181));

        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData(ArrayList<BarEntry> barEntries) {
        BarDataSet set1 = new BarDataSet(barEntries, "수면단계");

        if(bgMode == 0 || bgMode == 2) {
            set1.setGradientColor(Color.rgb(41, 67, 50), Color.rgb(54, 125, 69)); //green
            set1.setColor(Color.rgb(54, 125, 69));
        }
        else if(bgMode == 1 || bgMode == 3) {
            set1.setGradientColor(Color.rgb(41, 50, 67), Color.rgb(54, 69, 125)); //navy
            set1.setColor(Color.rgb(54, 69, 125));
        }
        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 1.0f; // x2 dataset


        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);

        return d;
    }

    private void generateConsultBarData() {
        consultBarDataset= new BarDataSet(consultBarEntries, "");
        consultBarData = new BarData(consultBarDataset);
        consultBarDataset.setColors(ColorTemplate.JOYFUL_COLORS);

        consultBarDataset.setDrawValues(true);
        consultBarData.setBarWidth(1.0f);

    }

}