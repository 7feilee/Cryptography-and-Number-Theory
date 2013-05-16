/*
 * This program is used to find the secret developed by Shamir (t, w) secret sharing algorithm, 
 * where shares are given to w people and t of those need to collaborate to find the secret. In this
 * case a t+1 degree polynomial is formed with the constant term as the secret and points on the
 * polynomial are distributed to w people. We use the following formula to find the secret
 * Secret = sum_i=1_to_t(yi * product_j=1_to_t_j!=i(-xj/(xi-xj))). This formula is derived by 
 * using Langrange Interpolation
 */

import java.awt.Point;
import java.math.BigInteger;


public class ShamirThresholdPolyInterpolation
{
    public static void main(String [] args)
    {
        BPoint[] points = new BPoint[]
        {
            new BPoint(new BigInteger("11"), new BigInteger("29952055635")), 
            new BPoint(new BigInteger("22"), new BigInteger("26786192733")), 
            new BPoint(new BigInteger("33"), new BigInteger("77756881208")),
            new BPoint(new BigInteger("44"), new BigInteger("80139093118")),
            new BPoint(new BigInteger("55"), new BigInteger("24225052606")),
            new BPoint(new BigInteger("66"), new BigInteger("74666503567")),
            new BPoint(new BigInteger("77"), new BigInteger("1078845979")),
            new BPoint(new BigInteger("88"), new BigInteger("72806030240")),
            new BPoint(new BigInteger("99"), new BigInteger("1471177497"))
        };
        BigInteger p = new BigInteger("81342267667");
        int t = 5;
        int[] indices = new int[] { 0, 1, 2, 3, 6 };
        BPoint[] usePoints = new BPoint[t];
        for (int i = 0; i < t; ++i)
            usePoints[i] = points[indices[i]];
        
        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < t; ++i)
        {
            BigInteger product = usePoints[i].y;
            for (int j = 0; j < t; ++j)
            {
                if (i == j)
                    continue;
                BigInteger num = BigInteger.ZERO.subtract(usePoints[j].x);
                BigInteger den = usePoints[i].x.subtract(usePoints[j].x);
                BigInteger temp = den.modInverse(p);
                product = product.multiply(num).multiply(temp).mod(p);
            }
            sum = sum.add(product).mod(p);
        }
        System.out.println("The secret is " + sum.toString());
    }
    
    public static class BPoint
    {
        BigInteger x;
        BigInteger y;
        public BPoint(BigInteger x, BigInteger y)
        {
            this.x = x;
            this.y = y;
        }
    }
}
