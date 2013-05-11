/*
 * Program for performing three different operations on points on elliptic curves
 * 1. Verify: Verifies if a point is on the elliptic curve defined by y^2 = x^3 + ax + b mod p
 * 2. Add: Adds two points on the elliptic curve
 * 3. Multiply: Multiplies a point with a constant on an elliptic curve
 *
 * Command line arguments for different modes
 * Verify: <p> <a> <b> verify <point.x> <point.y>
 * Add: <p> <a> <b> add <point1.x> <point1.y> <point2.x> <point2.y>
 * Multiply: <p> <a> <b> multiply <point.x> <point.y> <multiplier>
 *
 * The following rules are followed for point of infinity O
 * i. O + P = P + O = O
 * ii. O + O = O
 */
 
import java.awt.Point;
import java.math.BigInteger;

public class EllipticCurves
{
    // Some variables
    static int prime;
    static int a;
    static int b;

    public static void main(String[] args)
    {
        prime = Integer.parseInt(args[0]);
        a = Integer.parseInt(args[1]);
        b = Integer.parseInt(args[2]);

        // Determine whether to verify the point, add points, or find a multiple
        // of a point
        switch (args[3].toLowerCase())
        {
            case "verify":
                Point p = new Point(Integer.parseInt(args[4]),
                        Integer.parseInt(args[5]));
                // Verify the point and print the result
                if (verifyPoint(p, true))
                    System.out.printf("Point (%d, %d) is on the curve\n", p.x,
                            p.y);
                else
                    System.out.printf("Point (%d, %d) is not on the curve\n",
                            p.x, p.y);
                break;
            case "add":
                Point A = new Point(Integer.parseInt(args[4]),
                        Integer.parseInt(args[5]));
                Point B = new Point(Integer.parseInt(args[6]),
                        Integer.parseInt(args[7]));
                // Verify the points, then add them
                if (verifyPoint(A, false) && verifyPoint(B, false))
                {
                    Point sum = addPoint(A, B);
                    System.out
                            .printf("Sum of points (%d, %d) and (%d, %d) is (%d, %d)\n",
                                    A.x, A.y, B.x, B.y, sum.x, sum.y);
                }
                else
                    System.out
                            .printf("Points (%d, %d) and/or (%d, %d) are not on the curve\n",
                                    A.x, A.y, B.x, B.y);
                break;
            case "multiply":
                Point P = new Point(Integer.parseInt(args[4]),
                        Integer.parseInt(args[5]));
                int multiplier = Integer.parseInt(args[6]);
                // Verify the point and then find the multiple of the point
                if (verifyPoint(P, false))
                {
                    Point multiply = multiplyPoint(P, multiplier);
                    System.out
                            .printf("Point (%d, %d) multiplied with %d gives the point (%d, %d)\n",
                                    P.x, P.y, multiplier, multiply.x,
                                    multiply.y);
                }
                else
                    System.out.printf("Point (%d, %d) is not on the curve\n",
                            P.x, P.y);
                break;
        }
    }

    public static boolean verifyPoint(Point p, boolean print)
    {
        if (print)
        {
            // Calculate the left hand side (y^2) and right hand side (x^3 + ax
            // + b)
            int lhs = (p.y * p.y) % prime;
            int rhs = (p.x * p.x * p.x + a * p.x + b) % prime;
            // Print in nice readable form
            System.out.println("Left Hand Side: y^2");
            System.out.printf("y^2 = %d * %d = %d mod %d\n", p.y, p.y, lhs,
                    prime);
            System.out.println("Right Hand Side: x^3 + ax + b");
            System.out.printf(
                    "x^3 + ax + b = (%d)^3 + (%d)(%d) + %d = %d mod %d\n", p.x,
                    a, p.x, b, rhs, prime);
            return lhs == rhs;
        }
        else
            // Just calculate equality, no need to print
            return ((p.y * p.y) % prime) == ((p.x * p.x * p.x + a * p.x + b) % prime);
    }

    public static Point addPoint(Point A, Point B)
    {
        // If B is the point of infinity, then return A as the sum or viceversa
        if (B.x == Integer.MAX_VALUE && B.y == Integer.MAX_VALUE)
            return A;
        if (A.x == Integer.MAX_VALUE && A.y == Integer.MAX_VALUE)
            return B;
        int m = 0;
        if (A.equals(B)) // A == B, so use the formula of slope obtained by
                         // formula of tangent line to curve
        {
            BigInteger num = new BigInteger((3 * A.x * A.x + a) + "");
            BigInteger den = new BigInteger((2 * A.y) + "");
            m = num.multiply(den.modInverse(new BigInteger(prime + "")))
                    .mod(new BigInteger(prime + "")).intValue();
        }
        else // Formula of slope from slope of line passing through two points
        {
            BigInteger num = new BigInteger((B.y - A.y) + "");
            BigInteger den = new BigInteger((B.x - A.x) + "");
            m = num.multiply(den.modInverse(new BigInteger(prime + "")))
                    .mod(new BigInteger(prime + "")).intValue();
        }
        Point C = new Point();
        C.x = (m * m - A.x - B.x) % prime; // x coordinate is m^2 - x1 - x2
        if (C.x < 0)
            C.x += prime;
        // Plug x coordinate in point/slope formula to get y coordinate
        C.y = ((m * (C.x - A.x) + A.y) * -1) % prime;
        if (C.y < 0)
            C.y += prime;
        return C;
    }

    public static Point multiplyPoint(Point P, int multiplier)
    {
        // Get the binary of multiplier
        char[] bits = Integer.toBinaryString(multiplier).toCharArray();
        Point answer = new Point(P);
        // Loop to add and double point
        for (int i = 1; i < bits.length; ++i)
        {
            // Always double the point
            answer = addPoint(answer, answer);
            // If 1 bit, then add P
            if (bits[i] == '1')
                answer = addPoint(answer, P);
        }
        return answer;
    }
}
