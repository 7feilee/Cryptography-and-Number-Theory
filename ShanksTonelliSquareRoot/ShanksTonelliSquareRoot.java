/*
 * This algorithm is used to find the square root of a mod p
 * We start with an estimation for the square root, namely x = a^(t+1/2) mod p. If x is not the 
 * square root, then we keep on modifying it during every iteration of the loop. The loop iteration
 * ends when r = 0, and because the value of r goes down by atleast one every iteration, we know
 * that the loop terminates eventually.
 */

import java.math.BigInteger;


public class ShanksTonelliSquareRoot
{
    public static void main(String [] args)
    {
        final BigInteger TWO = new BigInteger("2");
        
        BigInteger p = new BigInteger("4481");
        BigInteger s = new BigInteger("7");
        BigInteger t = new BigInteger("35");
        BigInteger a = new BigInteger("19");
        
        BigInteger x = a.modPow(t.add(BigInteger.ONE).divide(TWO), p);
        BigInteger b = a.modPow(t, p);
        BigInteger n = getNonSquare(p);
        BigInteger g = n.modPow(t, p);
        BigInteger r = new BigInteger(s.toString());
        
        while (true)
        {
            BigInteger m = null;
            for (BigInteger i = BigInteger.ZERO; i.compareTo(r) < 0; i = i.add(BigInteger.ONE))
            {
                BigInteger pow = TWO.pow(i.intValue());
                if (b.modPow(pow, p).compareTo(BigInteger.ONE) == 0)
                {
                    m = i;
                    break;
                }
            }
            if (m.compareTo(BigInteger.ZERO) == 0)
                break;
            else
            {
                BigInteger xPow = TWO.pow(r.subtract(m).subtract(BigInteger.ONE).intValue());
                x = g.modPow(xPow, p).multiply(x).mod(p);
                BigInteger bPow = TWO.pow(r.subtract(m).intValue());
                b = g.modPow(bPow, p).multiply(b).mod(p);
                g = g.modPow(bPow, p);
                r = m;
            }
        }
        System.out.println("SQRT IS: " + x.toString());
    }
    
    static BigInteger getNonSquare(BigInteger p)
    {
        boolean[] array = new boolean[p.intValue()];
        for (int i = 0; i < array.length; ++i)
            array[(i * i) % array.length] = true;
        for (int i = 0; i < array.length; ++i)
            if (!array[i])
                return new BigInteger(i + "");
        return null;
    }
}
