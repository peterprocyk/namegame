package com.willowtreeapps.namegame.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.core.NameGameManager;

import javax.inject.Inject;

public class NameGameResultsActivity extends AppCompatActivity{

    @Inject
    NameGameManager gameManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_game_results_activity);
        NameGameApplication.get(this).component().inject(this);

        TextView txtTime = (TextView)findViewById(R.id.txtTotalTime);
        TextView txtAvgTime = (TextView)findViewById(R.id.txtAvgTime);
        TextView txtLongestTime = (TextView)findViewById(R.id.txtLongestTime);
        TextView txtMode = (TextView)findViewById(R.id.txtMode);
        TextView txtNumWrong = (TextView)findViewById(R.id.txtNumWrong);
        TextView txtScore = (TextView)findViewById(R.id.txtScore);
        Button btnContinue = (Button)findViewById(R.id.btnContinue);

        txtAvgTime.setKeyListener(null);
        txtLongestTime.setKeyListener(null);
        txtMode.setKeyListener(null);
        txtNumWrong.setKeyListener(null);
        txtScore.setKeyListener(null);
        txtTime.setKeyListener(null);

        txtTime.append(" "+gameManager.getTotalTime()+" seconds");
        txtAvgTime.append(" "+gameManager.getAverageTime()+ " seconds");
        txtLongestTime.append(" "+gameManager.getLongestRound()+" seconds");
        txtMode.append(" "+gameManager.getGameMode().toString());
        txtNumWrong.append(" "+gameManager.getNumWrong());
        txtScore.append(" "+gameManager.getScore());

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
