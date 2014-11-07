package mattia.fingerlib;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import java.io.File;
import java.util.BitSet;
import java.util.List;

import be.tarsos.dsp.SpectralPeakProcessor;

/*todo: serve un'idea migliore per togliere le righe di setup del logger da ogni metodo di fingerprinting-matching */

/**
 * la classe FingerprintGenerator si occupa di creare, mantenere ed aggiornare la hashmap di fingerprint di tutte le tracce nel database.
 * Created by mattia cerrato on 21/10/14.
 */
public class FingerprintGenerator {
    private ListMultimap<Integer, BitSet> fingerMap;
    private ListMultimap<String, TimePair> matchMap;
    public File track;

    public FingerprintGenerator() {
        this.fingerMap = ArrayListMultimap.create();
        this.matchMap = ArrayListMultimap.create();
    }

    public List getFingerMap(int key) {
        return fingerMap.get(key);
    }

    public List<TimePair> getMatchMap(int key) {
        return matchMap.get(track.getName());
    }

    public void putMatchMap(String trackName, TimePair timePair) {
        matchMap.put(trackName, timePair);
    }

    public void setTrack(File track) {
        this.track = track;
    }

    public File getTrack() {
        return track;
    }

    public ListMultimap getFingerMap() {
        return fingerMap;
    }

    public ListMultimap generateFingerprints(boolean noiseFilter, boolean matching) {
        if (track.getName().compareTo(".DS_Store") == 0) return null;
        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        PeakLogger logger = new PeakLogger(dirInfo.getLogDirPath(), dirInfo.getPeakDirPath(), dirInfo.getrDirPath());
        LogHashGenerator logHashGenerator = new LogHashGenerator(track.getName());
        logger.setLogPath(dirInfo.getLogDirPath() + track.getName() + ".log");
        logger.setPeakPath(dirInfo.getPeakDirPath() + track.getName() + "_peaks.log");
        logger.setrPath(dirInfo.getrDirPath() + track.getName() + "_matches.log");

        //estrazione picchi e scrittura del log (nascosta)
        PeakExtractor extractorNullFilter = new PeakExtractor(track.getName(), (float) 1.8, noiseFilter, matching);
        extractorNullFilter.extract(10);
        Multimap<Integer, SpectralPeakProcessor.SpectralPeak> peakMap;

        //parsing del log
        List<Double>[] arr = logger.parseLogFile(dirInfo.getLogDirPath() + track.getName() + ".log");
        List<Double> timeList = arr[0];
        List<Double> binList = arr[1];
        List<Double> frequencyList = arr[2];
        List<Double> volumeList = arr[3];

        //costruzione della hashmap, oppure matching
        if (!matching) return fillMap(timeList, binList, frequencyList);
        else return matchFingerprints(timeList, binList, frequencyList);
    }

    public ListMultimap fillMap(List<Double> timeList, List<Double> binList, List<Double> frequencyList) {
        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        PeakLogger logger = new PeakLogger(dirInfo.getLogDirPath(), dirInfo.getPeakDirPath(), dirInfo.getrDirPath());
        LogHashGenerator logHashGenerator = new LogHashGenerator(track.getName());
        logger.setLogPath(dirInfo.getLogDirPath() + track.getName() + ".log");
        logger.setPeakPath(dirInfo.getPeakDirPath() + track.getName() + "_peaks.log");
        logger.setrPath(dirInfo.getrDirPath() + track.getName() + "_matches.log");
        logger.initPeakWriter(false);
        logger.writePeakIntro();
        logger.initPeakWriter(true);
        for (int i = 0; i < timeList.size(); i++) {
            double frequency1 = frequencyList.get(i);
            int time1 = timeList.get(i).intValue();
            logHashGenerator.setBin1(binList.get(i).intValue());
            logHashGenerator.setTime1(timeList.get(i).intValue());
            for (int j = 1; j < 10; j++) {
                if (i + j >= timeList.size()) break;
                double frequency2 = frequencyList.get(i + j);
                if (Math.abs(frequency1 - frequency2) < 1250) { //target zone
                    logHashGenerator.setBin2(binList.get(i + j).intValue());
                    logHashGenerator.setTime2(timeList.get(i + j).intValue());
                    PeakPairTime peakPair = logHashGenerator.createHash();
                    logger.writePeakLine(logHashGenerator);
                    //inserimento nella tabella
                    fingerMap.put(peakPair.hash, peakPair.anchorAndId);
                }
            }
        }
        return fingerMap;
    }

    public ListMultimap<String, TimePair> matchFingerprints(List<Double> timeList, List<Double> binList, List<Double> frequencyList) {
        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        PeakLogger logger = new PeakLogger(dirInfo.getLogDirPath(), dirInfo.getPeakDirPath(), dirInfo.getrDirPath());
        LogHashGenerator logHashGenerator = new LogHashGenerator(track.getName());
        logger.setLogPath(dirInfo.getLogDirPath() + track.getName() + ".log");
        logger.setPeakPath(dirInfo.getPeakDirPath() + track.getName() + "_peaks.log");
        logger.setrPath(dirInfo.getrDirPath() + track.getName() + "_histogram.log");
        logger.initPeakWriter(false);
        logger.writePeakIntro();
        logger.initLogWriter(true);
        for (int i = 0; i < timeList.size(); i++) {
            double frequency1 = frequencyList.get(i);
            int time1 = timeList.get(i).intValue();
            logHashGenerator.setBin1(binList.get(i).intValue());
            logHashGenerator.setTime1(timeList.get(i).intValue());
            for (int j = 1; j < 10; j++) { //todo?: sostituire con un while per aumentare i solo se i due punti sono nella target zone
                if (i + j >= timeList.size()) break;
                double frequency2 = frequencyList.get(i + j);
                if (Math.abs(frequency1 - frequency2) < 1250) { //target zone
                    logHashGenerator.setBin2(binList.get(i + j).intValue());
                    logHashGenerator.setTime2(timeList.get(i + j).intValue());
                    PeakPairTime peakPair = logHashGenerator.createHash();
                    logger.writePeakLine(logHashGenerator);
                    for (BitSet anchorAndId : fingerMap.get(peakPair.hash)) {
                        boolean[] anchorAndIdBool = HashUtils.bitSetToBool(anchorAndId, false);
                        int id = HashUtils.extractId(anchorAndId, false);
                        int anchorTime = HashUtils.extractAnchorTime(anchorAndId, false);
                        TimePair timePair = new TimePair();
                        timePair.setTime1(time1);
                        timePair.setTime2(anchorTime);
                        SongMap songMap = SongMap.getInstance();
                        String matchTrack = songMap.getTrackName(id);
                        matchMap.put(matchTrack, timePair);
                    }
                }
            }
        }
        return matchMap;
    }

    public int trackScore() {
        int[] matchArr = new int[1000000]; //todo
        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        PeakLogger logger = new PeakLogger(dirInfo.getLogDirPath(), dirInfo.getPeakDirPath(), dirInfo.getrDirPath());
        logger.setRPath(dirInfo.getrDirPath()+track.getName()+"_histogram.log");
        logger.initRWriter(false);
        logger.writeRIntro();
        logger.initRWriter(true);
        for (TimePair timePair : matchMap.get(track.getName())) {
            int timeDifference = timePair.getTime2() - timePair.getTime1();
            if (timeDifference >= 0) {
                matchArr[timeDifference]++;
                logger.writeHistogramLine(timeDifference);
            }
        }
        logger.closeRWriter();
        int max = 0;
        int maxIndex = 0;
        for(int i = 0; i < matchArr.length; i++) {
            if (matchArr[i] > max) {
                max = matchArr[i];
                maxIndex = i;
            }
        }
        System.out.println(track.getName()+", score: "+max+" location: "+maxIndex);
        return max;
    }
}