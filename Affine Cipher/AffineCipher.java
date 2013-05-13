/*
 * Program to encrypt using Affine cipher
 *
 * Input: String to encrypt, a and b
 * Cipher Text = (a * Plain Text) + b % 26
 */

import java.util.HashMap;


public class AffineCipher
{
    public static void main(String [] args)
    {
        String encodedString = args[0];
        int a = Integer.parseInt(args[1]);
        int b = Integer.parseInt(args[2]);
        HashMap<Character, Character> map = new HashMap<Character, Character>();
        for (int i = 1; i < 27; ++i)
            map.put((char)(((a * i + b) % 26) + 'A'), (char)('A' + i - 1));
        
        StringBuilder sB = new StringBuilder(encodedString);
        for (int i = 0; i < sB.length(); ++i)
            sB.setCharAt(i, map.get(sB.charAt(i)));
        System.out.println(sB.toString());
    }
}
