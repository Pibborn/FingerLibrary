package mattia.fingerlib;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SpectralPeakProcessor;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.filters.IIRFilter;
import be.tarsos.dsp.io.PipedAudioStream;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;

import static be.tarsos.dsp.SpectralPeakProcessor.calculateNoiseFloor;
import static be.tarsos.dsp.SpectralPeakProcessor.findLocalMaxima;
import static java.util.Collections.sort;

/**
 * nota: forse sarebbe da prendere anche la lunghezza media del filtro per il noiseFloor. atm è 35, copiato da Joren.
 * Created by mattia cerrato on 07/10/14.
 */
public class PeakExtractor {
    public float noiseFilter;
    public boolean emptyFilter;
    public String audioFileName;
    public boolean isOta;



    public PeakExtractor(String audioFileName, float noiseFilter, boolean emptyFilter, boolean isOta) {
        this.noiseFilter = noiseFilter;
        this.emptyFilter = emptyFilter;
        this.audioFileName = audioFileName;
        this.isOta = isOta;
    }

    public void extract(final int timeGranularity) {
        //set up variabili
        final int sampleRate = 22050;
        final int fftsize = 1024;
        final int overlap = fftsize/2; //25% overlap
        final float[] bandsSize = {0.5f, 0.5f, 1, 1.5f, 1.5f, 1.5f, 1, 0.5f, 0.5f};
        final float[] bandsSize2 = {1, 2.5f, 1.5f, 1};

        //set up oggetti: stream, dispatcher, spectralfollower
        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        PipedAudioStream audioInput = null;
        if (isOta) {
            audioInput = new PipedAudioStream(dirInfo.getOtaDirPath() + audioFileName);
        }
        else {
            audioInput = new PipedAudioStream(dirInfo.getAudioDirPath() + audioFileName);
        }
        TarsosDSPAudioInputStream inputStream = audioInput.getMonoStream(sampleRate);
        AudioDispatcher dispatcher = new AudioDispatcher(inputStream, fftsize, overlap);
//        final HighPass highPassFilter = new HighPass(4000, 44100);
//        dispatcher.addAudioProcessor(highPassFilter);
        final SpectralPeakProcessor spectralFollower = new SpectralPeakProcessor(fftsize, overlap, 22050); //fftsize, overlap, samplerate
        dispatcher.addAudioProcessor(spectralFollower);

        //peakcomparer
        final PeakComparer comp = new PeakComparer();
        //logger
        final PeakLogger logger = new PeakLogger(dirInfo.getLogDirPath()+audioFileName+".log", dirInfo.getPeakDirPath()+audioFileName+"_peaks.log", dirInfo.getrDirPath()+audioFileName+"_matches.log");
        logger.writeIntro();

        //audioprocessor custom per il detecting dei picchi
        dispatcher.addAudioProcessor(new AudioProcessor() {
            int i = 0;
            int yes = 0;
            int no = 0;
            ArrayListMultimap<Integer, SpectralPeakProcessor.SpectralPeak> peakMap = ArrayListMultimap.create();
            @Override
            public boolean process(AudioEvent audioEvent) {
                List localMaxima = null;
                if (emptyFilter == false) {
                    float[] noiseFloor = calculateNoiseFloor(spectralFollower.getMagnitudes(), fftsize / 117, noiseFilter);
                    localMaxima = findLocalMaxima(spectralFollower.getMagnitudes(), noiseFloor);
                }
                else {
                    int size = spectralFollower.getMagnitudes().length;
                    float[] empty = new float[size]; //array di float = 0 per eliminare il filtering del rumore. shazam non lo fa!
                    localMaxima = findLocalMaxima(spectralFollower.getMagnitudes(), empty);
                }
                List<SpectralPeakProcessor.SpectralPeak> peakList;
                List<SpectralPeakProcessor.SpectralPeak> peakListFiltered = new ArrayList<SpectralPeakProcessor.SpectralPeak>();

                peakList = SpectralPeakProcessor.findWeightedPeaks(spectralFollower.getMagnitudes(), spectralFollower.getFrequencyEstimates(), bandsSize2, 15, localMaxima, 10, 20);
//                peakList = SpectralPeakProcessor.findPeaks(spectralFollower.getMagnitudes(), spectralFollower.getFrequencyEstimates(), localMaxima, 10, 20);

                //filtering picchi in base al volume
//                if (peakList.size() > 1) {
//                    float threshold = peakPercentile(peakList, 0.95);
//                    float threshold = 5;
//                    for (SpectralPeakProcessor.SpectralPeak sp : peakList) {
//                        if (sp.getMagnitude() >= threshold) {
//                            peakListFiltered.add(sp);
//                        }
//                    }
//                    logger.writeLogLine(peakList, i);
//                }
//                else {
                    int j = 0;

                    for (float bandSize : bandsSize2) {
                        System.out.println("band:"+j);
                        if (j != 3) {
                            j++;
                            continue;
                        }
                        TimeBandPair timeBandPair = new TimeBandPair(i/timeGranularity, j);
                        int hash = timeBandPair.generateHash();
//                        System.out.println("event:"+i+" band:"+j+" hash:"+hash);
                        List<SpectralPeakProcessor.SpectralPeak> peakCompList = peakMap.get(hash);
                        for (SpectralPeakProcessor.SpectralPeak peak : peakList) {
                            peak.setTimeStamp(i/timeGranularity);
                            if (peakCompList.size() == 0) {
                                peakMap.put(hash, peak);
                            }
                            else {
                                for (int k = 0; k < peakCompList.size(); k++) {
                                    SpectralPeakProcessor.SpectralPeak peakComp = peakCompList.get(k);
                                    if (peakCompList.size() < bandSize) {
                                        peakMap.put(hash, peak);
                                        peakCompList.add(peak);
                                        yes++;
                                    }
                                    else if (peak.getMagnitude() > peakComp.getMagnitude()) {
                                        peakMap.remove(hash, peakComp);
                                        peakMap.put(hash, peak);
                                        peakCompList.remove(k);
                                        peakCompList.add(peak);
                                        no++;
                                    }
                                }
                            }
                        }
                        j++;
                    }
//                    logger.writeLogLine(peakList, i);
//                }
//                logger.writeLogLine(peakList, i);
                i++;
                return true;
            }

            @Override
            public void processingFinished() {
                System.out.println("size peakMap:"+peakMap.size());
                System.out.println("yes:"+yes+" no:"+no);
                logger.writeMapLog(getPeakMap());
                java.util.Date date = new java.util.Date();
                System.out.println("end of processing: "+ new Timestamp(date.getTime()));
            }

            public ArrayListMultimap getPeakMap() {
                return peakMap;
            }

        });
        dispatcher.run();
    }

    public final float peakPercentile(List<SpectralPeakProcessor.SpectralPeak> peaks, double p) {
        if (p < 0 || p > 1)
            throw new IllegalArgumentException("Percentile out of range.");

        final Comparator comp = new PeakComparer();

        //	Sort the array in ascending order.
        sort(peaks, comp);
        SpectralPeakProcessor.SpectralPeak[] arr = peaks.toArray(new SpectralPeakProcessor.SpectralPeak[peaks.size()]);
        //	Calculate the percentile.
        double t = p*(peaks.size() - 1);
        int i = (int)t;
        float threshold;
        return threshold = (float) ((i + 1 - t)*(arr[i].getMagnitude()) + (t - i)*(arr[i+1].getMagnitude()));
    }
}
