package com.uva.inertia.besilite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RatingActivity extends AppCompatActivity {

    int r1,r2,r3,r4;
    TextView rv1, rv2, rv3, rv4;
    String[] answer = {"0","1","2","3","4","5","6","7","8","9","10"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        r1=0;
        r2=0;
        r3=0;
        r4=0;

        rv1 = findViewById(R.id.rateview1);
        rv1.setText(answer[r1%11]);
        rv2 = findViewById(R.id.rateview2);
        rv2.setText(answer[r2%11]);
        rv3 = findViewById(R.id.rateview3);
        rv3.setText(answer[r3%11]);
        rv4 = findViewById(R.id.rateview4);
        rv4.setText(answer[r4%11]);
    }

    public void incriment(View v){
        if (v.getId() == R.id.plusbutton1){
            r1++;
            r1 = checkBoundary(r1);
            rv1.setText(answer[r1%11]);
        }
        if (v.getId() == R.id.plusbutton2){
            r2++;
            r2 = checkBoundary(r2);
            rv2.setText(answer[r2%11]);

        }
        if (v.getId() == R.id.plusbutton3){
            r3++;
            r3 = checkBoundary(r3);
            rv3.setText(answer[r3%11]);

        }
        if (v.getId() == R.id.plusbutton4){
            r4++;
            r4 = checkBoundary(r4);
            rv4.setText(answer[r4%11]);
        }
    }

    public void decriment(View v){
        if (v.getId() == R.id.minusbutton1){
            r1--;
            r1 = checkBoundary(r1);
            rv1.setText(answer[r1%11]);
        }
        if (v.getId() == R.id.minusbutton2){
            r2--;
            r2 = checkBoundary(r2);
            rv2.setText(answer[r2%11]);

        }
        if (v.getId() == R.id.minusbutton3){
            r3--;
            r3 = checkBoundary(r3);
            rv3.setText(answer[r3%11]);

        }
        if (v.getId() == R.id.minusbutton4){
            r4--;
            r4 = checkBoundary(r4);
            rv4.setText(answer[r4%11]);
        }
    }
    public int checkBoundary (int num){
        if (num < 0){
            num = 0;
        } else if (num>10){
            num = 10;
        }
        return num;
    }
    public void backClick(View v){
//        Intent intent = new Intent(this.getApplicationContext(), Home.class);
//        startActivity(intent);
        finish();
    }

    public void submitClick(View v){
//        Intent intent = new Intent(this.getApplicationContext(), Home.class);
//        startActivity(intent);
        finish();

    }
}
