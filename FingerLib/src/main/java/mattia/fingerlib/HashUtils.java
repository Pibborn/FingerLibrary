package mattia.fingerlib;

import java.util.BitSet;

/**
 * agglomerato di metodi utili per l'hashing
 * Created by mattia cerrato on 16/10/14.
 */
public class HashUtils {

    private HashUtils() {}

    public static boolean[] toBinary(int number, int base) {
        final boolean[] ret = new boolean[base];
        for (int i = 0; i < base; i++) {
            ret[base - 1 - i] = (1 << i & number) != 0;
        }
        return ret;
    }

    public static int toInt(boolean[] arr) {
        int n = 0;
        for (boolean b : arr)
            n = (n << 1) | (b ? 1 : 0);
        return n;
    }

    public static boolean[] concatThree(boolean[] arr1, boolean[] arr2, boolean[] arr3) {
        boolean[] arrOut = new boolean[arr1.length+arr2.length+arr3.length];
        System.arraycopy(arr1, 0, arrOut, 0, arr1.length);
        System.arraycopy(arr2, 0, arrOut, arr1.length, arr2.length);
        System.arraycopy(arr3, 0, arrOut, arr1.length+arr2.length, arr3.length);
        return arrOut;
    }

    public static boolean[] concatTwo(boolean[] arr1, boolean[] arr2) {
        boolean[] arrOut = new boolean[arr1.length+arr2.length];
        System.arraycopy(arr1, 0, arrOut, 0, arr1.length);
        System.arraycopy(arr2, 0, arrOut, arr1.length, arr2.length);
        return arrOut;
    }

    public static int extractId(BitSet idAndTime, boolean debug) {
        boolean[] idAndTimeBool = HashUtils.bitSetToBool(idAndTime, false);
        boolean[] idBool = new boolean[10];
        for(int i = 0; i < idBool.length; i++) {
            idBool[i] = idAndTimeBool[i];
            if(debug) System.out.print(idBool[i] == false ? 0 : 1);
        }
        if(debug) System.out.println();
        return HashUtils.toInt(idBool);
    }

    public static int extractAnchorTime(BitSet idAndTime, boolean debug) {
        boolean[] idAndTimeBool = HashUtils.bitSetToBool(idAndTime, false);
        boolean[] anchorTimeBool = new boolean[10];
        for(int i = 0; i < anchorTimeBool.length; i++) {
            anchorTimeBool[i] = idAndTimeBool[i+10];
            if(debug) System.out.print(anchorTimeBool[i] == false ? 0 : 1);
        }
        if(debug) System.out.println();
        return HashUtils.toInt(anchorTimeBool);
    }

    public static BitSet toBitSet(boolean[] arr) {
        BitSet temp = new BitSet(arr.length);
        for (int i = 0; i < arr.length; i++) {
            temp.set(i, arr[i]);
        }
        return temp;
    }

    public static boolean[] bitSetToBool(BitSet bitSet, boolean debug) {
        boolean[] temp = new boolean[bitSet.size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = bitSet.get(i);
            if(debug) System.out.print(temp[i] == false ? 0 : 1);
        }
        if(debug) System.out.println();
        return temp;
    }
}
