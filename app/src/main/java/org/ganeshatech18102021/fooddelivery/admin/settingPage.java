package org.ganeshatech18102021.fooddelivery.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.ganeshatech18102021.fooddelivery.R;

public class settingPage extends AppCompatActivity {

    Button abt,help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        abt = findViewById(R.id.about_setting);
        help = findViewById(R.id.help_setting);

        abt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(settingPage.this,aboutsetting.class));
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(settingPage.this,helpsetting.class));
            }
        });
    }
}