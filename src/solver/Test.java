package solver;

import java.util.Random;

public class Test {
	
	public static void main(String[] args) {
		
		long startTime, endTime, duration;
		float stdDuration;
		Random random = new Random();
		double a;
		
		
		// Duration
		startTime = System.nanoTime();
		for(int i = 0; i < 1000000; i++) {}		
			a = random.nextFloat();
		endTime = System.nanoTime();		
		stdDuration = (float)(endTime - startTime)/1000000;
		System.out.println("Duration = " + (stdDuration));
		
		// Mat.exp
		startTime = System.nanoTime();
		for(int i = 0; i < 1000000; i++)			
			a = Math.exp(random.nextFloat());
		endTime = System.nanoTime();		
		duration = endTime - startTime;
		System.out.println("Math.exp = " + ((float)duration/1000000 - stdDuration));
		
		// Mat.expm1
		startTime = System.nanoTime();
		for(int i = 0; i < 1000000; i++)			
			Math.expm1(random.nextFloat());
		endTime = System.nanoTime();		
		duration = endTime - startTime;
		System.out.println("Math.expm1 = " + ((float)duration/1000000 - stdDuration));
		
		//Mat.abs
		startTime = System.nanoTime();
		for(int i = 0; i < 1000000; i++)			
			Math.abs(random.nextFloat());
		endTime = System.nanoTime();		
		duration = endTime - startTime;
		System.out.println("Math.abs = " + ((float)duration/1000000 - stdDuration));
		
		// Mat.pow2
		startTime = System.nanoTime();
		for(int i = 0; i < 1000000; i++)			
			Math.pow(random.nextFloat(), 2);
		endTime = System.nanoTime();		
		duration = endTime - startTime;
		System.out.println("Math.pow2 = " + ((float)duration/1000000 - stdDuration));
		
		// Mat.pow3
		startTime = System.nanoTime();
		for(int i = 0; i < 1000000; i++)			
			Math.pow(random.nextFloat(), 3);
		endTime = System.nanoTime();		
		duration = endTime - startTime;
		System.out.println("Math.pow3 = " + ((float)duration/1000000 - stdDuration));
		
		
		
		
		
		
		
		
		
		
		
	}
}
