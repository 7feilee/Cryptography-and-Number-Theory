/*
 * Finds the number of points on an elliptic curve mod p i.e. the order of curve mod p using
 * legendre symbol
 */

import java.math.BigInteger;

public class PointsOnEllipticCurve 
{
    // Make variables
    static BigInteger a = BigInteger.ONE;
    static BigInteger b = new BigInteger("28");
    static BigInteger p = new BigInteger("71");

    public static void main(String[] args) 
    {
        // Start number of points on curve with p+1
        BigInteger numPoints = p.add(BigInteger.ONE);
        // Calculate the legendre symbol from 0 to p-1 and add to count
        for (BigInteger i = BigInteger.ZERO; i.compareTo(p) < 0; i = i
                .add(BigInteger.ONE))
            numPoints.add(legendre(i));
        // Print the count
        System.out.println("Number of points on elliptic curve are: "
                + numPoints.intValue());
    }

    private static BigInteger legendre(BigInteger i) 
    {
        // Calculate x^3 + ax + b. If that is divisible by p, then answer is 0
        BigInteger num = i.pow(3).add(a.multiply(i)).add(b);
        if (num.remainder(p).equals(BigInteger.ZERO))
            return BigInteger.ZERO;

        // Calculate num^(p-1/2). If that is 1, the answer is 1, if it is -1,
        // then answer is -1, otherwise invalid symbol
        BigInteger exponent = p.subtract(BigInteger.ONE).divide(
                new BigInteger("2"));
        BigInteger result = a.modPow(exponent, p);
        if (result.equals(BigInteger.ONE))
            return BigInteger.ONE;
        else if (result.equals(p.subtract(BigInteger.ONE)))
            return new BigInteger("-1");
        else
            throw new ArithmeticException("Cannot evaluate legendre symbol");
    }
}
