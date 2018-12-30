package com.speedhack.plat.platscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelManager;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.google.firebase.ml.custom.model.FirebaseCloudModelSource;
import com.google.firebase.ml.custom.model.FirebaseLocalModelSource;
import com.google.firebase.ml.custom.model.FirebaseModelDownloadConditions;

import java.util.ArrayList;
import java.util.Stack;

public class UserCheck extends AppCompatActivity {
    /**TENSORFLOW
     *
     *
     */
    /**
     * An instance of the driver class to run model inference with Firebase.
     */
    private FirebaseModelInterpreter mInterpreter;
    /**
     * Data configuration of input & output data of model.
     */
    private FirebaseModelInputOutputOptions mDataOptions;

    private String modelNameInCloud = "similarity-scoring";
    private String modelNameLocal = "local_model_similarity_scoring.tflite";


    private int[] inputDims = new int[]{1, 54};
    private int[] outputDims = new int[]{1};

    private float[] finalOutput; //ini disesuaikan sama model

    public float[] score = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public int counter = 0;

    private void configureHostedModelSource() {
        // [START mlkit_cloud_model_source]
        FirebaseModelDownloadConditions.Builder conditionsBuilder =
                new FirebaseModelDownloadConditions.Builder().requireWifi();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Enable advanced conditions on Android Nougat and newer.
            conditionsBuilder = conditionsBuilder
                    .requireCharging()
                    .requireDeviceIdle();
        }
        FirebaseModelDownloadConditions conditions = conditionsBuilder.build();

        // Build a FirebaseCloudModelSource object by specifying the name you assigned the model
        // when you uploaded it in the Firebase console.
        FirebaseCloudModelSource cloudSource = new FirebaseCloudModelSource.Builder(modelNameInCloud)
                .enableModelUpdates(true)
                .setInitialDownloadConditions(conditions)
                .setUpdatesDownloadConditions(conditions)
                .build();
        FirebaseModelManager.getInstance().registerCloudModelSource(cloudSource);
        // [END mlkit_cloud_model_source]
    }

    private void configureLocalModelSource() {
        // [START mlkit_local_model_source]
        FirebaseLocalModelSource localSource =
                new FirebaseLocalModelSource.Builder("asset")  // Assign a name for this model
                        .setAssetFilePath(modelNameLocal)
                        .build();
        FirebaseModelManager.getInstance().registerLocalModelSource(localSource);
        // [END mlkit_local_model_source]
    }

    private FirebaseModelInterpreter createInterpreter() throws FirebaseMLException {
        // [START mlkit_create_interpreter]
        FirebaseModelOptions options = new FirebaseModelOptions.Builder()
                .setCloudModelName(modelNameInCloud)
                .setLocalModelName("asset")
                .build();
        FirebaseModelInterpreter firebaseInterpreter =
                FirebaseModelInterpreter.getInstance(options);
        // [END mlkit_create_interpreter]

        return firebaseInterpreter;
    }

    private FirebaseModelInputOutputOptions createInputOutputOptions() throws FirebaseMLException {
        FirebaseModelInputOutputOptions inputOutputOptions =
                new FirebaseModelInputOutputOptions.Builder()
                        .setInputFormat(0, FirebaseModelDataType.FLOAT32, this.inputDims) //input pertama
                        .setInputFormat(1, FirebaseModelDataType.FLOAT32, this.inputDims) //input kedua
                        .setOutputFormat(0, FirebaseModelDataType.FLOAT32, this.outputDims) //output
                        .build();
        return inputOutputOptions;
    }

    public void runInference(float[][] input1, float[][] input2) throws FirebaseMLException {
        System.out.println("MASUK MODEL INFERENCE");

        if (mInterpreter == null) {
            System.out.println("input has not been initialized; Skipped.");
            return;
        }

        // [START mlkit_run_inference]
        FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                .add(input1).add(input2)
                .build();
        mInterpreter.run(inputs, mDataOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseModelOutputs>() {
                            @Override
                            public void onSuccess(FirebaseModelOutputs result) {

                                float[] output = result.getOutput(0); //ini disesuaikan sama model
                                finalOutput = output; //ini disesuaikan sama model
                                printFinalOutput();
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                System.out.println("GAGAL");
                            }
                        });
    }

    public int[] getInputDims() {
        return inputDims;
    }

    public int[] getOutputDims() {
        return outputDims;
    }

    public float getFinalOutput() {
        float hasil = finalOutput[0];
        return hasil;
    }

    public void printFinalOutput() {
        System.out.println("OUTPUT: " + "[" + finalOutput[0] + "]"); //ini disesuaikan sama model
        float scorenowwww = finalOutput[0];
        float threshold = 40;
        if (scorenowwww < threshold) {
            System.out.println("SCORENOW = " + scorenowwww + " THRESHOLD = " + threshold);
            Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UserCheck.this, UserActivity.class));
            finish();
        } else {
            System.out.println("SCORENOW = " + scorenowwww + " THRESHOLD = " + threshold);
            Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Please Try Again", Toast.LENGTH_LONG).show();
            behaviourCounter++;
            clearStack();
        }
        if (behaviourCounter>3){
            Toast.makeText(getApplicationContext(), "Please Check Your Email This is Really You", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserCheck.this, UserActivity.class));
            finish();
        }
        if(tryCounter>3){
            Toast.makeText(getApplicationContext(), "You Have Reached Maximum Try", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserCheck.this, UserActivity.class));
            finish();
        }
    }

    /***
     *
     *
     * BATAS TENSORFLOW
     */
    public static float scoreNow = 0;
    //Accelerometer
    private SensorManager mSensorManager;
    private Sensor mAcceleration_linear;
    private Sensor mAcceleration_angular;

    private long lastUpdate_lin = 0;
    private float last_x_lin, last_y_lin, last_z_lin;
    private long lastUpdate_ang = 0;
    private float last_x_ang, last_y_ang, last_z_ang;
    private static final int SHAKE_THRESHOLD = 600;


    //Variable XML
    private TextView txtPressureDown;
    private TextView txtPressureUp;
    private TextView txtSizeDown;
    private TextView txtSizeUp;
    private TextView txtPressedDuration;
    private TextView txtPerintah;
    private EditText txtPassword;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_0;
    private Button btn_del;
    private Button btn_togglePassword;

    //Variable temp
    private int maxLengthPassword = 6;
    private long lastDown;
    private long lastDuration;
    private boolean isShowed = false;

    private Stack<Integer> pinEntered = new Stack<Integer>();
    private String nama = "";
    private ArrayList<SensorData> ListSensorData = new ArrayList<SensorData>();

    //Database Stuff
    DatabaseReference databaseSensor;

    //Data RAW
    private float currentAccelLinear = 0;
    private float currentAccelAngular = 0;


    private Stack<Float> acc_linear_touch = new Stack<>();
    private float currentAccelLinearTouch = 0;

    private Stack<Float> acc_angular_touch = new Stack<>();
    private float currentAccelAngularTouch = 0;

    private Stack<Float> acc_linear_release = new Stack<>();
    private float currentAccelLinearRelease = 0;

    private Stack<Float> acc_angular_release = new Stack<>();
    private float currentAccelAngularRelease = 0;

    private Stack<Float> pressure_touch = new Stack<>();
    private float currentPressureTouch = 0;

    private Stack<Float> pressure_release = new Stack<>();
    private float currentPressureRelease = 0;

    private Stack<Float> size_touch = new Stack<>();
    private float currentSizeTouch = 0;

    private Stack<Float> size_release = new Stack<>();
    private float currentSizeRelease = 0;

    private Stack<Long> key_hold = new Stack<>();
    private long currentKeyHold = 0;

    private String UserID;
    private int tryCounter = 0;
    private int behaviourCounter = 0;
    private String userId;

    private static ArrayList<String> pin_input_stages = new ArrayList<String>() {
        {
            add("pertama");
            add("kedua");
            add("ketiga");
        }
    };
    private static String pin_input_stage = pin_input_stages.get(0);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_check);
        finalOutput = new float[this.outputDims[0]]; //ini disesuaikan sama model

        configureHostedModelSource(); // setting model di cloud
        configureLocalModelSource(); // setting model di local

        try {
            mInterpreter = createInterpreter(); // instance interpreternya
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }

        try {
            mDataOptions = createInputOutputOptions(); // instance dataoptionsnya
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }

        //Inisialisasi semua elemen xml
        txtPressureDown = (TextView) findViewById(R.id.sensor_pressure_down);
        txtPressureUp = (TextView) findViewById(R.id.sensor_pressure_up);
        txtSizeDown = (TextView) findViewById(R.id.sensor_size_down);
        txtSizeUp = (TextView) findViewById(R.id.sensor_size_up);
        txtPressedDuration = (TextView) findViewById(R.id.sensor_pressed_timer);
        txtPassword = (EditText) findViewById(R.id.password);
        txtPerintah = (TextView) findViewById(R.id.perintah);
        txtPerintah.setText("Enter Your Pin Number");

        btn_1 = (Button) findViewById(R.id.pin_id_1);
        btn_2 = (Button) findViewById(R.id.pin_id_2);
        btn_3 = (Button) findViewById(R.id.pin_id_3);
        btn_4 = (Button) findViewById(R.id.pin_id_4);
        btn_5 = (Button) findViewById(R.id.pin_id_5);
        btn_6 = (Button) findViewById(R.id.pin_id_6);
        btn_7 = (Button) findViewById(R.id.pin_id_7);
        btn_8 = (Button) findViewById(R.id.pin_id_8);
        btn_9 = (Button) findViewById(R.id.pin_id_9);
        btn_0 = (Button) findViewById(R.id.pin_id_0);
        btn_del = (Button) findViewById(R.id.pin_id_del);
        btn_togglePassword = (Button) findViewById(R.id.toogle_password);

        // 1. Get sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 2. Get the default sensor of specified type
        mAcceleration_linear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        // 2. Get the default sensor of specified type
        mAcceleration_angular = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


        //Setup semua onTouchListener masing-masing tombol
        btn_togglePassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isShowed) {
                        txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                        isShowed = false;
                        btn_togglePassword.setText("Show");
                    } else {
                        txtPassword.setTransformationMethod(null);
                        isShowed = true;
                        btn_togglePassword.setText("Hide");
                    }
                }
                return true;
            }
        });

        btn_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(1);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(2);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(3);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(4);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(5);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(6);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_7.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(7);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_8.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(8);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_9.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(9);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //push stack and display it
                    //input pin ke stack
                    stack_push(0);
                    getAccelero("touch");
                }
                getTouchEvents(v, event);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    getAccelero("release");
                    pushAllSensorData();
                    isFull();
                }
                return true;
            }
        });
        btn_del.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //biar onTouch gk kepanggil berulang kali untuk stacknya
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!pinEntered.isEmpty()) {
                        pinEntered.pop();
                        acc_linear_touch.pop();
                        acc_angular_touch.pop();
                        acc_linear_release.pop();
                        acc_angular_release.pop();
                        pressure_touch.pop();
                        pressure_release.pop();
                        size_touch.pop();
                        size_release.pop();
                        key_hold.pop();
                        displayStack();
                    }
                }
                return true;
            }
        });
    }

    //Berurusan dengan accelerometer
    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor mySensor = event.sensor;

            if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                float sum = (float) Math.sqrt(x * x + y * y + z * z);
                currentAccelLinear = sum;
//                System.out.println("Sensor Linear x = " + x + " | y = " + y + " z = " + z);

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate_lin) > 100) {
                    long diffTime = (curTime - lastUpdate_lin);
                    lastUpdate_lin = curTime;

                    float speed = Math.abs(x + y + z - last_x_lin - last_y_lin - last_z_lin) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {

                    }

                    last_x_lin = x;
                    last_y_lin = y;
                    last_z_lin = z;
                }
            }

            if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                float sum = (float) Math.sqrt(x * x + y * y + z * z);
                currentAccelAngular = sum;
//                System.out.println("Sensor Angular x = " + x + " | y = " + y + " z = " + z);

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate_ang) > 100) {
                    long diffTime = (curTime - lastUpdate_ang);
                    lastUpdate_ang = curTime;

                    float speed = Math.abs(x + y + z - last_x_ang - last_y_ang - last_z_ang) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {
                        /*
                         * TODO: Figure something out about this SHAKE_THRESHOLD
                         */
                    }

                    last_x_ang = x;
                    last_y_ang = y;
                    last_z_ang = z;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("MY_APP", sensor.toString() + " - " + accuracy);
        }
    };

    public void getAccelero(String key) {
        if (key.equals("touch")) {
            currentAccelLinearTouch = currentAccelLinear;
            currentAccelAngularTouch = currentAccelAngular;
        } else if (key.equals("release")) {
            currentAccelLinearRelease = currentAccelLinear;
            currentAccelAngularRelease = currentAccelAngular;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAcceleration_linear != null) {
            mSensorManager.registerListener(mSensorListener, mAcceleration_linear,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mAcceleration_angular != null) {
            mSensorManager.registerListener(mSensorListener, mAcceleration_angular,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAcceleration_linear != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
        if (mAcceleration_angular != null) {
            mSensorManager.unregisterListener(mSensorListener);
        }
    }

    /*
     * Method untuk mengambil data event selain input pin number
     */
    public void getTouchEvents(View v, MotionEvent event) {

        //touch duration
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastDown = System.currentTimeMillis();
            //pressure dan size
            txtPressureDown.setText(String.valueOf(event.getPressure()));
            txtSizeDown.setText(String.valueOf(event.getSize()));

            //update value now size dan pressure
            currentSizeTouch = event.getSize();
            currentPressureTouch = event.getPressure();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            lastDuration = System.currentTimeMillis() - lastDown;
            //pressure dan size
            System.out.println("Pressure up = " + event.getPressure() + "Size up = " + event.getSize());
            txtPressureUp.setText(String.valueOf(event.getPressure()));
            txtSizeUp.setText(String.valueOf(event.getSize()));

            //update value now size dan pressure
            currentSizeRelease = event.getSize();
            currentPressureRelease = event.getPressure();

        }
        txtPressedDuration.setText(String.valueOf(lastDuration));
        currentKeyHold = lastDuration;
    }

    /*
     * Method seputar pengolahan stack untuk pin yang dimasukkan
     * Mungkin kedepannya bisa direfactor agar bisa digunakan oleh semua stack
     */
    public void isFull() {
        //cek jumlah elemen sekarang
        int counter = 0;
        for (Integer pin : pinEntered) {
            counter++;
        }
        System.out.println("counter = " + counter);
        //cek apakah sudah maksimum diinput
        //kalo sudah, gk bisa input lagi
        if (counter >= maxLengthPassword) {
            checkPassword();
        }
    }

    public void stack_push(int num) {
        pinEntered.push(num);
        displayStack();
    }

    public void displayStack() {
        String buffer = "";
        for (Integer pin : pinEntered) {
            String temp = pin.toString();
            buffer = buffer + temp;
        }
        txtPassword.setText(buffer);
        System.out.println("Buffer = " + buffer);
    }

    public void clearStack() {
        while (!pinEntered.isEmpty()) {
            pinEntered.pop();
            acc_linear_touch.pop();
            acc_angular_touch.pop();
            acc_linear_release.pop();
            acc_angular_release.pop();
            pressure_touch.pop();
            pressure_release.pop();
            size_touch.pop();
            size_release.pop();
            key_hold.pop();
        }
        displayStack();
    }

    public void checkPassword() {

        String buffer = "";
        for (Integer pin : pinEntered) {
            String temp = pin.toString();
            buffer = buffer + temp;
        }

        //TODO: Parsing JSON
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/"+userId+"/pin");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Post post = dataSnapshot.getValue(Post.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // ...
//            }
//        });
//        System.out.println("Buffer akhir = " + buffer);
//        Toast.makeText(getApplicationContext(), "Good Job", Toast.LENGTH_SHORT).show();


            Wina wina = new Wina();
            if (buffer.equals(wina.getPassword())) {

                //simpen sensordata ke arraylist

                SensorData data_sensor = new SensorData(new ArrayList(acc_linear_touch), new ArrayList(acc_angular_touch), new ArrayList(acc_linear_release), new ArrayList(acc_angular_release), new ArrayList(pressure_touch), new ArrayList(pressure_release), new ArrayList(size_touch), new ArrayList(size_release), new ArrayList(key_hold));

                float[][] parsed_data = data_sensor.getParsed();

                try {
                    runInference(wina.data_wina[3], parsed_data);
                } catch (FirebaseMLException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "You input the wrong PIN", Toast.LENGTH_SHORT).show();
                tryCounter++;
                clearStack();
            }

        if (behaviourCounter>3){
            Toast.makeText(getApplicationContext(), "Please Check Your Email This is Really You", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserCheck.this, UserActivity.class));
            finish();
        }
        if(tryCounter>3){
            Toast.makeText(getApplicationContext(), "You Have Reached Maximum Try", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserCheck.this, UserActivity.class));
            finish();
        }
    }

    public void pushAllSensorData() {

        acc_linear_touch.push(currentAccelLinearTouch);
        acc_angular_touch.push(currentAccelAngularTouch);
        acc_linear_release.push(currentAccelLinearRelease);
        acc_angular_release.push(currentAccelAngularRelease);
        pressure_touch.push(currentPressureTouch);
        pressure_release.push(currentPressureRelease);
        size_touch.push(currentSizeTouch);
        size_release.push(currentSizeRelease);
        key_hold.push(currentKeyHold);
    }

//    public boolean checkUserIdentity(float[][][] input1, float[][] input2){
//        // TODO: [Ridwan] bandingin score dengan treshold
//        getDist(input1, input2);
//        float ave = 0;
//        float[] dist = score;
//        for(int i=0;i<10;i++){
//            System.out.println("DIST = " + dist[i]);
//            ave+=dist[i];
//        }
//        ave/=10;
//        float threshold = 40;
//        if(ave<threshold){
//            System.out.println("AVE = " + ave + " THRESHOLD = " + threshold);
//            return true;
//        }
//        return false;
//    }
//
//    public void getDist(float[][][] input1, float[][] input2){
//        float[] hasil = new float[10];
//        int counter = 0;
//        for(float[][] input1_single: input1){
//            try {
//                runInference(input1_single, input2);
//                hasil[counter] = scoreNow;
//            } catch (FirebaseMLException e) {
//                e.printStackTrace();
//            }
//
//            hasil[counter] = scoreNow;
//            System.out.println("SCORENOW = " + scoreNow);
//            counter++;
//        }
//    }

}
