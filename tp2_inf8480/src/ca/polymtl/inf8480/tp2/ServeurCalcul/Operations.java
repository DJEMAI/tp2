package ca.polymtl.inf8480.tp2.ServeurCalcul;

import static java.lang.StrictMath.sqrt;

public class Operations {

    // inspired by: https://www.geeksforgeeks.org/pell-number/
    public static int pell(int x) {
        return (x <= 2) ? x : (2 * pell(x - 1) + pell(x - 2));
    }

    // inspired by: https://www.geeksforgeeks.org/find-largest-prime-factor-number/
    public static int prime(int x) {

        int highestPrime = -1;

        while (x % 2 == 0){
            highestPrime = 2;
            x >>= 1;
        }

        for (int i = 3 ; i < sqrt(x); i += 2) {
            while(x % i == 0){
                highestPrime = i;
                x /= i;
            }
        }

        if(x > 2){
            highestPrime = x;
        }

        return highestPrime;
    }


}