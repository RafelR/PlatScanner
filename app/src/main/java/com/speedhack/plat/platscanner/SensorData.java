package com.speedhack.plat.platscanner;

import java.util.ArrayList;

public class SensorData {
    private ArrayList<Float> acc_linear_touch = new ArrayList<>();
    private ArrayList<Float> acc_angular_touch = new ArrayList<>();
    private ArrayList<Float> acc_linear_release = new ArrayList<>();
    private ArrayList<Float> acc_angular_release = new ArrayList<>();
    private ArrayList<Float> pressure_touch = new ArrayList<>();
    private ArrayList<Float> pressure_release = new ArrayList<>();
    private ArrayList<Float> size_touch = new ArrayList<>();
    private ArrayList<Float> size_release = new ArrayList<>();
    private ArrayList<Long> key_hold = new ArrayList<>();
    private float[][] parsed = new float[1][54];

    public SensorData() {

    }

    public SensorData(ArrayList<Float> acc_linear_touch, ArrayList<Float> acc_angular_touch, ArrayList<Float> acc_linear_release, ArrayList<Float> acc_angular_release, ArrayList<Float> pressure_touch, ArrayList<Float> pressure_release, ArrayList<Float> size_touch, ArrayList<Float> size_release, ArrayList<Long> key_hold) {
        this.acc_linear_touch = acc_linear_touch;
        this.acc_angular_touch = acc_angular_touch;
        this.acc_linear_release = acc_linear_release;
        this.acc_angular_release = acc_angular_release;
        this.pressure_touch = pressure_touch;
        this.pressure_release = pressure_release;
        this.size_touch = size_touch;
        this.size_release = size_release;
        this.key_hold = key_hold;

        for(Long data:this.key_hold){
            System.out.print(data + ", ");
        }
    }

    public ArrayList<Float> getAcc_linear_touch() {
        return acc_linear_touch;
    }

    public ArrayList<Float> getAcc_angular_touch() {
        return acc_angular_touch;
    }

    public ArrayList<Float> getAcc_linear_release() {
        return acc_linear_release;
    }

    public ArrayList<Float> getAcc_angular_release() {
        return acc_angular_release;
    }

    public ArrayList<Float> getPressure_touch() {
        return pressure_touch;
    }

    public ArrayList<Float> getPressure_release() {
        return pressure_release;
    }

    public ArrayList<Float> getSize_touch() {
        return size_touch;
    }

    public ArrayList<Float> getSize_release() {
        return size_release;
    }

    public ArrayList<Long> getKey_hold() {
        return key_hold;
    }

    public void parsing() {
        int counter = 0;
        for (Float data : acc_angular_release) {
            parsed[0][counter] = data.floatValue();
            System.out.print(data + " ");
            counter++;
        }
        for (Float data : acc_angular_touch) {
            parsed[0][counter] = data.floatValue();
            counter++;
        }
        for (Float data : acc_linear_release) {
            parsed[0][counter] = data.floatValue();
            counter++;
        }
        for (Float data : acc_linear_touch) {
            parsed[0][counter] = data.floatValue();
            counter++;
        }
        for (Long data : key_hold) {
            parsed[0][counter] = data.floatValue()/1000.0f;
            counter++;
        }
        for (Float data : pressure_release) {
            parsed[0][counter] = data.floatValue();
            counter++;
        }
        for (Float data : pressure_touch) {
            parsed[0][counter] = data.floatValue();
            counter++;
        }
        for (Float data : size_release) {
            parsed[0][counter] = data.floatValue();
            counter++;
        }
        for (Float data : size_touch) {
            parsed[0][counter] = data.floatValue();
            counter++;
        }
    }

    public float[][] getParsed() {
        parsing();
        System.out.print("parsed : ");
        for(int i=0;i<54;i++){
            System.out.print(parsed[0][i]+ ", ");
        }

        return parsed;
    }
}
