package com.aip.pillowbuddy;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirstFragment extends Fragment {
    private Integer[] bgIDs = {R.drawable.bg_morning, R.drawable.bg_afternoon, R.drawable.bg_evening, R.drawable.bg_night};
    private Integer[] mtIDs = {R.drawable.ff_morning_02, R.drawable.ff_afternoon_02, R.drawable.ff_evening_02, R.drawable.ff_night_02};
    private Integer[] skyIDs = {R.drawable.ff_morning_01, R.drawable.ff_afternoon_01, R.drawable.ff_evening_01, R.drawable.ff_night_01};
    private Integer[] footIDs = {R.color.colorMorning, R.color.colorAfternoon, R.color.colorEvening, R.color.colorNight};

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;

    private boolean refreshOnce = true;

    TimePicker timePicker;
    View dialogView;
    Integer tpHour, tpMinute;
    public static MyAlarm myAlarm = new MyAlarm(00, 00, false);
    private String title;
    private int page;
    private int bgMode;


    public static FirstFragment newInstance(int page, String title, int bgMode) {
        FirstFragment fragment = new FirstFragment();
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

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        final View viewInit = view;
        if(refreshOnce) initAlarm(viewInit);

        LinearLayout linearLayoutBg = (LinearLayout) view.findViewById(R.id.linearRoot);
        LinearLayout linearLayoutFoot = (LinearLayout) view.findViewById(R.id.linearFoot);
        LinearLayout linearTvs = (LinearLayout) view.findViewById(R.id.linearTvs);
        ImageView ivSky = (ImageView) view.findViewById(R.id.ivSky);
        ImageView ivMountain = (ImageView) view.findViewById(R.id.ivMountain);
        final TextView tvFoot = (TextView) view.findViewById(R.id.tvFoot);
        final TextView tvAmpm = (TextView) view.findViewById(R.id.tvAmpm);
        final TextView tvHour = (TextView) view.findViewById(R.id.tvHour);
        final TextView tvMinute = (TextView) view.findViewById(R.id.tvMin);
        final TextView tvColon = (TextView) view.findViewById(R.id.tvColon);
        final Switch switchMeasure = (Switch) view.findViewById(R.id.switchMeasure);
        final Switch switchDriveSleep = (Switch) view.findViewById(R.id.switchDriveSleep);

        //set resources
        linearLayoutBg.setBackgroundResource(bgIDs[bgMode]);
        linearLayoutFoot.setBackgroundResource(footIDs[bgMode]);
        ivSky.setImageResource(skyIDs[bgMode]);
        ivMountain.setImageResource(mtIDs[bgMode]);
        switchMeasure.setTextColor(Color.WHITE);
        switchDriveSleep.setTextColor(Color.WHITE);

        //ACCESSIBILITY LABEL
        //set contentDescription
        linearLayoutBg.setContentDescription("현재 화면은 '알람설정' 및 '수면유도 기능'을 사용할 수 있습니다. '컨설팅결과 및 수면 그래프' 확인은 화면을 오른쪽으로 넘겨주세요.");
        if(myAlarm.isMeasureSwitch()) {
            linearTvs.setContentDescription(myAlarm.getAP() + " " + myAlarm.getHour() + "시" + myAlarm.getMinutes() + "분에 활성화 되어있습니다. 시간을 다시 설정하시려면 두번 터치하세요.");
        } else {
            linearTvs.setContentDescription(myAlarm.getAP() + " " + myAlarm.getHour() + "시" + myAlarm.getMinutes() + "분에 비활성화 되어있습니다. 알람을 설정하시려면 오른쪽의 스위치를 활성화하세요.");
        }
        linearTvs.setContentDescription("설정된 알람 시각은" + myAlarm.getAP()+myAlarm.getHour()+"시"+myAlarm.getMinutes()+"분 입니다. 재설정 하시겠습니까?");
        switchMeasure.setContentDescription("알람설정 스위치 입니다.");
        switchDriveSleep.setContentDescription("수면유도기능 스위치 입니다.");


        Log.i("yeji", "4 onCreateView myAlarm's IntHour: "+myAlarm.getHour());
        Log.i("yeji", "5 onCreateView myAlarm's IntMinute: "+myAlarm.getMinutes());
        Log.i("yeji", "6 onCreateView myAlarm's SwitchIsOn: "+myAlarm.isMeasureSwitch());

        //init widget
        tpHour = myAlarm.getHour(); //get the each value from DB
        tpMinute = myAlarm.getMinutes();
        Log.d("yeji", "tpHour: "+tpHour);
        Log.d("yeji", "tpMinute: "+tpMinute);

        tvAmpm.setText(myAlarm.getAP());
        if(tpHour==0) tpHour=12;
        tvHour.setText(tpHour<10? "0"+tpHour.toString(): tpHour.toString());
        tvMinute.setText(tpMinute<10? "0"+tpMinute.toString(): tpMinute.toString());


        Log.i("yeji", "ONCREATED SAFEMS IS: "+myAlarm.isSafeMs());
        Log.i("yeji", "ONCREATED MEASURESWITCH IS: "+myAlarm.isMeasureSwitch());

        if (switchMeasure.isChecked() && !refreshOnce) {
            tvFoot.setText(myAlarm.getAP() + " " + myAlarm.getHour().toString() + ":" + myAlarm.getMinutes().toString() + "에 깨워드릴게요."); //0:00(init)
            tvAmpm.setTextColor(Color.WHITE);
            tvHour.setTextColor(Color.WHITE);
            tvMinute.setTextColor(Color.WHITE);
            tvColon.setTextColor(Color.WHITE);
            switchDriveSleep.setVisibility(View.VISIBLE);
        } else if (!switchMeasure.isChecked() && !refreshOnce) {
            tvFoot.setText("알람이 설정되어있지 않습니다.");
            tvAmpm.setTextColor(Color.GRAY);
            tvHour.setTextColor(Color.GRAY);
            tvMinute.setTextColor(Color.GRAY);
            tvColon.setTextColor(Color.GRAY);
            switchDriveSleep.setVisibility(View.INVISIBLE);
        }

        //open dialog
        final View viewFromFrag = view;
        linearTvs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialogView = (View) View.inflate(getActivity(), R.layout.dialog_alarm, null);
                timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

                dlg.setView(dialogView);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView tvAmpm = (TextView) viewFromFrag.findViewById(R.id.tvAmpm);
                        TextView tvHour = (TextView) viewFromFrag.findViewById(R.id.tvHour);
                        TextView tvMinute = (TextView) viewFromFrag.findViewById(R.id.tvMin);
                        TextView tvColon = (TextView) viewFromFrag.findViewById(R.id.tvColon);
                        Switch switchMeasure = (Switch) viewFromFrag.findViewById(R.id.switchMeasure);

                        tpHour = timePicker.getCurrentHour();
                        tpMinute = timePicker.getCurrentMinute();
                        myAlarm.setAP(tpHour>12? "오후": "오전");
                        myAlarm.setHour(tpHour);
                        myAlarm.setMinutes(tpMinute);
                        myAlarm.setMeasureSwitch(true);

                        tvAmpm.setText(myAlarm.getAP() + " ");
                        tvHour.setText(myAlarm.getHour()<10? "0"+myAlarm.getHour().toString(): myAlarm.getHour().toString());
                        tvMinute.setText(myAlarm.getMinutes()<10? "0"+myAlarm.getMinutes().toString(): myAlarm.getMinutes().toString());
                        switchMeasure.setChecked(myAlarm.isMeasureSwitch());
                        switchDriveSleep.setVisibility(View.VISIBLE);
                        tvAmpm.setTextColor(Color.WHITE);
                        tvHour.setTextColor(Color.WHITE);
                        tvMinute.setTextColor(Color.WHITE);
                        tvColon.setTextColor(Color.WHITE);

//                        String strH = tpHour<10? "0"+tpHour.toString(): tpHour.toString();
//                        String strM = tpMinute<10? "0"+tpMinute.toString(): tpMinute.toString();
                        tvFoot.setText(myAlarm.getAP() + " " + myAlarm.getHour() + "시 " + (myAlarm.getMinutes()<10? "0"+myAlarm.getMinutes().toString(): myAlarm.getMinutes().toString()) + "분에 깨워드릴게요.\n좋은 꿈 꾸세요:)");
                        Toast.makeText(getContext().getApplicationContext(), "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();

                        //update firebase data
                        updateAlarm();
                    }
                });
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext().getApplicationContext(), "취소했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.show();
            }
        });


        switchMeasure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true && !refreshOnce){
//                    Integer intH = myAlarm.getHour();
//                    Integer intM = myAlarm.getMinutes();
//                    String strH = intH<10? "0"+intH.toString(): intH.toString();
//                    String strM = intM<10? "0"+intM.toString(): intM.toString();
                    tvFoot.setText(myAlarm.getAP() + " " + myAlarm.getHour() + "시 " + (myAlarm.getMinutes()<10? "0"+myAlarm.getMinutes().toString(): myAlarm.getMinutes().toString()) + "분에 깨워드릴게요.\n좋은 꿈 꾸세요:)");
                    tvAmpm.setTextColor(Color.WHITE);
                    tvHour.setTextColor(Color.WHITE);
                    tvMinute.setTextColor(Color.WHITE);
                    tvColon.setTextColor(Color.WHITE);
                    Toast.makeText(getContext().getApplicationContext(), "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();

                    myAlarm.setMeasureSwitch(true);
                    updateAlarm();
                    switchDriveSleep.setEnabled(true);
                    switchDriveSleep.setTextColor(Color.WHITE);
                    switchDriveSleep.setVisibility(View.VISIBLE);
                    mFirebaseDatabase.getReference().child("switch").child("sleepMusic").setValue("True");

                } else if(b == false && !refreshOnce) {
                    tvFoot.setText("알람이 설정되어있지 않습니다.");
                    tvAmpm.setTextColor(Color.GRAY);
                    tvHour.setTextColor(Color.GRAY);
                    tvMinute.setTextColor(Color.GRAY);
                    tvColon.setTextColor(Color.GRAY);
                    Toast.makeText(getContext().getApplicationContext(), "알람이 해제되었습니다.", Toast.LENGTH_SHORT).show();

                    myAlarm.setMeasureSwitch(false);
                    updateAlarm();
                    switchDriveSleep.setEnabled(false);
                    switchDriveSleep.setTextColor(Color.GRAY);
                    switchDriveSleep.setVisibility(View.INVISIBLE);
                    mFirebaseDatabase.getReference().child("switch").child("sleepMusic").setValue("False");
                }
            }
        });

        if(!switchMeasure.isChecked()) switchDriveSleep.setEnabled(false);
        else switchDriveSleep.setEnabled(true);
        switchDriveSleep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true && !refreshOnce){
                    switchDriveSleep.setTextColor(Color.WHITE);
                    mFirebaseDatabase.getReference().child("switch").child("sleepMusic").setValue("True");
                    Toast.makeText(getContext().getApplicationContext(), "수면 유도 음악이 설정되었습니다.", Toast.LENGTH_SHORT).show();

                } else if(b == false && !refreshOnce) {
                    switchDriveSleep.setTextColor(Color.GRAY);
                    mFirebaseDatabase.getReference().child("switch").child("sleepMusic").setValue("False");
                    Toast.makeText(getContext().getApplicationContext(), "알람이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void refresh() {
//        Log.i("yeji", "refreshOnce: "+refreshOnce);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
        refreshOnce = false;
    }

    private void initAlarm(final View viewInit) {
        //db저장된거 가져와서 myAlarm값 바꿔주기 db에는 쓰기x
        mFirebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("data") == false) {
                    Log.d("yeji", "initAlarm 알람없음: "+dataSnapshot.child("data").getChildrenCount());

//                    Map<String, Object> childUpdates = new HashMap<>();
//                    childUpdates.put("/data", myAlarm);
//                    mFirebaseDatabase.getReference().updateChildren(childUpdates);
                    mFirebaseDatabase.getReference().child("data").child("AP").setValue(myAlarm.getAP());
                    mFirebaseDatabase.getReference().child("data").child("hour").setValue(myAlarm.getHour().toString() + "시");
                    mFirebaseDatabase.getReference().child("data").child("minutes").setValue(myAlarm.getMinutes().toString() + "분");
                    mFirebaseDatabase.getReference().child("switch").child("measureSwitch").setValue(myAlarm.isSafeMs()?"True":"False"); //문자열 boolean으로 변환
                }
                else {
                    Log.d("yeji", "initAlarm 알람있음: "+dataSnapshot.child("data").getChildrenCount());

//                    for(DataSnapshot s : dataSnapshot.getChildren()) {
//                        String key = s.getKey();
//                        MyAlarm ma = s.getValue(MyAlarm.class); //Long Integer Object 변환 불가 error
                        //test PASS
//                        Log.i("yeji", "initAlarm print key value: "+key);
//                        if(key == "data") {
                            Log.i("yeji", "initAlarm print intHour: "+dataSnapshot.child("data/hour").getValue());
                            Log.i("yeji", "initAlarm print intMinute: "+dataSnapshot.child("data/minute").getValue());
                            Log.i("yeji", "initAlarm print switchIsOn: "+dataSnapshot.child("switch").child("switchIsOn").getValue());

                            String strAP = (String) dataSnapshot.child("data/AP").getValue();
                            String strHour = (String) dataSnapshot.child("data/hour").getValue();
                            String strMinutes = (String) dataSnapshot.child("data/minutes").getValue();
                            strHour = strHour.replace("시",""); //파베값 문자열에 시,분 붙어있어서 제거해줌
                            strMinutes = strMinutes.replace("분","");

                            myAlarm.setAP(strAP);
                            myAlarm.setHour(Integer.parseInt(strHour));
                            myAlarm.setMinutes(Integer.parseInt(strMinutes));

                            String strSwitch = (String) dataSnapshot.child("switch").child("measureSwitch").getValue();
                            boolean bSwitch; //파베 measureSwitch값이 문자열이라 boolean으로 변환
                            if(strSwitch.equals("True")) bSwitch = true;
                            else bSwitch = false;
                            myAlarm.setMeasureSwitch(bSwitch);

                            //View로 받아 스위치 체크해주기
                            Switch switchMeasure = (Switch) viewInit.findViewById(R.id.switchMeasure);
                            switchMeasure.setChecked(myAlarm.isMeasureSwitch());

                            Log.i("yeji", "INIT BSWITCH IS: "+bSwitch);
                            Log.i("yeji", "INIT SAFEMS IS: "+myAlarm.isSafeMs());
                            Log.i("yeji", "INIT MEASURESWITCH IS: "+myAlarm.isMeasureSwitch());
                            Log.i("yeji", "INIT SWITCHMS IS: "+ switchMeasure.isChecked());

                            //test PASS
                            Log.i("yeji", "1 initAlarm myAlarm's IntHour: "+myAlarm.getHour());
                            Log.i("yeji", "2 initAlarm myAlarm's IntMinute: "+myAlarm.getMinutes());
                            Log.i("yeji", "3 initAlarm myAlarm's SwitchIsOn: "+myAlarm.isMeasureSwitch());

//                            if(strHour != null) break; //찾았으면 한번더돌아서 null찾지않도록 빠져나감
//                        }
//                    }

                }
                //fragment refresh
                if(refreshOnce) refresh();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("yeji", "updateAlarm onCancelled:", databaseError.toException());
            }
        });
    }

    private void updateAlarm() {
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/data", myAlarm);
//        mFirebaseDatabase.getReference().updateChildren(childUpdates);
        mFirebaseDatabase.getReference().child("data").child("AP").setValue(myAlarm.getAP());
        mFirebaseDatabase.getReference().child("data").child("hour").setValue(myAlarm.getHour().toString() + "시");
        mFirebaseDatabase.getReference().child("data").child("minutes").setValue(myAlarm.getMinutes().toString() + "분");
        mFirebaseDatabase.getReference().child("switch").child("measureSwitch").setValue(myAlarm.isMeasureSwitch()?"True":"False"); //문자열 boolean으로 변환

        mFirebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("data")) Log.d("yeji", "updateAlarm 알람있음: "+dataSnapshot.child("data").getChildrenCount());
                else Log.d("yeji", "updateAlarm 알람없음: "+dataSnapshot.child("data").getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("yeji", "updateAlarm onCancelled:", databaseError.toException());
            }
        });
    }

}
