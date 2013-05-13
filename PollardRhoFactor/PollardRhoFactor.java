/*
 * Factors numbers using the pollard rho method
 *
 * Maintain two numbers x and y, both start with 1. This method uses the function x^2 + 1 as the 
 * iterating function
 * At each step x is reassigned to f(x) and y is reassigned to f(f(y))
 * The gcd (y - x, N) is calculated. If gcd is not 1 or N, then gcd is a factor, otherwise we keep
 * going.
 */

import java.math.BigInteger;


public class PollardRhoFactor
{
    public static void main (String [] args)
    {
        //Start with x=1, y=1, and gcd=0
        BigInteger x = BigInteger.ONE;
        BigInteger y = BigInteger.ONE;
        BigInteger N = new BigInteger(262063 + "");
        BigInteger gcd = BigInteger.ZERO;
        do //Calculate first round and then continue until gcd=1 or N
        {
            //x = f(x)
            x = evalFunction(x, N);
            //y = f(f(y))
            y = evalFunction(evalFunction(y, N), N);
            //Calculate gcd
            gcd = N.gcd(y.subtract(x));
            //gcd = 0, then no factor found (x=y)
            if (gcd.equals(BigInteger.ZERO))
                return;
        } while (gcd.equals(BigInteger.ONE) || gcd.equals(N));
        System.out.println(gcd);
    }
    
    public static BigInteger evalFunction(BigInteger val, BigInteger N)
    {
        //Evaluate x^2 + 1
        return val.pow(2).add(BigInteger.ONE).mod(N);
    }
}
