package com.tac.bluetoothcomm;

/**
 * Created by baozi on 2017/12/11.
 */
public class PublicUtils {


    private double getPressureValue(int sensor01, int sensor02) {

    }


    /***
     * 解正常传感器的一元二次方程
     * @param result
     * @return
     */
    public static double getPressureNormalValue(int result) {
        double a = 4.9308;
        double b = 35.544;
        double c = -851.19 - result;

        return solveEquation(a, b, c);
    }

    /***
     * 解正常传感器的一元二次方程
     * @return
     */
    public static double getPressureNormalValue(int sensor01, int sensor02) {
        return getPressureNormalValue(sensor01 * 100 + sensor02);
    }

    /***
     * 解不正常传感器的一元二次方程
     * @param result
     * @return
     */
    public static double getPressureAbnormalValue(int result) {
        double a = 0.151;
        double b = -0.7902;
        double c = -6.5879 - result;

        return solveEquation(a, b, c);
    }

    /***
     * 解不正常传感器的一元二次方程
     * @return
     */
    public static double getPressureAbnormalValue(int sensor01, int sensor02) {
        return getPressureAbnormalValue(sensor01 * 100 + sensor02);
    }

    private static double solveEquation(double a, double b, double c) {
        double x1=0,x2=0,t;
        t = b * b - 4 * a * c;

        if(t < 0){
            return 0;
        }else{
            x1 = ((-b)+Math.sqrt(t))/(2*a);
            x2 = ((-b)-Math.sqrt(t))/(2*a);
        }

        return x1 >= 0 ? x1 : x2;
    }

}
