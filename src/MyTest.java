/**
 * Created by Hong on 2017/12/6.
 */
public class MyTest {
	private final static int R0 = 2000;

	public static void main(String[] args) {
//		double v = Math.log10(100);
//		System.out.println(v);
//
//		double dd = 108;
//		int xx = 100;
//		double temperature = ((dd / xx) - 1) / 3.90802 * 1000;
//		System.out.println(temperature);

		int i = Integer.parseInt("08", 16);
		int j = Integer.parseInt("1E", 16);
		Integer integer = Integer.valueOf("081E", 16);
		System.out.println(i);

		double sensor01 = getTemperaturValue("08", "1E");

		System.out.println(sensor01);

	}

	private static double getTemperaturValue(String sensor01, String sensor02) {
		double sensorValue = Integer.valueOf(sensor01 + sensor02, 16);
		return ((sensorValue / R0) - 1) / 3.90802 * 1000;
	}

	private static double getSensorValue(int sensor01, int sensor02) {
		int n1 = sensor01 * 100 + sensor02;
		return  3.412 * Math.exp((0.0016 * n1));
	}
}
