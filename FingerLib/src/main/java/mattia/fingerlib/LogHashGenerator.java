package mattia.fingerlib;

/**
 * questa classe si differenzia da peakpairhashgenerator perchè invece di prendere due picchi, prende tre interi. pertanto è possibile crearla
 * dal file di log, o comunque senza passare da SpectroGramPeak
 * Created by mattia cerrato on 15/10/14.
 */
public class LogHashGenerator {
    public int bin1;
    public int bin2;
    public int time1;
    public int time2;
    public PeakPairTime peakPair;
    public String trackName;

    public LogHashGenerator(String trackName) {
        this.trackName = trackName;
    }

    public PeakPairTime createHash() {
        PeakPairTime peakPairHash = new PeakPairTime(time1, trackName);
        peakPairHash.generateHash(bin1, bin2, time2 - time1, false);
        this.setPeakPair(peakPairHash);
        return peakPairHash;
    }

    public void setBin1(int bin1) {
        this.bin1 = bin1;
    }

    public void setBin2(int bin2) {
        this.bin2 = bin2;
    }

    public void setTime1(int time1) {
        this.time1 = time1;
    }

    public void setTime2(int time2) {
        this.time2 = time2;
    }

    public void setPeakPair(PeakPairTime peakPair) {
        this.peakPair = peakPair;
    }

    public int getBin1() {
        return bin1;
    }

    public int getBin2() {
        return bin2;
    }

    public int getTime1() {
        return time1;
    }

    public PeakPairTime getPeakPair() {
        return peakPair;
    }

    public int getTime2() {
        return time2;
    }

}
