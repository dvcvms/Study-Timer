package com.example.myapppomodoro;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //Объявили все элементы MainActivity====
    private Button btnStartStopMainAct;
    private ImageButton imbtnSettingsMainAct;
    private TextView tvPomodoroMainAct;
    private TextView tvTimeMainAct;
    //====

    //Для работы с таймером====
    private CountDownTimer countDownTimer; //Для работы с таймером
    private long timeLeftInMillSeconds = 600_000; //Начальное значение таймера - 10:00
    private boolean timerRunning = false; //Показатель on/off таймера
    //====

    //Для работы с рингтоном====
    Ringtone ringtone;
    Uri notificationUri;
    //====

    //Данные для логики Pomodoro==

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
}