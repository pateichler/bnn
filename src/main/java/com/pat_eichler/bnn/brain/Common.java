package com.pat_eichler.bnn.brain;

public class Common {

    // Solution found online
    /**
     * Calculate binary logarithm of number
     * @param bits is number to apply logarithm
     * @return the logarithm of bits
     */
    public static int binlog( int bits )
    {
        int log = 0;
        if( ( bits & 0xffff0000 ) != 0 ) { bits >>>= 16; log = 16; }
        if( bits >= 256 ) { bits >>>= 8; log += 8; }
        if( bits >= 16  ) { bits >>>= 4; log += 4; }
        if( bits >= 4   ) { bits >>>= 2; log += 2; }
        return log + ( bits >>> 1 );
    }

    public static int numBitsToEncode(int x){
        if(x <= 0)
            throw new RuntimeException("Can't encode negative amount");

        return binlog(x-1)+1;
    }
    public static int[] combineArray(int[] a, int[] b){
        int[] array = new int[a.length + b.length];

        for (int i = 0; i < array.length; i++)
            array[i] = i < a.length ? a[i] : b[i - a.length];

        return array;
    }
}
