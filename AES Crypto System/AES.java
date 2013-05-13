/*
 * This program runs the AES algorithm. To run the program, input the message
 * as 16 8-byte groups in binary. Then, input the key as a 16 digit long hex number.
 * 
 * For example, for our problem, we input:
 * 11111111 11111111 11111111 11111111 11111111 11111111 11111111 11111111 
 * 11111111 11111111 11111111 11111111 11111111 11111111 11111111 11111111
 * 2B 7E 15 16 28 AE D2 A6 AB F7 15 88 09 CF 4F 3C
 *
 * Key Schedule
 * First four keys are just columns of the key matrix. After that
 * k[i] = k[i-4] ^ [subBytes(k[i-1](1)) ^ (x^(1-4/4)), subBytes(k[i-1](2)), subBytes(k[i-1](3)), 
 *                 subBytes(k[i-1](0))] if i is divisible by 4,
 * otherwise k[i] = k[i-1] ^ k[i-4]
 * 
 * Encryption
 * Add key 1 to message
 * for 10 times
 *		subBytes
 *		shiftRows
 *		mixColumns
 *		addKey
 *
 * Add Key: XOR round key with the input (message in round 0)
 * Sub Bytes: Convert byte to polynomial and finds its inverse w.r.t. x^8+x^4+x^3+x+1, then multiply
 * 			  with S and adds CV to get a byte.
 * Shift Rows: Shift 2nd, 3rd and 4th rows by 1, 2, and 3 respectively
 * Mix Columns: Multiply current state matrix with a given matrix to mix up columns
 */
 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;


public class AES
{
    PolynomialInverse inverseFinder;

    public static void main(String[] args) throws FileNotFoundException
	{
		//Read in the input
        Scanner scan = new Scanner(new File("IN.txt"));
		//use 4x4 matrices to represent the message and the key
        int[][] message = new int[4][4];
        int[][] key = new int[4][4];
		//Use a map to record the key schedule
        HashMap<Integer, int[]> keyMap = new HashMap<Integer, int[]>();
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
				//Read the message as bits into columns
                message[j][i] = Integer.parseInt(scan.next(), 2);
        for (int i = 0; i < 4; ++i)
            for (int j = 0; j < 4; ++j)
				//Read the key in hex into columns
                key[j][i] = Integer.parseInt(scan.next(), 16);
        //Generate the keys before running encryption algorithm
		populateKeys(keyMap, key);
        encrypt(message, keyMap);
        scan.close();
    }

    private static void printMatrix(int[][] matrix, boolean flag)
    {
		//This function will print out the matrix, in either row major or column major order
        if (flag)
		{
            for (int i = 0; i < 4; ++i)
            {
                for (int j = 0; j < 4; ++j)
                    System.out.printf("%02x ", matrix[i][j]);
                System.out.println();
            }
		}
        else
		{
            for (int i = 0; i < 4; ++i)
            {
                for (int j = 0; j < 4; ++j)
                    System.out.printf("%02x ", matrix[j][i]);
                System.out.println();
            }
		}
    }

    private static void encrypt(int[][] message, HashMap<Integer, int[]> keyMap)
	{
        System.out.println("Round 0");
        //Start the encryption with just a XOR with the key
		addKey(0, keyMap, message);
        System.out.print("Message:\n--------\n");
        printMatrix(message, true);
        System.out.println();
		//Run the algorithm 10 times
        for (int i = 1; i < 11; ++i) {
            for (int j = 0; j < 4; ++j)
                for (int k = 0; k < 4; ++k)
					//Do subBytes first
                    message[j][k] = subBytes(message[j][k]);

            System.out.println("Round: " + i);
			//Call shiftRows
            message = shiftRows(message);

            if (i != 10) {
				//Only do mixColumns if its not the last round
                message = mixColumns(message);
            }
            //Add key again
            addKey(i, keyMap, message);
            System.out.print("Message:\n--------\n");
            printMatrix(message, true);
            System.out.println();
        }
    }

    private static int subBytes(int input)
	{
		//To do subBytes, get the inverse of the byteToField(input), with respect to x^8+x^4+x^3+x+1
		//Then call a matrix function that multiplies that with S and adds CV to get a byte back
        return fieldToByte(matrixByteMultiplyAdd((new PolynomialInverse(
                byteToField(input), new int[] { 1, 0, 0, 0, 1, 1, 0, 1, 1 }, 2))
                .findInverse()));
    }

    private static int[][] shiftRows(int[][] S)
	{
		//Shift rows simply returns an array with the 2st, 3nd and 4rd rows shifted left by 1 2, 3, respectively
        return new int[][] { S[0], { S[1][1], S[1][2], S[1][3], S[1][0] },
                { S[2][2], S[2][3], S[2][0], S[2][1] },
                { S[3][3], S[3][0], S[3][1], S[3][2] } };
    }

    private static int[][] mixColumns(int[][] S)
	{
		//Will hold the answer
        int[][] answer = new int[4][4];
		//This matrix represents the M matrix, given to us, we used x+1=3, x=2, 1=1(Polynomials to byte)
        int[][] M = new int[][] { { 2, 3, 1, 1 }, { 1, 2, 3, 1 },
                { 1, 1, 2, 3 }, { 3, 1, 1, 2 } };
		//Loop through the 4 columns of answer
        for (int i = 0; i < 4; i++)
		{
			//Loop through the 4 rows of answer
            for (int j = 0; j < 4; j++)
			{
				//Use this total array to sum up the result of the multiplication that goes in answer[i,j]
                int[] total = new int[] {};
                for (int k = 0; k < 4; k++)
				{
					//This will multiply two fields, M[i,k] and S[k,j], and sum up the result in total
                    total = fieldAdd(total, fieldMultiply(byteToField(M[i][k]),
                                    byteToField(S[k][j])));
                }
				//Store the result of the multiplication mod the field x^8+x^4+x^3+x+1, in answer
                answer[i][j] = fieldToByte(doModPoly(total));
            }
        }
        return answer;
    }

    private static void addKey(int round, HashMap<Integer, int[]> keyMap, int[][] message)
	{
        System.out.print("Key\n----------\n");
		//Get each of the four keys
        for (int i = 0 ; i < 4; i++)
		{
			//Get the correct key
            int [] key = keyMap.get(round*4 + i);
			//Loop through each column in the key
            for (int j = 0; j < 4; j++)
			{
				//Do the XOR and store it back in the array
                message[j][i] = message[j][i]^key[j];
            }
        }
        printMatrix(new int[][] {keyMap.get(round*4), keyMap.get(round*4+1), 
                keyMap.get(round*4+2), keyMap.get(round*4+3)}, false);
    }

    private static int[] doModPoly(int[] total)
	{
		//This function does polynomial mod in field 2^8
        if (total.length == 9)
		{
			//Only if we have overflow
            if (total[0] == 0)
                return Arrays.copyOfRange(total, 1, total.length);
            else
			{
				//Take out the top value (removing the x^8)
                int[] copy = Arrays.copyOfRange(total, 1, total.length);
				//Add one to the other locations using the fact that x^8 = x^4+x^3+x+1
                copy[3] = Math.abs(copy[3] + 1) % 2;
                copy[4] = Math.abs(copy[4] + 1) % 2;
                copy[6] = Math.abs(copy[6] + 1) % 2;
                copy[7] = Math.abs(copy[7] + 1) % 2;
                return copy;
            }
        }
		else if (total.length == 10)
		{
			//If we have x^9, return x^5+x^4+x^2+x(Needed for key schedule)
            return new int[]{1, 1, 0, 1, 1, 0};
        }
        return total;
    }

    private static int[] fieldMultiply(int[] M, int[] S)
	{
		//Multiplies two fields
		//Create an array (field) big enough to hold result
        int[] p = new int[M.length + S.length - 1];
		//Loop through M
        for (int j = 0; j < M.length; ++j)
			//Loop thorugh S
            for (int k = 0; k < S.length; ++k)
				//This calculation adds the value at each location in the array, boils down to binary addition
                p[k + j] = Math.abs(p[k + j] + M[j] * S[k]) % 2;
        return p;
    }

    private static int[] fieldAdd(int[] total, int[] product)
	{
		//This functions adds two fields
        int[] ans = null;
		//Make the answer, fill it with the bigger fields
        if (product.length > total.length)
            ans = Arrays.copyOf(product, product.length);
        else
            ans = Arrays.copyOf(total, total.length);
		//Loop through the smaller field
        int length = Math.min(product.length, total.length);
        for (int i = 0; i < length; ++i)
			//Start from the back and find the value of the addition, mod 2
            ans[ans.length - i - 1] = Math.abs(product[product.length - i - 1]
                    + total[total.length - i - 1]) % 2;
        return ans;
    }

    private static int[] matrixByteMultiplyAdd(int[] byteToField)
	{
		//This function is called in subbytes to multiply and add two matrices
        int[] byteToFieldTemp = new int[8];
        int start = 8 - byteToField.length;
		//byteToFieldTemp will make sure we have an array with proper dimensions (8x1)
        for (int i = start, j = 0; i < byteToFieldTemp.length; ++i, ++j)
            byteToFieldTemp[i] = byteToField[j];
        int[] answer = new int[8];
		//Circulant array. Given
        int[][] S = new int[][] { { 1, 0, 0, 0, 1, 1, 1, 1 },
                { 1, 1, 0, 0, 0, 1, 1, 1 }, { 1, 1, 1, 0, 0, 0, 1, 1 },
                { 1, 1, 1, 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1, 0, 0, 0 },
                { 0, 1, 1, 1, 1, 1, 0, 0 }, { 0, 0, 1, 1, 1, 1, 1, 0 },
                { 0, 0, 0, 1, 1, 1, 1, 1 } };
		//Constant array. Given
        int[] CV = new int[] { 1, 1, 0, 0, 0, 1, 1, 0 };
		//Loop through each row in S
        for (int i = 0; i < S.length; i++)
		{
            int total = 0;
			//Loop through each column in S
            for (int j = 0; j < S[0].length; j++)
				//Do the multiplication and put the result in total
                total += S[i][j] * byteToFieldTemp[7 - j];
			//Do the addition, mod 2
            answer[i] = Math.abs(total + CV[i]) % 2;
        }
		//reverse the array 
        for (int i = 0; i < 4; ++i)
        {
            int temp = answer[i];
            answer[i] = answer[7-i];
            answer[7-i] = temp;
        }
        return answer;
    }

	private static int fieldToByte(int[] matrix)
	{
		//Converts a given field to a byte
        String str = "";
		//Add up the value in the matrix
        for (int i : matrix)
            str += "" + i;
		//Treat the value in binary and return the reulting byte
        return Integer.parseInt(str, 2);
    }
	
    private static int[] byteToField(int b)
	{
		//Convert a given byte to a field
        if (b == 0)
            return new int[] {};
		//Makes use of toBinArray and java functions to parse bytes in binary
        return toBinArray(Integer.toBinaryString(b));
    }

    private static int[] toBinArray(String binaryString)
	{
		//Simply converts a string into a binary array, padding the array with leading 0s, if needed
        int[] answer = new int[binaryString.length()]; //creates an array with all zeros
        for (int i = 0; i < answer.length; ++i)
            answer[i] = binaryString.charAt(i) - '0';
        return answer;
    }

    private static void populateKeys(HashMap<Integer, int[]> keyMap, int[][] keys)
	{
		//The next four lines puts W(0), W(1), W(2), W(3)
        keyMap.put(0, new int[] { keys[0][0], keys[1][0], keys[2][0], keys[3][0] });
        keyMap.put(1, new int[] { keys[0][1], keys[1][1], keys[2][1], keys[3][1] });
        keyMap.put(2, new int[] { keys[0][2], keys[1][2], keys[2][2], keys[3][2] });
        keyMap.put(3, new int[] { keys[0][3], keys[1][3], keys[2][3], keys[3][3] });
		//Create keys for every i, from 4 to 43, inclusive
        for (int i = 4; i <= 43; i++)
		{
            int[] wi_1 = keyMap.get(i - 1);
            int[] wi_4 = keyMap.get(i - 4);
			//If i is a multiple of 4
            if (i % 4 == 0)
			{
                int[] modWi_1 = new int[4];
				//calculate the exponent (i-4)/4
                int exp = (i-4)/4;
				//Create the field with the exponent
                int [] x = new int[exp+1];
				//Make the top bit 1
                x[0] = 1;
				//Make sure we handle overflow
                x = doModPoly(x);
				//The first entry does a subBytes, and adds	the result with x^((i-4)/4)
                modWi_1[0] = fieldToByte(fieldAdd(byteToField(subBytes(wi_1[1])), x));
				//Find the subbytes of the remaining values, rotated by one
                modWi_1[1] = subBytes(wi_1[2]);
                modWi_1[2] = subBytes(wi_1[3]);
                modWi_1[3] = subBytes(wi_1[0]);
				//Put the result in the schedule, with the XOR
                keyMap.put(i, new int[] { modWi_1[0] ^ wi_4[0], modWi_1[1] ^ wi_4[1],
                        modWi_1[2] ^ wi_4[2], modWi_1[3] ^ wi_4[3] });
            }
			else
			{
				//If i is not a multiple of 4, just computer wi-4^wi-1
                keyMap.put(i, new int[] { wi_1[0] ^ wi_4[0], wi_1[1] ^ wi_4[1],
                        wi_1[2] ^ wi_4[2], wi_1[3] ^ wi_4[3] });
            }
        }
    }
}
