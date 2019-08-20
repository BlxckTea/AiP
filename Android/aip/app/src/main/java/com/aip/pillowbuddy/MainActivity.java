package com.aip.pillowbuddy;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {
    static final int TIME_MORNING = 0, TIME_AFTERNOON = 1, TIME_EVENING = 2, TIME_NIGHT = 3;

    FragmentPagerAdapter adapterViewPager;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private static int bgMode;
    //get current time
    private long mNow;
    private Date mDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("HH");

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser == null) {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
            return; //메인아예빠져나옴
        }


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);


        //calculate bgMode
        calcBgMode(Integer.parseInt(getTime())); //get hour value from Device

    }

    private void calcBgMode(int currentHour) {
        if(currentHour >= 6 && currentHour <= 11) bgMode = TIME_MORNING;
        else if(currentHour >= 12 && currentHour <= 16) bgMode = TIME_AFTERNOON;
        else if(currentHour >= 17 && currentHour <= 19) bgMode = TIME_EVENING;
        else if((currentHour >= 20 && currentHour <= 24)
                || (currentHour >= 0 && currentHour <= 5)) bgMode = TIME_NIGHT;
//        bgMode = TIME_AFTERNOON; //test //change bgMode
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        Fragment[] fragments = new Fragment[NUM_ITEMS];

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0 :
                    fragments[i] = FirstFragment.newInstance(0, "PAGE #1", bgMode);
                    return fragments[i];
                case 1 :
                    fragments[i] = SecondFragment.newInstance(1, "PAGE #2", bgMode);
                    return fragments[i];
                default :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page : " + position;
        }

    }
}
