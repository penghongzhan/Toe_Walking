package com.tac.bluetoothcomm;

import java.util.ArrayList;

public class AppPublic {
	//脚尖传感器第一个字节
	public static ArrayList<Integer> linedataint_ch1_1 = new ArrayList<Integer>();
	//脚尖传感器第二个字节
	public static ArrayList<Integer> linedataint_ch1_2 = new ArrayList<Integer>();

	//脚后跟传感器的第一个字节
	public static ArrayList<Integer> linedataint_ch2_1 = new ArrayList<Integer>();
	//脚后跟传感器的第二个字节
	public static ArrayList<Integer> linedataint_ch2_2 = new ArrayList<Integer>();

	public static ArrayList<Integer> countarray = new ArrayList<Integer>();

	//脚尖传感器的压力值
	public static ArrayList<Double> power_ch1 = new ArrayList<Double>();
	//脚后跟传感器的压力值
	public static ArrayList<Double> power_ch2 = new ArrayList<Double>();

	//脚尖传感器压力值两两之间的插值
	public static ArrayList<Double> difference_ch1 = new ArrayList<Double>();

	//脚后跟传感器压力值两两之间的插值
	public static ArrayList<Double> difference_ch2 = new ArrayList<Double>();

	public static double power_all_ch1_1 = 0;
	public static double power_all_ch1_2 = 0;
	public static double power_all_ch2_1 = 0;
	public static double power_all_ch2_2 = 0;

}
