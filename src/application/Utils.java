package application;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Utils {
	

	public static Double rnd(Double Min, Double Max)
	{
		Double randomValue =0.0;
		randomValue = ThreadLocalRandom.current().nextDouble(Min, Max+0.0000000001);
		
		return (double)Math.round(randomValue * 1000d) / 1000d;
	}
	public static Integer rnd(Integer Min, Integer Max)
	{
		Integer randomValue =0;
		randomValue = ThreadLocalRandom.current().nextInt(Min, Max+1);
		
		return randomValue;
	}

	public static Double rndExp(Double alpha)
	{
		Random rnd = new Random();
		return ( - ( 1.0 / alpha )) * Math.log(rnd.nextDouble());
	}
}
