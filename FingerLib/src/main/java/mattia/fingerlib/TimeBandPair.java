package mattia.fingerlib;

/**
 * Created by mattia cerrato on 03/11/14.
 */
public class TimeBandPair {
    private int event;
    private int bandNumber;
    private int hash;

    public TimeBandPair(int event, int bandNumber) {
        this.event = event;
        this.bandNumber = bandNumber;
    }

    public int getEvent() {
        return event;
    }

    public int getBandNumber() {
        return bandNumber;
    }

    public int generateHash() {
        boolean[] arr = HashUtils.concatTwo(HashUtils.toBinary(event, 10), HashUtils.toBinary(bandNumber, 10));
        this.hash = HashUtils.toInt(arr);
        return hash;
    }
}
