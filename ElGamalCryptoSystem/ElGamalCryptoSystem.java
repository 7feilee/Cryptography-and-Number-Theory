/*
 * Program for decrypting ElGamal crypto system.
 *
 * How it works? (Alice wants to send a message to Bob)
 * Encryption: Bob chooses a prime p and a primitive element alpha mod p. He then computes 
 * beta = alpha ^ a mod p, where a is a random integer. Bob publishes p, alpha and beta and these
 * values are publicly available. a is kept secret. Next Alice chooses random number k mod p. To
 * send a message X, she computes to values: y1 = alpha ^ k mod p and y2 = X(beta ^ k) mod p. She
 * sends y1 and y2 to Bob.
 *
 * Decryption: Once Bob receives y1 and y2, he can compute X because X = y2((y1 ^ a) ^ -1) mod p.
 * y2 = X(beta ^ k) = X((alpha ^ a) ^ k) = X((alpha ^ k) ^ a) = X(y1 ^ a).
 *
 * The plain text message needs to be "digitized", or in other words alphabets need to be converted
 * to numbers. This decryption program assumes a digitization scheme based on the following example
 * CAT = 26*26*2 + 26*0 + 19 = 1371 (map A=0, B=1, ..., and then convert to decimal)
 */
 
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;


public class ElGamalCrytpoSystem
{
    public static void main(String [] args) throws IOException
    {
        //The prime and a
        BigInteger p = new BigInteger(args[0]);
        BigInteger a = new BigInteger(args[1]);
        
		//Read cipher text from a file
        Scanner scan = new Scanner(new File("input.txt"));
        while (scan.hasNextInt())
        {
            //Scan y1 and y2 from the input and calculate y2.y1^-a mod p
            BigInteger y1 = scan.nextBigInteger();
            BigInteger y2 = scan.nextBigInteger();
            BigInteger x = y2.multiply(y1.modPow(BigInteger.ZERO.subtract(a), p)).mod(p);
            //Convert number to alphabets and print it out
            System.out.print(getMessage(x.intValue()));
        }
        scan.close();
    }
    
    private static String getMessage(int num)
    {
        //Convert the integer to alphabets by converting base 10 integer to base
        //26 and then associating numbers with alphabets. Example: 0=A, 1=B, ...
        ArrayList<Integer> alphabets = getBaseP(num, 26);
        String message = "";
        for (Integer i : alphabets)
            message = message + ((char)(i + 'A'));
        return message;
    }
    
    private static ArrayList<Integer> getBaseP(int n, int p)
    {
        //Convert integer to base 26 number by constantly dividing by 26 and 
        //keeping track of the remainder. Then add the remainders to a list in
        //opposite order and return it
        ArrayList<Integer> list = new ArrayList<Integer>();
        int temp = n;
        while (temp >= p)
        {
            list.add(0, temp % p);
            temp = temp / p;
        }
        list.add(0, temp);
        if (list.size() == 1)
            list.add(0, 0);
        if (list.size() == 2)
            list.add(0, 0);
        return list;
    }
}
