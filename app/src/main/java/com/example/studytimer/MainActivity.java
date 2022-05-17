package com.example.studytimer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //Объявили все элементы MainActivity====
    private Button btnStartStopMainAct;
    private ImageButton imbtnSettingsMainAct;
    private TextView tvPomodoroMainAct;
    private TextView tvTimeMainAct;
    //====

    //Для работы с таймером====
    private CountDownTimer countDownTimer; //Для работы с таймером
    private long timeLeftInMillSeconds;
    private boolean timerRunning = false; //Показатель on/off таймера
    //====

    //Для работы с рингтоном====
    Ringtone ringtone;
    Uri notificationUri;
    //====

    //Для вывода уведомлений в случае ошибки
    Toast toastEmptyFields;
    //====

    //Вспомогательные

    private boolean settingsIsOpen = false; //Исключаем повторное открытие настроек
    //====

    //Данные для логики Pomodoro==
    private SharedPreferences workTime;
    private SharedPreferences shortBreak;
    private SharedPreferences longBreak;
    private SharedPreferences cycles;

    private int counterSwitch = 0; //Вспомогательная вещь для переключения

    final String SAVED_TXT_WORK_TIME = "WORK_TIME_SAVED";
    final String SAVED_TXT_SHORT_BREAK = "SHORT_BREAK_SAVED";
    final String SAVED_TXT_LONG_BREAK = "LONG_BREAK_SAVED";
    final String SAVED_TXT_CYCLES = "CYCLES_SAVED";

    private EditText edTxtWorkTime;
    private EditText edTxtShortBreak;
    private EditText edTxtLongBreak;
    private EditText edTxtCycles;

    private Button btnUpdateSettings;

    //====

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Устанавливаем портретный вид экрана в силу отсутствия необходмисоти поворота
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
        findRingtone();
        btnStartStopMainAct.setOnClickListener(v -> {
            startstop();

            //Одним из функционалом кнопки startstop является отключение мелодии
            //Если таймер сработал, то играет мелодия, то мы её отключаем, иначе нет
            if (ringtone.isPlaying()) {
                finishRingtone();
            }
        });
        imbtnSettingsMainAct.setOnClickListener(v -> {
            if (!settingsIsOpen) {
                settingsIsOpen = true;
                //Создаём диалог настроек, при нажатии на кнпоку настройки:
                SettingDialog();
            }
        });

    }

    private void init() {
        //Находим все элементы на MainActivity:
        //Для работы с таймером:
        btnStartStopMainAct = findViewById(R.id.btn_startstop_mainAct);
        tvTimeMainAct = findViewById(R.id.tv_time_mainAct);

        imbtnSettingsMainAct = findViewById(R.id.imbtn_settings_mainAct); //Кнопка настройки
        tvPomodoroMainAct = findViewById(R.id.tv_pomodoro_mainAct); //Главная надпись TODO: она быть может совсем не нужна для изменения - удалить значит

        setInitialParameters();
        updateTimer();
    }

    private void startstop() {
        if (timerRunning) { //Если таймер включён - выключаем / выключен - включаем <- при нажатии на кнопку startstop
            stopTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() { //Логика старта таймера
        countDownTimer = new CountDownTimer(timeLeftInMillSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { //Что делаем, когда прошел тик
                timeLeftInMillSeconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() { //Что делаем при завершении работы таймера
                ringtone.play();
                btnStartStopMainAct.setText("PAUSE!");
                counterSwitch++;
                timerRunning = false;
                change();

            }
        }.start();
        timerRunning = true; //Показываем, что таймер идёт
        btnStartStopMainAct.setText("PAUSE");
    }

    private void stopTimer() { //Логика остановки таймера
        countDownTimer.cancel(); //Приостанавливаем таймер (останавливаем - следуя из логики письма)
        btnStartStopMainAct.setText("START");
        timerRunning = false; //Показываем, что таймер остановился
    }

    private void updateTimer() { //Логика обновление циферблата

        int minutes = (int) timeLeftInMillSeconds / 60000;
        int seconds = (int) timeLeftInMillSeconds % 60000 / 1000;

        String timeLefText; //Формируем строку вида 10:00

        timeLefText = "" + minutes;
        timeLefText += ":";

        if (seconds < 10) { //Избегаем случай 10:5 -> 10:05
            timeLefText += "0";
        }

        timeLefText += seconds;

        tvTimeMainAct.setText(timeLefText); //Сформированную строку показываем на циферблате пользователю
    }

    private void findRingtone() {
        //Ищём рингтон из телефона пользователя:
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

    private void finishRingtone() { //Выключение проигрывания рингтона:
        if (ringtone != null) {
            ringtone.stop();
        }
    }

    private void SettingDialog() {
        //TODO: добавить описание всего написанного
        //https://stackoverflow.com/questions/18352324/how-can-can-i-add-custom-buttons-into-an-alertdialogs-layout?newreg=6ff112c896204cadbbb395cacc191aa1

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promtView = layoutInflater.inflate(R.layout.settings_dialog, null); //TODO: поменять название promtView

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(promtView);

        btnUpdateSettings = promtView.findViewById(R.id.btnUpdateSettings);

        edTxtWorkTime = promtView.findViewById(R.id.edTxt_workTime_dialogSet);
        edTxtShortBreak = promtView.findViewById(R.id.edTxt_shortBreak_dialogSet);
        edTxtLongBreak = promtView.findViewById(R.id.edTxt_longBreak_dialogSet);
        edTxtCycles = promtView.findViewById(R.id.edTxt_cycles_dialogSet);

        setTextToSettingParameters();

        AlertDialog alertD = builder.create();
        alertD.show();

        btnUpdateSettings.setOnClickListener(v -> {

            if (! (isEmpty(edTxtWorkTime) || isEmpty(edTxtShortBreak) || isEmpty(edTxtLongBreak) || isEmpty(edTxtCycles) )) {
                //Проверили на непустоту строки. Теперь проверка на корректность - строка состоит только из 0..9

                if (checkBannendSymbol(edTxtWorkTime) && checkBannendSymbol(edTxtShortBreak) && checkBannendSymbol(edTxtLongBreak) && checkBannendSymbol(edTxtCycles)) {
                    workTime = getPreferences(MODE_PRIVATE);
                    shortBreak = getPreferences(MODE_PRIVATE);
                    longBreak = getPreferences(MODE_PRIVATE);
                    cycles = getPreferences(MODE_PRIVATE);

                    SharedPreferences.Editor editorWorkTime = workTime.edit();
                    SharedPreferences.Editor editorShortBreak = shortBreak.edit();
                    SharedPreferences.Editor editorLongBreak = longBreak.edit();
                    SharedPreferences.Editor editorCycles = cycles.edit();

                    editorWorkTime.putInt(SAVED_TXT_WORK_TIME, Integer.parseInt(edTxtWorkTime.getText().toString()));
                    editorShortBreak.putInt(SAVED_TXT_SHORT_BREAK, Integer.parseInt(edTxtShortBreak.getText().toString()));
                    editorLongBreak.putInt(SAVED_TXT_LONG_BREAK, Integer.parseInt(edTxtLongBreak.getText().toString()));
                    editorCycles.putInt(SAVED_TXT_CYCLES, Integer.parseInt(edTxtCycles.getText().toString()));

                    editorWorkTime.commit();
                    editorShortBreak.commit();
                    editorLongBreak.commit();
                    editorCycles.commit();

                    if (timerRunning) {
                        startstop();
                    }
                    //После
                    timeLeftInMillSeconds = workTime.getInt(SAVED_TXT_WORK_TIME, 50) * 60 * 1000;
                    updateTimer();
                    alertD.cancel();
                } else {
                    Toast.makeText(this, "Некорректный ввод!", Toast.LENGTH_LONG).show();
                }

            } else {
                //Доделать логику, чтобы нового сообщения не выводилось, раз есть иное
                Toast.makeText(this, "Поля не должны быть пустыми!", Toast.LENGTH_LONG).show();
            }
        });

        alertD.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                settingsIsOpen = false;
            }
        });

    }

    private void setTextToSettingParameters() {

        workTime = getPreferences(MODE_PRIVATE);
        shortBreak = getPreferences(MODE_PRIVATE);
        longBreak = getPreferences(MODE_PRIVATE);
        cycles = getPreferences(MODE_PRIVATE);

        edTxtWorkTime.setText(String.valueOf(workTime.getInt(SAVED_TXT_WORK_TIME, 50)));
        edTxtShortBreak.setText(String.valueOf(shortBreak.getInt(SAVED_TXT_SHORT_BREAK, 10)));
        edTxtLongBreak.setText(String.valueOf(longBreak.getInt(SAVED_TXT_LONG_BREAK, 20)));
        edTxtCycles.setText(String.valueOf(cycles.getInt(SAVED_TXT_CYCLES, 4)));


    }

    private void setInitialParameters() {

        workTime = getPreferences(MODE_PRIVATE);
        btnStartStopMainAct.setText("START");
        timeLeftInMillSeconds = workTime.getInt(SAVED_TXT_WORK_TIME, 50) * 60 * 1000;

    }

    private void change() {
        if (counterSwitch == (cycles.getInt(SAVED_TXT_CYCLES, 2) * 2 - 1) ) {
            counterSwitch = 0; //Вроде ноль, но протестировать все
            timeLeftInMillSeconds = longBreak.getInt(SAVED_TXT_LONG_BREAK, 20) * 60 * 1000;
        } else {
            if (counterSwitch % 2 == 0) {
                timeLeftInMillSeconds = workTime.getInt(SAVED_TXT_WORK_TIME, 50) * 60 * 1000;
            } else {
                timeLeftInMillSeconds = shortBreak.getInt(SAVED_TXT_SHORT_BREAK, 40) * 60 * 1000; //40 - change
            }
        }
        updateTimer();
    }

    private boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().length() > 0) {
            return false;
        }
        return true;
    }

    private boolean checkBannendSymbol(EditText editText) {

        String str, test = "0123456789";
        boolean result;

        str = editText.getText().toString();

        for (int i = 0; i < str.length(); i++) {
            result = false;

            for (int j = 0; j < 10; j++) {
                if (str.charAt(i) == test.charAt(j)) { result = true; break; } //Обдуманно добавить break для повышения эффективности
            }

            //Имеется запрещенный символ
            if (result == false) { return false; }
        }
        return true;
    }

}
