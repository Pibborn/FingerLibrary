package mattia.fingerlib;

import java.util.BitSet;

/**
 * Created by mattia cerrato on 13/10/14.
 */
public class PeakPairTime {
    public int hash;
    public BitSet anchorAndId;

    public PeakPairTime(int anchorTime, String trackName) {
        //codifica dei 10 bit della traccia con i 10 bit del tempo
        SongMap songMap = SongMap.getInstance();
        int id = songMap.findTrackId(trackName);
        boolean[] boolId = HashUtils.toBinary(id, 10);
        boolean[] boolAnchor = HashUtils.toBinary(anchorTime, 10);
        boolean[] boolAnchorAndId = HashUtils.concatTwo(boolId, boolAnchor);
        this.anchorAndId = HashUtils.toBitSet(boolAnchorAndId);
    }

    public int generateHash(int bin1, int bin2, int timeDifference, boolean debug) {
        boolean[] bool1 = HashUtils.toBinary(bin1, 10);
        boolean[] bool2 = HashUtils.toBinary(bin2, 10);
        boolean[] boolTime = HashUtils.toBinary(timeDifference, 10);
        boolean[] hashBool = HashUtils.concatThree(bool1, bool2, boolTime);
        this.hash = HashUtils.toInt(hashBool);
        if (debug) {
            System.out.print("bool1: ");
            int i = 0;
            for(i = 0; i < bool1.length; i++) {
                System.out.print(bool1[i] == false ? 0 : 1);
            }
            System.out.print("\nbool2: ");
            for(i = 0; i < bool2.length; i++) {
                System.out.print(bool2[i] == false ? 0 : 1);
            }
            System.out.print("\nboolTime: ");
            for(i = 0; i < boolTime.length; i++) {
                System.out.print(boolTime[i] == false ? 0 : 1);
            }
            System.out.print("\nboolConcat: ");
            for(i = 0; i < hashBool.length; i++) {
                System.out.print(hashBool[i] == false ? 0 : 1);
            }
        }
        return hash;
    }

    @Override
    public String toString() {
        return Integer.toString(this.hash);
    }
}
