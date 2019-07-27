package com.aip.pillowbuddy;

import android.content.Context;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirstFragment extends Fragment {
    private Integer[] bgIDs = {R.drawable.bg_morning, R.drawable.bg_afternoon, R.drawable.bg_evening, R.drawable.bg_night};
    private Integer[] mtIDs = {R.drawable.ff_morning_02, R.drawable.ff_afternoon_02, R.drawable.ff_evening_02, R.drawable.ff_night_02};
    private Integer[] skyIDs = {R.drawable.ff_morning_01, R.drawable.ff_afternoon_01, R.drawable.ff_evening_01, R.drawable.ff_night_01};
    private Integer[] footIDs = {R.color.colorMorning, R.color.colorAfternoon, R.color.colorEvening, R.color.colorNight};

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseDatabase mFirebaseDatabase;

    private boolean refreshOnce = true;
    private boolean refreshSwitch;

    TimePicker timePicker;
    View dialogView;
    Integer tpHour, tpMinute;
    MyAlarm myAlarm = new MyAlarm(6, 30, false);

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

        initAlarm();

        LinearLayout linearLayoutBg = (LinearLayout) view.findViewById(R.id.linearRoot);
        LinearLayout linearLayoutFoot = (LinearLayout) view.findViewById(R.id.linearFoot);
        LinearLayout linearTvs = (LinearLayout) view.findViewById(R.id.linearTvs);
        ImageView ivSky = (ImageView) view.findViewById(R.id.ivSky);
        ImageView ivMountain = (ImageView) view.findViewById(R.id.ivMountain);
        final TextView tvFoot = (TextView) view.findViewById(R.id.tvFoot);
        final TextView tvHour = (TextView) view.findViewById(R.id.tvHour);
        final TextView tvMinute = (TextView) view.findViewById(R.id.tvMin);
        final TextView tvColon = (TextView) view.findViewById(R.id.tvColon);
        Switch switchAlarm = (Switch) view.findViewById(R.id.switchAlarm);

        //set resources
        linearLayoutBg.setBackgroundResource(bgIDs[bgMode]);
        linearLayoutFoot.setBackgroundResource(footIDs[bgMode]);
        ivSky.setImageResource(skyIDs[bgMode]);
        ivMountain.setImageResource(mtIDs[bgMode]);

        Log.i("yeji", "4 onCreateView myAlarm's IntHour: "+myAlarm.getIntHour());
        Log.i("yeji", "5 onCreateView myAlarm's IntMinute: "+myAlarm.getIntMinute());
        Log.i("yeji", "6 onCreateView myAlarm's SwitchIsOn: "+myAlarm.isSwitchIsOn());

        //init widget
        tpHour = myAlarm.getIntHour(); //TODO get the each value from DB
        tpMinute = myAlarm.getIntMinute();
        Log.d("yeji", "tpHour: "+tpHour);
        Log.d("yeji", "tpMinute: "+tpMinute);

        tvHour.setText(tpHour<10? "0"+tpHour.toString(): tpHour.toString());
        tvMinute.setText(tpMinute<10? "0"+tpMinute.toString(): tpMinute.toString());
        switchAlarm.setChecked(myAlarm.isSwitchIsOn());
        if (switchAlarm.isChecked()) {
            tvFoot.setText(myAlarm.getIntHour().toString() + ":" + myAlarm.getIntMinute().toString() + "에 깨워드릴게요."); //6:00(init)
            tvHour.setTextColor(Color.WHITE);
            tvMinute.setTextColor(Color.WHITE);
            tvColon.setTextColor(Color.WHITE);
        } else {
            tvFoot.setText("알람이 설정되어있지 않습니다.");
            tvHour.setTextColor(Color.GRAY);
            tvMinute.setTextColor(Color.GRAY);
            tvColon.setTextColor(Color.GRAY);
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
                        TextView tvHour = (TextView) viewFromFrag.findViewById(R.id.tvHour);
                        TextView tvMinute = (TextView) viewFromFrag.findViewById(R.id.tvMin);
                        TextView tvColon = (TextView) viewFromFrag.findViewById(R.id.tvColon);
                        Switch switchAlarm = (Switch) viewFromFrag.findViewById(R.id.switchAlarm);

                        tpHour = timePicker.getCurrentHour();
                        tpMinute = timePicker.getCurrentMinute();
                        myAlarm.setIntHour(tpHour);
                        myAlarm.setIntMinute(tpMinute);
                        myAlarm.setSwitchIsOn(true);

                        tvHour.setText(tpHour<10? "0"+tpHour.toString(): tpHour.toString());
                        tvMinute.setText(tpMinute<10? "0"+tpMinute.toString(): tpMinute.toString());
                        switchAlarm.setChecked(myAlarm.isSwitchIsOn());
                        tvHour.setTextColor(Color.WHITE);
                        tvMinute.setTextColor(Color.WHITE);
                        tvColon.setTextColor(Color.WHITE);

                        String strH = tpHour<10? "0"+tpHour.toString(): tpHour.toString();
                        String strM = tpMinute<10? "0"+tpMinute.toString(): tpMinute.toString();
                        tvFoot.setText(strH + ":" + strM + "에 깨워드릴게요.");
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

        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    Integer intH = myAlarm.getIntHour();
                    Integer intM = myAlarm.getIntMinute();
                    String strH = intH<10? "0"+intH.toString(): intH.toString();
                    String strM = intM<10? "0"+intM.toString(): intM.toString();
                    tvFoot.setText(strH + ":" + strM + "에 깨워드릴게요.");
                    tvHour.setTextColor(Color.WHITE);
                    tvMinute.setTextColor(Color.WHITE);
                    tvColon.setTextColor(Color.WHITE);
                    Toast.makeText(getContext().getApplicationContext(), "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show();

                    myAlarm.setSwitchIsOn(true);
                    updateAlarm();

                } else {
                    tvFoot.setText("알람이 설정되어있지 않습니다.");
                    tvHour.setTextColor(Color.GRAY);
                    tvMinute.setTextColor(Color.GRAY);
                    tvColon.setTextColor(Color.GRAY);
                    Toast.makeText(getContext().getApplicationContext(), "알람이 해제되었습니다.", Toast.LENGTH_SHORT).show();

                    myAlarm.setSwitchIsOn(false);
                    updateAlarm();
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

    private void initAlarm() {
        //db저장된거 가져와서 myAlarm값 바꿔주기 db에는 쓰기x
        mFirebaseDatabase.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("android") == false) {
                    Log.d("yeji", "initAlarm 알람없음: "+dataSnapshot.child("android").getChildrenCount());

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/android", myAlarm);
                    mFirebaseDatabase.getReference().updateChildren(childUpdates);
                    mFirebaseDatabase.getReference().child("switch").child("measureSwitch").setValue(myAlarm.isSwitchIsOn()?"True":"False"); //문자열 boolean으로 변환
                }
                else {
                    Log.d("yeji", "initAlarm 알람있음: "+dataSnapshot.child("android").getChildrenCount());

                    for(DataSnapshot s : dataSnapshot.getChildren()) {
//                        String key = s.getKey();
//                        MyAlarm ma = s.getValue(MyAlarm.class); //Long Integer Object 변환 불가 error
                        /*//test PASS
                        Log.i("yeji", "initAlarm print key value: "+key);
                        Log.i("yeji", "initAlarm print intHour: "+s.child("intHour").getValue());
                        Log.i("yeji", "initAlarm print intMinute: "+s.child("intMinute").getValue());
                        Log.i("yeji", "initAlarm print switchIsOn: "+s.child("switchIsOn").getValue());*/

                        Long lHour = (Long) s.child("intHour").getValue();
                        Long lMinute = (Long) s.child("intMinute").getValue();
//                        boolean bSwitch = (Boolean) s.child("switchIsOn").getValue();
                        boolean bSwitch; //파베 measureSwitch값이 문자열이라 boolean으로 변환
                        if(dataSnapshot.child("switch").child("measureSwitch").getValue() == "True") bSwitch = true;
                        else bSwitch = false;

                        Integer iHour = lHour.intValue();
                        Integer iMinute = lMinute.intValue();
//                        myAlarm = new MyAlarm(iHour, iMinute, bSwitch);
                        myAlarm.setIntHour(iHour);
                        myAlarm.setIntMinute(iMinute);
                        myAlarm.setSwitchIsOn(bSwitch);

                        //test PASS
                        Log.i("yeji", "1 initAlarm myAlarm's IntHour: "+myAlarm.getIntHour());
                        Log.i("yeji", "2 initAlarm myAlarm's IntMinute: "+myAlarm.getIntMinute());
                        Log.i("yeji", "3 initAlarm myAlarm's SwitchIsOn: "+myAlarm.isSwitchIsOn());

                        if(iHour != null) break; //찾았으면 한번더돌아서 null찾지않도록 빠져나감
                    }
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
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/android", myAlarm);
        mFirebaseDatabase.getReference().updateChildren(childUpdates);
        mFirebaseDatabase.getReference().child("switch").child("measureSwitch").setValue(myAlarm.isSwitchIsOn()?"True":"False"); //문자열 boolean으로 변환

        mFirebaseDatabase.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("android")) Log.d("yeji", "updateAlarm 알람있음: "+dataSnapshot.child("android").getChildrenCount());
                else Log.d("yeji", "updateAlarm 알람없음: "+dataSnapshot.child("android").getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("yeji", "updateAlarm onCancelled:", databaseError.toException());
            }
        });
    }

}
