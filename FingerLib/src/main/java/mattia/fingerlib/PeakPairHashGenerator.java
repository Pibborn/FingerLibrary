package mattia.fingerlib;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import be.tarsos.dsp.SpectralPeakProcessor;

/**
 * Questa classe è molto delicata perchè letteralmente migliaia di questi oggetti devono venire creati per effettuare il matching
 * Per ora l'idea è di generare una sola PeakPairHashGenerator. Per creare nuovi oggetti si richiama createHash, che genera un PeakPairTime
 * PeakPairTime.generateHash a sua volta genera un PeakPair. PeakPair finalmente calcola il suo Hash. Quello che guardo davvero sarà però
 * PeakPairTime che contiene la hash dei due picchi+la differenza in tempo e il tempo dal primo picco
 * Created by mattia cerrato on 09/10/14.
 */
public class PeakPairHashGenerator {
    SpectralPeakProcessor.SpectralPeak peak1;
    SpectralPeakProcessor.SpectralPeak peak2;
    String trackName;

    public PeakPairHashGenerator(String trackName) {
        this.trackName = trackName;
    }

    public PeakPairTime createHash() {
        PeakPairTime peakPairHash = new PeakPairTime(peak1.getTimeStamp(), trackName);
        peakPairHash.generateHash(peak1.getBin(), peak2.getBin(), (peak2.getTimeStamp() - peak1.getTimeStamp()), true);
        return peakPairHash;
    }

    public void setPeak1(SpectralPeakProcessor.SpectralPeak peak1) {
        this.peak1 = peak1;
    }

    public void setPeak2(SpectralPeakProcessor.SpectralPeak peak2) {
        this.peak2 = peak2;
    }


}
