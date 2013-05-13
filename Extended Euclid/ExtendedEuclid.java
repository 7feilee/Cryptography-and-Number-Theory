/*
 * Program to find the gcd of two numbers a and b and also to find numbers x and y such that
 * ax + by = g = gcd(a,b)
 */

import java.util.Scanner;


public class ExtendedEuclid
{
	public static void main(String[] args)
	{
		//Create a scanner to read in numbers
		Scanner in = new Scanner(System.in);
		while (true) //Read until two non-positive numbers are entered
		{
			int num1 = in.nextInt();
			int num2 = in.nextInt();
			
			if (num1 <= 0 && num2 <= 0)
				break;
			
			//Make a the max and b the min to avoid complications
			int a = Math.max(num1, num2);
			int b = Math.min(num1, num2);
			
			int[] aSeed = {1, 0};
			int[] bSeed = {0, 1};
			int gcd = 1;
			while (true) //Divide until the remainder is 0
			{
				int quotient = a / b;
				int remainder = a % b;
				
				System.out.println("Quotient: " + quotient + ", Remainder: " + remainder);
				
				if (remainder == 0)
				{
					//b is the gcd when the remainder is 0
					gcd = b;
					break;
				}
				//Change a and b for the next iteration
				a = b;
				b = remainder;
				
				//Calculation for m, n
				int newNumA = aSeed[0] - quotient * aSeed[1];
				int newNumB = bSeed[0] - quotient * bSeed[1];
				//Replace the old seeds with new ones
				aSeed[0] = aSeed[1];
				aSeed[1] = newNumA;
				bSeed[0] = bSeed[1];
				bSeed[1] = newNumB;
			}
			System.out.printf("GCD = %d, m = %d, n = %d\n%d = (%d * %d) + (%d * %d)\n",
					gcd, aSeed[1], bSeed[1], gcd, aSeed[1], Math.max(num1, num2), 
					bSeed[1], Math.min(num1, num2));
		}
	}
}
