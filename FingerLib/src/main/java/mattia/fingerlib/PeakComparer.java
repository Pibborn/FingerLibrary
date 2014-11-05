package mattia.fingerlib;

import java.util.Comparator;

import be.tarsos.dsp.SpectralPeakProcessor;

/**
 * Created by mattia cerrato on 07/10/14.
 */
class PeakComparer implements Comparator<SpectralPeakProcessor.SpectralPeak> {

    public PeakComparer () {
    }
    @Override
    public int compare(SpectralPeakProcessor.SpectralPeak spectralPeak, SpectralPeakProcessor.SpectralPeak spectralPeak2) {
        return Float.compare(spectralPeak.getMagnitude(), spectralPeak2.getMagnitude());
    }
}