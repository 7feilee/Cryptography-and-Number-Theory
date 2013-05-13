/*
 * Pollard p-1 method to factor an integer N
 *
 * Start with answer as 2 (needs to be relatively prime to N). Keep raising answer to increasing 
 * powers as we iterate, thus producing ans^B! at each iteration. We calculate the gcd of this value
 * with N and if the gcd is not 1 or N, then gcd is the factor, otherwise we keep going
 */

import java.math.BigInteger;


public class PollardP1Factor
{
    public static void main(String [] args)
    {
        //Let alpha = 2, so start answer with 2
        BigInteger answer = BigInteger.ONE.add(BigInteger.ONE);
        BigInteger N = new BigInteger(112426043 + "");
        //Iterate and calculate answer^B! mod N, then find gcd
        //If gcd is 1 or N, then continue, else g is a factor
        for (int i = 1; ; ++i)
        {
            answer = answer.modPow(new BigInteger(i + ""), N);
            BigInteger tempAnswer = answer.subtract(BigInteger.ONE);
            BigInteger gcd = tempAnswer.gcd(N);
            if (!gcd.equals(BigInteger.ONE) && !gcd.equals(N))
            {
                System.out.println(gcd);
                break;
            }
        }
    }
}
