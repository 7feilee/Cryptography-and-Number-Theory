/*
 * RSA Algorithm for encryption and decryption
 *
 * Encryption
 * Reads the plain text from a file and the number N and encryption exponent b from command line args
 * First we digitize the message and then the cipher text, C = M^b mod N
 *
 * Decryption
 * Reads the cipher text from a file and the number N and encryption exponent b from command line args
 * First we compute the encryption exponent by finding phi(N) and then finding the inverse of b mod
 * phi(N). Once the cipher text is read, we find the message M = C^a mod N and then de-digitize 
 * M to get plain text
 * C^a = M^ab = M (because a and b have to be inverse of each other mod phi(N))
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;


public class RSA
{
    public static void main(String [] args) throws IOException
    {
        //Check whether we want to encrypt or decrypt
        if (args[0].equals("encrypt"))
            runEncrypt(args);
        else
            runDecrypt(args);
    }
    
    private static void runEncrypt(String[] args) throws IOException
    {
        //Create a scanner to read files and store N and b
        Scanner in = new Scanner(new File("plaintext.txt"));
        BigInteger N = new BigInteger(args[1]);
        BigInteger b = new BigInteger(args[2]);
        PrintWriter writer = new PrintWriter("ciphertext.txt");
        
        //For each word first convert it to digital string as described in the
        //textbook and then write it to a file
        while (in.hasNext())
        {
            String s = in.next();
            int digitalString = getDigitalString(s);
            BigInteger digitalBigInt = new BigInteger(digitalString + "");
            writer.write(digitalBigInt.modPow(b, N).toString());
        }
        in.close();
        writer.flush();
        writer.close();
    }
    
    private static int getDigitalString(String str)
    {
        //Converts string to integer as described in textbook
        //Example: DOG = 26^2 * 3 + 26 * 14 + 6
        int sum = 0;
        int index = str.length() - 1;
        for (char c : str.toCharArray())
            sum += Math.pow(26, index--) * (c - 'A');
        return sum;
    }
    
    private static void runDecrypt(String[] args) throws IOException
    {
        //Create a scanner to read file and variables to store N and b
        Scanner in = new Scanner(new File("ciphertext.txt"));
        BigInteger N = new BigInteger(args[1]);
        BigInteger b = new BigInteger(args[2]);
        PrintWriter writer = new PrintWriter("plaintext.txt");
        
        //Factor N and calculate phi(N) and then run extended euclid to calculate
        //the decryption coefficient from phi(N) and b
        int phiN = getPhiN(N.intValue());
        BigInteger a = new BigInteger(extendedEuclidInverse(phiN, b.intValue()) + "");
        
        //For each token decrypt it as shown in the textbook and then convert
        //the integer to alphabets again as shown in the textbook
        while (in.hasNext())
        {
            BigInteger message = new BigInteger(in.next());
            int digitalMessage = message.modPow(a, N).intValue();
            writer.write(getMessage(digitalMessage));
        }
        in.close();
        writer.flush();
        writer.close();
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
    
    private static int getPhiN(int N)
    {
        //Factorize N to two prime numbers to calculate phi(N)
        boolean[] composites = sieve(N);
        for (int i = 1; i <= composites.length; ++i)
            if (!composites[i] && N % i == 0)
                return (i - 1) * ((N / i) - 1);
        return 0;
    }
    
    private static boolean[] sieve(int number)
    {            
        //Array of booleans, false represents prime, true means not prime
        boolean[] array = new boolean[number + 1];
        array[1] = true;  //1 is not prime, so set it to true

        //Set all multiples of the number to true as they are composite
        //except the number itself as it will be prime
        //End when the square of loop counter is bigger than the input
        for (int i = 2; i <= number; i++)
        {
            if (i * i > number)
                break;
            for (int j = 2; i * j <= number; j++)
                array[i * j] = true;
        }
        return array;
    }

    private static int extendedEuclidInverse(int a, int b)
    {
        int[] aSeed = {1, 0};
        int[] bSeed = {0, 1};
        while (true) //Divide until the remainder is 0
        {
            int quotient = a / b;
            int remainder = a % b;

            if (remainder == 0)
                break;
            
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
        return bSeed[1];
    }
}
