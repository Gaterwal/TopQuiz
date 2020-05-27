package com.example.topquiz.controller;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topquiz.R;
import com.example.topquiz.model.User;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private TextView mGreetingText;
    private EditText mNameInput;
    private Button mPlayButton;
    public static final int GAME_ACTIVITY_REQUEST_CODE = 0;
    private SharedPreferences mPreferences;



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final Switch simpleSwitch = findViewById(R.id.switchMain);
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });
        
        mPreferences = getPreferences(MODE_PRIVATE);

        initViews();

    }

    private void initViews()
    {
        //Récupérer les composants XML de activity_main.xml
        mGreetingText = findViewById(R.id.activity_main_greeting_txt);
        mNameInput = findViewById(R.id.activity_main_name_input);
        mPlayButton = findViewById(R.id.activity_main_play_btn);

        //Rendre le bouton "Let's play" grisé et non cliquable
        mPlayButton.setEnabled(false);
        mNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //Méthode appelée lorsque l'utilisateur appuie sur un touche dans la saisie
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Rendre le bouton disponible à partir d'un caractère
                mPlayButton.setEnabled(s.toString().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            //Methode appelée à chaque fois que l'utilisateur clique sur le bouton
            @Override
            public void onClick(View v) {
                //Récuperer le nom de l'utilisateur et l'attribuer
                String firstname = mNameInput.getText().toString();
                if (!firstname.equals("")) {

                    User user = loadUser(firstname);

                    if (user ==null)
                    {
                        user = saveUser(firstname);
                    }
                    // Nouvelle instance Intent pour faire le lien entre les deux classes

                    // Démarre le Intent et récupérer des données
                    //startActivity(gameActivityIntent);
                    startActivityForResult(GameActivity.newIntent(MainActivity.this, user), GAME_ACTIVITY_REQUEST_CODE);
                } else {
                    Toast.makeText(MainActivity.this, "Vous pouvez pas écrire une chaine libre", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private User saveUser(final String firstName) {

        //Seul l'appli aura accès au fichier
        mPreferences = getPreferences(MODE_PRIVATE);
        final User user = new User(firstName);
        SharedPreferences.Editor prefsEditor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("User_" + firstName, json);
        prefsEditor.apply();
        return user;

    }

    private User updateUser(final User user) {

        //Seul l'appli aura accès au fichier
        mPreferences = getPreferences(MODE_PRIVATE);

        SharedPreferences.Editor prefsEditor = mPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("User_" + user.getFirstName(), json);
        prefsEditor.apply();
        return user;
    }

    @Nullable // Annotation pour IDE, peut retourner null
    private User loadUser(final String firstName) {
        mPreferences = getPreferences(MODE_PRIVATE);
        final String userString = mPreferences.getString("User_"+firstName,null);
        if(userString==null)
        {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(userString, User.class);

    }


    //Permet de stocker/sauvegarder les données en cours
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            User user = data.getParcelableExtra("user");
            greetUser(user);
            user.saveScores();
            updateUser(user);


        }
    }

    private void greetUser(final User user) {
        int bestScore = user.getBestScore();
        int score  =user.getScore();
        if (score>bestScore)
        {
            bestScore = score;
        }

        final String fulltext = getResources().getString(R.string.greetuser,user.getFirstName(),score,user.getLastScore(), bestScore);
        mGreetingText.setText(fulltext);
        mNameInput.setText(user.getFirstName());
        mNameInput.setSelection(user.getFirstName().length());
        mPlayButton.setEnabled(true);
    }


}



