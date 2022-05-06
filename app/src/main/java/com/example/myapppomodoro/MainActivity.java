package com.example.myapppomodoro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //Объявили все элементы MainActivity====
    private Button btnStartStopMainAct;
    private ImageButton imbtnSettingsMainAct;
    private TextView tvPomodoroMainAct;
    private TextView tvTimeMainAct;
    //====

    //Для работы с таймером====
    private CountDownTimer countDownTimer; //Для работы с таймером
    private long timeLeftInMillSeconds = 600_0; //Начальное значение таймера - 10:00
    private boolean timerRunning = false; //Показатель on/off таймера
    //====

    //Для работы с рингтоном====
    Ringtone ringtone;
    Uri notificationUri;
    //====

    //Данные для логики Pomodoro==
        private int workTime;
        private int shortBreak;
        private int longBreak;
        private int cycles;

        private EditText edTxtWorkTime;
        private EditText edTxtShortBreak;
        private EditText edTxtLongBreak;
        private EditText edTxtCycles;

        private Button testbtn; //TODO: удалить потом

    //====

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        findRingtone();
        btnStartStopMainAct.setOnClickListener(v -> {
            startstop();

            //Одним из функционалом кнопки startstop является отключение мелодии
            //Если таймер сработал, то играет мелодия, то мы её отключаем, иначе нет
            if (ringtone.isPlaying()) { finishRingtone(); }
        });
        imbtnSettingsMainAct.setOnClickListener(v -> {
            //Создаём диалог настроек, при нажатии на кнпоку настройки:
            SettingDialog(); });

    }


    private void init() { //Находим все элементы на MainActivity:
        //Для работы с таймером:
        btnStartStopMainAct = findViewById(R.id.btn_startstop_mainAct);
        tvTimeMainAct = findViewById(R.id.tv_time_mainAct);

        imbtnSettingsMainAct = findViewById(R.id.imbtn_settings_mainAct); //Кнопка настройки
        tvPomodoroMainAct = findViewById(R.id.tv_pomodoro_mainAct); //Главная надпись TODO: она быть может совсем не нужна для изменения - удалить значит
    }

    public void startstop() { //TODO: по поводу модификатора доступа подумать - нужен ли вообще public? private - may be? - ВО ВСЕХ МЕТОДАХ
        if (timerRunning) { //Если таймер включён - выключаем / выключен - включаем <- при нажатии на кнопку startstop
            stopTimer();
        } else {
            startTimer();
        }
    }

    public void startTimer() { //Логика старта таймера
        countDownTimer = new CountDownTimer(timeLeftInMillSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { //Что делаем, когда прошел тик
                timeLeftInMillSeconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() { //Что делаем при завершении работы таймера
                ringtone.play();
                btnStartStopMainAct.setText("FINISH!");
            }
        }.start();

        btnStartStopMainAct.setText("PAUSE");
        timerRunning = true; //Показываем, что таймер идёт
    }

    public void stopTimer() { //Логика остановки таймера
        countDownTimer.cancel(); //Приостанавливаем таймер (останавливаем - следуя из логики письма)
        btnStartStopMainAct.setText("START");
        timerRunning = false; //Показываем, что таймер остановился
    }

    public void updateTimer() { //Логика обновление циферблата
        int minutes = (int) timeLeftInMillSeconds / 60_000;
        int seconds = (int) timeLeftInMillSeconds % 60_000 / 1000;

        String timeLefText; //Формируем строку вида 10:00

        timeLefText = "" + minutes;
        timeLefText += ":";

        if (seconds < 10) { //Избегаем случай 10:5 -> 10:05
            timeLefText += "0";
        }

        timeLefText += seconds;

        tvTimeMainAct.setText(timeLefText); //Сформированную строку показываем на циферблате пользователю
    }

    public void findRingtone() { //Ищём рингтон из телефона пользователя: TODO: Добавить нормальное и правильное описание ниже написанных строк
        //Проиграем мелодию, установленную в системе по умолчанию:
        //Запрашиваем у RingtoneManager дефолтные Uri для звука будильника
        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        //А теперь сам рингтонл
        ringtone = RingtoneManager.getRingtone(this, notificationUri);

        //Если рингтона по умолчанию нет, то попробуем достать звук звонка:
        if (ringtone == null) {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(this, notificationUri);
        }
    }

    public void finishRingtone() { //Выключение проигрывания рингтона:
        if (ringtone != null) {
            ringtone.stop();
        }
    }

    public void SettingDialog() { //TODO: добавить описание всего написанного
        //https://stackoverflow.com/questions/18352324/how-can-can-i-add-custom-buttons-into-an-alertdialogs-layout?newreg=6ff112c896204cadbbb395cacc191aa1

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promtView = layoutInflater.inflate(R.layout.settings_dialog, null); //TODO: поменять название promtView

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(promtView);

        testbtn = promtView.findViewById(R.id.testbtn);

        edTxtWorkTime = promtView.findViewById(R.id.edTxt_workTime_dialogSet);
        edTxtShortBreak = promtView.findViewById(R.id.edTxt_shortBreak_dialogSet);
        edTxtLongBreak = promtView.findViewById(R.id.edTxt_longBreak_dialogSet);
        edTxtCycles = promtView.findViewById(R.id.edTxt_cycles_dialogSet);

        testbtn.setOnClickListener(v -> {
            workTime = Integer.parseInt(edTxtWorkTime.getText().toString());
            shortBreak = Integer.parseInt(edTxtShortBreak.getText().toString());
            longBreak = Integer.parseInt(edTxtLongBreak.getText().toString());
            cycles = Integer.parseInt(edTxtCycles.getText().toString());
        });


        AlertDialog alertD = builder.create();
        alertD.show();
    }


    /*public void SettingDialog() { //TODO: добавить описание всего написанного
        AlertDialog.Builder builder = new AlertDialog.Builder(this);




        ConstraintLayout cl = (ConstraintLayout) getLayoutInflater().inflate(R.layout.settings_dialog, null);
        builder.setView(cl);
        builder.show();
    }*/

}