package mattia.fingerlib;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.max;
import static java.util.Collections.sort;
/* todo: cambiare i riferimenti alle cartelle e i costruttori utilizzando DirectoryInfo
   todo: non l'ho cambiata in PeakLogger perchè quello cambia dinamicamente prendendo proprio i nomi dei file. va bene?
 */
/**
 * Created by mattia cerrato on 29/09/14.
 */
public class FingerTest {

    private List<Double> timeList;

    public static void main(String[] args) {


        DirectoryInfo dirInfo = DirectoryInfo.getInstance();

        PlotImageGenerator plotter = new PlotImageGenerator(dirInfo.getImgDirPath(), dirInfo.getAudioDirPath(), dirInfo.getLogDirPath());
        plotter.setAudioPath(dirInfo.getAudioDirPath());
        plotter.setLogPath(dirInfo.getLogDirPath());

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

        FingerprintGenerator finGen = new FingerprintGenerator();

        int db_size = 0;
        //generazione fingerprint delle tracce-db
        for (File track : audioDir) {
            if (track.getName().compareTo(".DS_Store") == 0) continue;
            finGen.setTrack(track);
            peakMap = finGen.generateFingerprints(true, false);
            db_size++;
        }
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
            trackNames[i] = track.getName();
            finGen.setTrack(track);
            int score = finGen.trackScore();
            scores[i] = score;
            boolean hasPeak = finGen.findHistogramPeak();
            histoPeak[i] = hasPeak;
            if (score > max) {
                max = score;
                match = track.getName();
            }
            i++;
        }
        double[] sorted_scores = new double[db_size];
        for (int j = 0; j < db_size; j++) {
            sorted_scores[j] = (double) scores[j];
        }
        Arrays.sort(sorted_scores);
        double next_to_max = sorted_scores[db_size-2];
        double difference = max - next_to_max;
        difference *= difference;
        System.out.println("quadrato della differenza: "+difference);
        Percentile percentileCalculator = new Percentile();
        double filter_value = percentileCalculator.evaluate(sorted_scores, 80);
        System.out.println("\n75mo percentile dei punteggi: "+filter_value);
//        int times = 0;
//        for (int j = 0; j < db_size; j++) {
//            if (scores[j] > filter_value) {
//                times++;
//            }
//        }
        if (difference < max) {
            for (int j = 0; j < db_size; j++) {
                if (histoPeak[j]) {
                    match = trackNames[j]; //todo: cosa succede se ho percepito picchi su più istogrammi?
                    return;
                }
            }
            match = "no match!";
        }
        System.out.println("previsione di match: "+ match);

    }
}