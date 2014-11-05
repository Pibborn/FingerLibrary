package mattia.fingerlib;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.io.File;
import java.util.ArrayList;
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

        //generazione fingerprint delle tracce-db
        for (File track : audioDir) {
            if (track.getName().compareTo(".DS_Store") == 0) continue;
            finGen.setTrack(track);
            peakMap = finGen.generateFingerprints(true, false);
            return;
        }
        //generazione fingerprint delle tracce-ota (solitamente una!)
        for (File ota : otaDir) {
            if (ota.getName().compareTo(".DS_Store") == 0) continue;
            finGen.setTrack(ota);
            timeMatchMap = finGen.generateFingerprints(true, true);
        }
        //generazione score delle tracce-db rispetto all'ota
        int max = 0;
        for (File track : audioDir) {
            if (track.getName().compareTo(".DS_Store") == 0) continue;
            finGen.setTrack(track);
            int score = finGen.trackScore();
            if (score > max) {
                max = score;
                match = track.getName();
            }
        }
        System.out.println("previsione di match: "+ match);

//        int[] timeArr = new int[1000000];
//        for (File track : audioDir) {
//            for (TimePair timePair : timeMatchMap.get(track.getName())) {
//                if (track.getName().compareTo("ota_punk_3.wav") != 0) break; //voglio matchare ota_rec
//                int timeDifference = timePair.getTime2() - timePair.getTime1();
//                if (timeDifference > 0) {
//                    timeArr[timeDifference]++;
//                }
//            }
//        }
//        int max = 0;
//        int occ = 0;
//        for (int i = 0; i < timeArr.length; i++) {
//            if (timeArr[i] > occ) {
//                max = i;
//                occ = timeArr[i];
//            }
//        }
//        System.out.println("max time: "+ max +" occurrences: "+occ);

//        for (File track : audioDir) {
//            int i = 0;
//            for (TimePair timePair : timeMatchMap.get(track.getName())) {
//                if (track.getName().compareTo("ota_punk_3.wav") != 0) break; //voglio matchare ota_rec
//                int timeDifference = timePair.getTime2() - timePair.getTime1();
//                if (timeDifference > 0) {
//                    timeArr[i] = timeDifference;
//                }
//                i++;
//            }
//        }
//        logger.writeHistogramLog(timeArr);

        //prova histogram
//        System.out.println(times);
//        System.out.println(dbMatchList.size());
//        System.out.println(peakMap.keySet().size());
//        //grafico temporale dei match
//        plotter.setxAxisList(dbMatchList);
//        plotter.setyAxisList(otaMatchList);
//        plotter.setImgPath("/Users/tesi/Desktop/FingerLibrary/matchplot/");
//        plotter.generateScatterplot();

//        int i = 0;
//        List<Integer> matchList = new ArrayList<Integer>();
//        for (Integer key : peakMap.keySet()) {
//            System.out.print("hash "+key+": ");
//            for (Integer hash : peakMap.get(key)) {
//                System.out.print(hash +" ");
//            }
//            System.out.println("");
//        }
    }
}