package mattia.fingerlib;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * classe che si occupa di mantenere una associazione tra [10 bit binari] - [nomefile della traccia]
 * è una classe perchè il costruttore si occuperà di mantenere le associazioni con il db, quando lo inserirò
 * Created by mattia cerrato on 17/10/14.
 */
public class SongMap extends HashMap{
    public static HashMap<Integer, String> songMap;
    private static SongMap instance = null;
    private static String[] songArr;

    private SongMap() {

    }

    public String[] generateArr() {
        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        File audioDirFile = new File(dirInfo.getAudioDirPath());
        File[] audioDir = audioDirFile.listFiles();
        songArr = new String[audioDir.length];
        int i = 0;
        for (File track : audioDir) {
            if (track.getName().compareTo(".DS_Store") == 0) continue;
            songArr[i] = track.getName();
            i++;
        }
        return songArr;
    }

    public int findTrackId(String trackName) {
        return Arrays.asList(songArr).indexOf(trackName);
    }

    public String getTrackName(int id) {
        return songArr[id];
    }

    public HashMap generate() {
        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        File audioDirFile = new File(dirInfo.getAudioDirPath());
        File[] audioDir = audioDirFile.listFiles();
        int i = 0;
        for (File track : audioDir) {
            if (track.getName().compareTo(".DS_Store") == 0) continue;
            songMap.put(i, track.getName());
            i++;
        }
        return songMap;
    }

    public String toString() {
        String out = "";
        for(Map.Entry<Integer, String> entry : songMap.entrySet()) {
            out += entry.getKey() + " " + entry.getValue() + "\n";
        }
        return out;
    }

    public static SongMap getInstance() {
        if (instance == null) {
            instance = new SongMap();
        }
        return instance;
    }

    public String get(int id) {
        return songMap.get(id);
    }
}
