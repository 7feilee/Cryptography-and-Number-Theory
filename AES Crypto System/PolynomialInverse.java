/*
 * This program finds polynomial GCD and finds the inverse of a polynomial over
 * a prime p.
 * 
 * We represent a polynomial as a array of numbers, where each entry represents
 * the coefficient of the corresponding power in the polynomial.
 * So the polynomial x^3 + 2x^2 + 3x + 10 is represented as the array [1, 2, 3, 10] and the 
 * polynomial x^2 + 20 is represented as the array [1, 0, 20].
 */
 
import java.util.ArrayList;
import java.util.Arrays;

public class PolynomialInverse
{
    private int [] divisor;
    private int [] dividend;
    private int p;
    
    public PolynomialInverse(int[] divisor, int[] dividend, int p)
	{
        this.divisor = divisor;
        this.dividend = dividend;
        this.p = p;
    }
    
    public int[] findInverse()
	{
        //Inverse of zero is zero
        if (divisor.length == 0)
            return new int[8];
        ArrayList<int[]> quotients = new ArrayList<int[]>();
        gcd(quotients);

        // To find inverse, call the extended GCD with quotients and get back
        // inverses of the given polynomials
        ArrayList<int[]> inverses = new ArrayList<int[]>();
        extendedGCD(inverses, quotients);

        int [] inv1 = inverses.get(inverses.size() - 1);
        int [] inv2 = inverses.get(inverses.size() - 2);
        
        return inv1.length >= inv2.length ? inv1 : inv2;
    }
    
    private void gcd(ArrayList<int[]> quotients)
	{
        // Copy over the dividend to another array
        int[] r = Arrays.copyOf(dividend, dividend.length);
        // This is the GCD algorithm:
        while (true)
		{
            // Create a new array of degree dividend.length - divisor.length +1
            int[] q = new int[dividend.length - divisor.length + 1];
            // Call divide and put result in q
            r = divide(dividend, divisor, q);
            // We end the algorithm when the remainder is 0
            if (r.length == 0)
                break;
            // Add this round's quotient to our list of quotients
            quotients.add(0, q);
            // The old divisor becomes the new dividend
            dividend = divisor;
            // The remainder becomes the new divisor
            divisor = r;
        }
    }

    private void extendedGCD(ArrayList<int[]> inverses, ArrayList<int[]> quotients)
	{
        //This function implements the algorithm to find inverses
        //Start by creating two arrays, one for arrays to add, and 
        //another to multiply quotients
        int[] addArray = new int[] { 0 };
        int[] multArray = new int[] { 1 };
        //In the table, the inverses array is the last row, so we add 0 and 1 to start
        inverses.add(addArray);
        inverses.add(multArray);
        //Loop through all the quotients to do the add and multiply.
        for (int i = 0; i < quotients.size(); ++i)
		{
            //Call multiplyAdd, with a quotient and an array of numbers to add
            //and an array of numbers to multiply
            int[] ans = multiplyAdd(quotients.get(i), multArray, addArray);
            //Add the answer we get to a inverses list
            inverses.add(ans);
            //The old multiplyArray will be the new addArray
            addArray = multArray;
            //The old answer will be the new multiArray
            multArray = Arrays.copyOf(ans, ans.length);
        }
    }

    //This function divides two polynomials, returns a remainder
    //and puts the quotient in the quotient array that is passed in
    private int[] divide(int[] dividend, int[] divisor, int[] quotient)
	{
        //While the degree of the dividend is greater than or equal to the divisor's degree
        while (dividend.length >= divisor.length)
		{
            //Find the coefficient by dividing the first two coefficients
            int qCoeff = dividend[0] / divisor[0];
            
            //If they are not divisible, to avoid fractional powers
            if (qCoeff * divisor[0] != dividend[0])
                break;

            //Find the power of the quotient and put it in the quotient
            int qPower = quotient.length - (dividend.length - divisor.length + 1);
            quotient[qPower] = qCoeff;
            //Do the actual division algorithm
            for (int i = 0; i < divisor.length; ++i)
                dividend[i] = doMod(dividend[i] - divisor[i] * qCoeff);

            //Trims down the array to remove leading 0s
            int tempIndex = 0;
            while (tempIndex < dividend.length && dividend[tempIndex] == 0)
                tempIndex++;
            int[] temp = new int[dividend.length - tempIndex];
            for (int i = tempIndex, j = 0; j < temp.length; ++i, ++j)
                temp[j] = dividend[i];
            dividend = temp;
        }
        //return the remainder
        return dividend;
    }

    private int[] multiplyAdd(int[] quotient, int[] multArray, int[] addArray)
	{
        //Multiply the quotient and the multArray, uses FOIL
        int[] p = new int[quotient.length + multArray.length - 1];
        for (int j = 0; j < quotient.length; ++j)
            for (int k = 0; k < multArray.length; ++k)
                p[k + j] = doMod(p[k + j] + quotient[j] * multArray[k]);
        //Add the addArray to the result of the multiplication
        int length = Math.min(p.length, addArray.length);
        for (int i = 0; i < length; ++i)
            p[p.length - i - 1] = doMod(p[p.length - i - 1] + addArray[addArray.length - i - 1]);
        //Return the result of multiplying and adding
        return p;
    }
    
    private int doMod(int number)
    {
        if (number >= 0)
            return number % p;
        else
        {
            int div = Math.abs(number) / p;
            return number + (div * p) + p;
        }
    }
}
