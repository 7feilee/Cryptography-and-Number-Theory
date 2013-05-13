/*
 * Pollard rho method to solve the discrete log problem mod p. Given beta = alpha ^ l mod p, we want
 * to find l.
 *
 * We keep track of two tuples, each tuple starts with the value (1, 0, 0) = (x, a, b)
 * During each iteration, we modify one tuple once and the other twice.
 * Once x for both tuples are equal, we are done and there are two possibilities
 *		If b2 - b1 is invertible mod p, then l = ((b2 - b1)^-1)(a1 - a2) mod p
 *		else we use congruence properties to find multiple solutions
 */

import java.math.BigInteger;


public class PollardRhoDiscreteLog
{
    public static void main(String [] args)
    {
        //Creating variables
        BigInteger alpha = new BigInteger("13");
        BigInteger beta = new BigInteger("59480");
        BigInteger p = new BigInteger("526483");
        BigInteger order = new BigInteger("87747");
        Tuple t1 = new Tuple(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO);
        Tuple t2 = new Tuple(BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO);
        
        //Go in a loop until the x for both tuple is equal
        //ModifyTuple does math on the Tuple based on whether x is in S1, S2, or S3
        int counter = 0;
        do
        {
            t1 = modifyTuple(t1, beta, alpha, p);
            t2 = modifyTuple(modifyTuple(t2, beta, alpha, p), beta, alpha, p);
            counter++;
        } while (!t1.x.equals(t2.x));
        
        BigInteger b = t2.b.subtract(t1.b);
        BigInteger a = t1.a.subtract(t2.a);
        BigInteger gcd = t2.b.subtract(t1.b).gcd(order);
        
        //If gcd is 1, print discrete log, else print all possible solutions
        if (gcd.equals(BigInteger.ONE))
            System.out.println("Discrete log is: " + b.modInverse(order).multiply(a).mod(order) + ", counter: " + counter);
        else
        {
            BigInteger l = b.divide(gcd).modInverse(order.divide(gcd)).multiply(a.divide(gcd)).mod(order.divide(gcd));
            System.out.println("Possible solutions are");
            for (BigInteger i = BigInteger.ZERO; i.compareTo(gcd) < 0; i = i.add(BigInteger.ONE))
                System.out.println(l.add(i.multiply(order).divide(gcd)));
        }
    }

    private static Tuple modifyTuple(Tuple t, BigInteger beta, BigInteger alpha, BigInteger p)
    {
        BigInteger two = new BigInteger("2");
        BigInteger three = new BigInteger("3");
        if (t.x.mod(three).equals(BigInteger.ONE)) //x in S1
            return new Tuple(beta.multiply(t.x).mod(p), t.a, t.b.add(BigInteger.ONE).mod(p));
        else if (t.x.mod(three).equals(BigInteger.ZERO)) //x in S2
            return new Tuple(t.x.multiply(t.x).mod(p), t.a.multiply(two).mod(p), t.b.multiply(two).mod(p));
        else //x in S3
            return new Tuple(alpha.multiply(t.x).mod(p), t.a.add(BigInteger.ONE).mod(p), t.b);
    }

    //Class to store x, a, and b
    private static class Tuple
    {
        BigInteger x;
        BigInteger a;
        BigInteger b;

        public Tuple(BigInteger x, BigInteger a, BigInteger b)
        {
            this.x = x;
            this.a = a;
            this.b = b;
        }
    }
}
