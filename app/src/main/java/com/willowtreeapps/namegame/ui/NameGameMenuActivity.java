package com.willowtreeapps.namegame.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.willowtreeapps.namegame.R;
import com.willowtreeapps.namegame.core.GameMode;
import com.willowtreeapps.namegame.core.NameGameApplication;
import com.willowtreeapps.namegame.core.NameGameManager;

import javax.inject.Inject;

public class NameGameMenuActivity extends AppCompatActivity {
    @Inject
    NameGameManager gameManager;

    String rdoSelectedText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_game_menu_activity);
        NameGameApplication.get(this).component().inject(this);

        Button button = (Button)findViewById(R.id.btnStart);
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.rdogrpMode);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rdoButton = (RadioButton)group.findViewById(checkedId);
                String rdoText = rdoButton.getText().toString();
                setRdoSelectedId(rdoText);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NameGameMenuActivity.this,NameGameActivity.class);
                RadioGroup rdoGroup = (RadioGroup)findViewById(R.id.rdogrpMode);
                RadioButton selectedButton = (RadioButton)findViewById(rdoGroup.getCheckedRadioButtonId());
                gameManager.setGameMode(GameMode.getState(selectedButton.getText().toString()));
                gameManager.startNewGame();
                startActivity(intent);
            }
        });
    }
    private void setRdoSelectedId(String rdoSelectedText){
        this.rdoSelectedText=rdoSelectedText;
    }
}
