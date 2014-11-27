package mattia.fingerlib;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by mattia cerrato on 29/09/14.
 */
public class FingerTest {

    private List<Double> timeList;

    public static void main(String[] args) {

        // set to TRUE if all TestCases refer to the same set of audio files
        boolean sameAudioSet = true;

        TestLogParser testLogParser = new TestLogParser();
        List<TestCase> testList = null;
        try {
            testList = testLogParser.generateTestList(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PrintWriter testResultWriter = null;

        //il nome del file-risultato di log è la descrizione del primo file di test
        String resultFilePath = testList.get(0).getOutPath() + testList.get(0).getDescription()+".log";
        System.out.println("logpath:" +resultFilePath);
        try {
            testResultWriter = new PrintWriter(new BufferedWriter(new FileWriter(resultFilePath, true)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        boolean first = true;

        FingerprintGenerator finGen = null;

        if(sameAudioSet) {
            finGen = new FingerprintGenerator();
        }

        int db_size = 0;
        for(TestCase test : testList) {
            dirInfo.setAudioDirPath(test.getAudioPath());
            dirInfo.setOtaDirPath(test.getOtaPath());

//            generazione plotter (non lo uso più)
//            PlotImageGenerator plotter = new PlotImageGenerator(dirInfo.getImgDirPath(), dirInfo.getAudioDirPath(), dirInfo.getLogDirPath());
//            plotter.setAudioPath(dirInfo.getAudioDirPath());
//            plotter.setLogPath(dirInfo.getLogDirPath());

            PeakLogger logger = new PeakLogger(dirInfo.getLogDirPath(), dirInfo.getPeakDirPath(), dirInfo.getrDirPath());

            File audioDirFile = new File(dirInfo.getAudioDirPath());
            File[] audioDir = audioDirFile.listFiles();

            File otaDirFile = new File(dirInfo.getOtaDirPath());
            File[] otaDir = otaDirFile.listFiles();


            List<Double> dbMatchList = new ArrayList<Double>();
            List<Double> otaMatchList = new ArrayList<Double>();

            ListMultimap<String, TimePair> timeMatchMap = ArrayListMultimap.create();
            ListMultimap<Integer, Integer> peakMap = ArrayListMultimap.create();

            String match = "";

            //generazione songmap
            SongMap songMap = SongMap.getInstance();
            songMap.generateArr();

            if (!sameAudioSet) {
                finGen = new FingerprintGenerator();
            }

            String expectedMatch = test.getExpectedMatch();
            String expectedMatchNoFileFormat = expectedMatch.replace(".mp3", "");
            expectedMatchNoFileFormat = expectedMatchNoFileFormat.replace(".wav", "");
            boolean hasAMatch = false;

            //generazione fingerprint delle tracce-db
             db_size = 0;
             for (File track : audioDir) {
                if (track.getName().compareTo(".DS_Store") == 0) continue;
                if (track.getName().contains(".sh")) continue;
                String trackNameNoFileFormat = track.getName().replace(".wav", "");
                trackNameNoFileFormat = trackNameNoFileFormat.replace(".mp3", "");
//                System.out.println("expectednoformat:"+expectedMatchNoFileFormat);
//                System.out.println("tracknamenoformat:"+trackNameNoFileFormat);

                if (expectedMatchNoFileFormat.compareTo(trackNameNoFileFormat) == 0) {
                    hasAMatch = true; //allora un match andrebbe trovato
                }
                finGen.setTrack(track);
                if (first || !sameAudioSet) {
                    peakMap = finGen.generateFingerprints(true, false);
                }
                db_size++;
            }
            first = false;

            //generazione fingerprint delle tracce-ota (solitamente una!)
            for (File ota : otaDir) {
                if (ota.getName().compareTo(".DS_Store") == 0) continue;
                finGen.setTrack(ota);
                timeMatchMap = finGen.generateFingerprints(true, true);
            }
            //generazione score delle tracce-db rispetto all'ota
            int[] scores = new int[db_size];
            String[] trackNames = new String[db_size];
            boolean[] histoPeak = new boolean[db_size];
            int max = 0;
            int i = 0;
            for (File track : audioDir) {
                if (track.getName().compareTo(".DS_Store") == 0) continue;
                if (track.getName().contains(".sh")) continue;
                trackNames[i] = track.getName();
                finGen.setTrack(track);
                int score = finGen.trackScore();
                scores[i] = score;
                boolean hasPeak = finGen.findHistogramPeak(true);
                histoPeak[i] = hasPeak;
                if (score > max) {
                    max = score;
                    match = track.getName();
                }
                i++;
            }
//            double[] sorted_scores = new double[db_size];
//            for (int j = 0; j < db_size; j++) {
//                sorted_scores[j] = (double) scores[j];
//            }
//            Arrays.sort(sorted_scores);
//            double next_to_max = sorted_scores[db_size-2];
//            double difference = max - next_to_max;
//            difference *= difference;
//            System.out.println("quadrato della differenza: "+difference);
//            Percentile percentileCalculator = new Percentile();
//            double filter_value = percentileCalculator.evaluate(sorted_scores, 80);
//            System.out.println("80mo percentile dei punteggi: "+filter_value);
//        int times = 0;
//        for (int j = 0; j < db_size; j++) {
//            if (scores[j] > filter_value) {
//                times++;
//            }
//        }
//        if (difference < max) {
            for (int j = 0; j < db_size; j++) {
                if (histoPeak[j]) {
                    match = trackNames[j]; //todo: cosa succede se ho percepito picchi su più istogrammi?
                    if (scores[j] >= max) {
                        break;
                    }
                }
                match = "no match!";
            }
//        }
            String matchNoFileFormat = match.replace(".wav", "");
            matchNoFileFormat = matchNoFileFormat.replace(".mp3", "");

            System.out.println("previsione di match: "+ matchNoFileFormat);
            File outTestFile = new File(test.getOutPath()+test.getTestName());
            PrintWriter writer = null;
            if (hasAMatch && (matchNoFileFormat.compareTo(expectedMatchNoFileFormat) == 0)) {
                System.out.println(test.getTestName() + ": success (found the right match)");
                testResultWriter.println(test.getTestName()+": success (found the right match)");
            }
            else if(!hasAMatch && (matchNoFileFormat.compareTo("no match!") == 0)) {
                System.out.println(test.getTestName() + ": success (no match)");
                testResultWriter.println(test.getTestName()+": success (no match)");
            }
            else if(!hasAMatch && (matchNoFileFormat.compareTo("no match!") != 0)) {
                System.out.println(test.getTestName() + ": failure (false positive)");
                testResultWriter.println(test.getTestName()+": failure (false positive)");
            }
            else if(hasAMatch && (matchNoFileFormat.compareTo(expectedMatchNoFileFormat) != 0)) {
                System.out.println(test.getTestName() + ": failure (matched the wrong track)");
                testResultWriter.print(test.getTestName()+": failure (matched the wrong track)");
                if (match.compareTo("no match") == 0) {
                    testResultWriter.println(test.getTestName()+": (found no match but there was)\n ");
                } else {
                    testResultWriter.println();
                }
            }

            if (sameAudioSet) finGen.resetMatchMap();
        }
        testResultWriter.close();
    }
}