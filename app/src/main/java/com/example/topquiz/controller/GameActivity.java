package com.example.topquiz.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.topquiz.R;
import com.example.topquiz.model.Question;
import com.example.topquiz.model.QuestionBank;
import com.example.topquiz.model.User;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mQuestionTextView;
    private Button mAnswerButton1;
    private Button mAnswerButton2;
    private Button mAnswerButton3;
    private Button mAnswerButton4;
    private QuestionBank mQuestionBank;
    private Question mCurrentQuestion;


    private int mNumberOfQuestions;
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
    public static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    public static final String BUNDLE_STATE_QUESTION = "BUNDLE_STATE_QUESTION";
    public static final String BUNDLE_STATE_QUESTION_BANK = "QuestionBank";
    public static final String BUNDLE_STATE_CURRENT_QUESTION = "CurrentQuestion";
    private boolean mEnableTouchEvents;
    private User mUser;

    public static Intent newIntent(final Context context,final User user) {
        Intent gameActivityIntent = new Intent(context, GameActivity.class);

        Bundle bundle= new Bundle();
        bundle.putParcelable("user", user);
        gameActivityIntent.putExtras(bundle);
        return gameActivityIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        final Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
        {
            mUser = bundle.getParcelable("user");

            if (mUser == null) {
                throw new IllegalArgumentException("user is required");
            }
        }else{
            throw new IllegalArgumentException("user is required");
        }
        initViews();
        //Si sauvegarde, afficher les données sauvegardées
        if (savedInstanceState != null) {


            mNumberOfQuestions = savedInstanceState.getInt(BUNDLE_STATE_QUESTION);
            mQuestionBank = savedInstanceState.getParcelable(BUNDLE_STATE_QUESTION_BANK);
            mCurrentQuestion = savedInstanceState.getParcelable(BUNDLE_STATE_CURRENT_QUESTION);
            this.displayQuestion(mCurrentQuestion);

        } else {
            mNumberOfQuestions = 4;
            mQuestionBank = this.generateQuestions();
            //Permet de récuperer la question et l'afficher
            mCurrentQuestion = mQuestionBank.getQuestion();
            this.displayQuestion(mCurrentQuestion);
        }
        mEnableTouchEvents = true;
    }

    private void initViews()
    {
        final Switch simpleSwitch = findViewById(R.id.switchActivity);
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });

        //Récuperer les id de activity_game.xml pour les mettre dans des variables
        mQuestionTextView = findViewById(R.id.activity_game_question_text);
        mAnswerButton1 = findViewById(R.id.activity_game_answer1_btn);
        mAnswerButton2 = findViewById(R.id.activity_game_answer2_btn);
        mAnswerButton3 = findViewById(R.id.activity_game_answer3_btn);
        mAnswerButton4 = findViewById(R.id.activity_game_answer4_btn);

        //Affilier un bouton à un tag = correspond à la réponse adéquate
        mAnswerButton1.setTag(0);
        mAnswerButton2.setTag(1);
        mAnswerButton3.setTag(2);
        mAnswerButton4.setTag(3);

        mAnswerButton1.setOnClickListener(this);
        mAnswerButton2.setOnClickListener(this);
        mAnswerButton3.setOnClickListener(this);
        mAnswerButton4.setOnClickListener(this);

    }

    private void displayQuestion(final Question question) {   //Affiche la question et les 4 réponses

        mQuestionTextView.setText(question.getQuestion());
        mAnswerButton1.setText(question.getChoiceList().get(0));
        mAnswerButton2.setText(question.getChoiceList().get(1));
        mAnswerButton3.setText(question.getChoiceList().get(2));
        mAnswerButton4.setText(question.getChoiceList().get(3));

    }

    // Permet de sauvegarder le score
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BUNDLE_STATE_QUESTION, mNumberOfQuestions);
        //Sauvegarde la question et la banque de question
        outState.putParcelable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
        outState.putParcelable(BUNDLE_STATE_CURRENT_QUESTION, mCurrentQuestion);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        int responseIndex = (int) v.getTag();
        if (responseIndex == mCurrentQuestion.getAnswerIndex()) {
            //Afficher en cas de bonne réponse "Bonne réponse". Toast = message éphémère en bas
            Toast.makeText(this, (getString(R.string.goodA)), Toast.LENGTH_SHORT).show();
            mUser.incrementScore();
        } else {
            Toast.makeText(this, (getString(R.string.badA)), Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents = false;
        //Methode Temporisation de 2 secondes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mEnableTouchEvents = true;
                //Si derniere question, endgame() sinon, affiche question suivante
                // Nombre de questions posées
                if (--mNumberOfQuestions == 0) {
                    endGame();
                } else {
                    mCurrentQuestion = mQuestionBank.getQuestion();
                    displayQuestion(mCurrentQuestion);
                }
            }
        }, 2000);
    }

    //Empeche d'interagir avec l'écran pendant les deux secondes
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    private void endGame() {

        //Nouvelle instance d'alerte et affichage
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString((R.string.congrats))).setMessage(getString((R.string.score)) + mUser.getScore() + getString((R.string.onFour))).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Nouvelle instance intent
                Intent intent = new Intent();
                //Attache le score à l'intent avec la clé BUNDLE_EXTRA_SCORE
                intent.putExtra("user",mUser);
                // Permet d'enregistrer l'intent auprès du systeme Android avant d'envoyer au MainActivity
                setResult(RESULT_OK, intent);

                //Termine l'activité
                finish();
            }
        })
                .create().show();
    }

    private QuestionBank generateQuestions() {
        Question question1 = new Question((getString(R.string.question1)), Arrays.asList(getString(R.string.answer1),
                getString(R.string.answer2), getString(R.string.answer3), getString(R.string.answer4)),
                1);
        Question question2 = new Question((getString(R.string.question2)), Arrays.asList(getString(R.string.answer21),
                getString(R.string.answer22), getString(R.string.answer23), getString(R.string.answer24)),
                2);
        Question question3 = new Question((getString(R.string.question3)), Arrays.asList(getString(R.string.answer31),
                getString(R.string.answer32), getString(R.string.answer33), getString(R.string.answer34)),
                0);
        Question question4 = new Question((getString(R.string.question4)), Arrays.asList(getString(R.string.answer41),
                getString(R.string.answer42), getString(R.string.answer43), getString(R.string.answer44)),
                3);
        Question question5 = new Question((getString(R.string.question5)), Arrays.asList(getString(R.string.answer51),
                getString(R.string.answer52), getString(R.string.answer53), getString(R.string.answer54)),
                2);
        Question question6 = new Question((getString(R.string.question6)), Arrays.asList(getString(R.string.answer61),
                getString(R.string.answer62), getString(R.string.answer63), getString(R.string.answer64)),
                1);
        Question question7 = new Question((getString(R.string.question7)), Arrays.asList(getString(R.string.answer71),
                getString(R.string.answer72), getString(R.string.answer73), getString(R.string.answer74)),
                0);
        Question question8 = new Question((getString(R.string.question8)), Arrays.asList(getString(R.string.answer81),
                getString(R.string.answer82), getString(R.string.answer83), getString(R.string.answer84)),
                3);
        Question question9 = new Question((getString(R.string.question9)), Arrays.asList(getString(R.string.answer91),
                getString(R.string.answer92), getString(R.string.answer93), getString(R.string.answer94)),
                0);
        return new QuestionBank(Arrays.asList(question1,
                question2,
                question3,
                question4,
                question5,
                question6,
                question7,
                question8,
                question9));
    }

}
