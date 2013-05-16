/*
 * Shanks' method to solve the discrete log problem mod p. Given beta = alpha ^ l mod p, we want
 * to find l.
 *
 * We keep track of two lists of tuples (i,j goes from 0 to m-1, m = ceil(sqrt(order(alpha))))
 *		L1 = (j, alpha^mj)
 *		L2 = (i, beta * alpha ^ -i)
 * Next we sort the lists on the second term and try to find a match on the second term.
 * Once we find the match we know that alpha^mj = beta * alpha^-i
 * Thus alpha^(mj+i) = beta, which is the definition of discrete log. Thus l = mj + i on the 
 * matching tuple
 */

import java.math.BigInteger;
import java.util.Arrays;


public class ShanksDiscreteLog
{
    public static void main(String [] args)
    {
        //Initialize prime, m, alpha, and beta
        final int prime = 526483;
        final int m = (int)(Math.ceil(Math.sqrt(prime - 1)));
        final BigInteger alpha = new BigInteger(2 + "");
        final BigInteger beta = new BigInteger(767 + "");
        
        //Array to store pairs
        Tuple[] L1 = new Tuple[m];
        Tuple[] L2 = new Tuple[m];
        
        //Calculate the pairs and store in the tuple array
        for (int i = 0; i < m; ++i)
        {
            BigInteger p = new BigInteger(prime + "");
            L1[i] = new Tuple(i, alpha.modPow(new BigInteger((m * i) + ""), p));
            L2[i] = new Tuple(i, beta.multiply(alpha.modPow(new BigInteger((-1 * i) + ""), p)).mod(p));
        }
        
        //Sort the arrays
        Arrays.sort(L1);
        Arrays.sort(L2);
        
        //Print the lists
        System.out.println("L1: " + Arrays.toString(L1));
        System.out.println("L2: " + Arrays.toString(L2));
        
        //Find a match
        for (int i = 0, j = 0; i < m && j < m; )
        {
            if (L1[j].partB.equals(L2[i].partB))
            {
                System.out.println(m * L1[j].partA + L2[i].partA);
                break;
            }
            else if (L1[j].partB.compareTo(L2[i].partB) < 0)
                ++j;
            else
                ++i;
        }
    }
    
    private static class Tuple implements Comparable<Tuple>
    {
        int partA;
        BigInteger partB;
        
        //Constructor
        public Tuple(int partA, BigInteger partB)
        {
            this.partA = partA;
            this.partB = partB;
        }

        //Compare to Tuples for sorting
        @Override
        public int compareTo(Tuple that)
        {
            return this.partB.compareTo(that.partB);
        }
        
        //ToString
        @Override
        public String toString()
        {
            return "(" + partA + ", " + partB + ")";
        }
    }
}
