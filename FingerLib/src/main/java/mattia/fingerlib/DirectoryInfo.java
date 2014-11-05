package mattia.fingerlib;

/**
 * Created by mattia cerrato on 20/10/14.
 */
public class DirectoryInfo {
    private final String audioDirPath = "/Users/tesi/Desktop/audio/";
    private final String logDirPath = "/Users/tesi/Desktop/FingerLibrary/logs/";
    private final String rDirPath = "/Users/tesi/Desktop/FingerLibrary/rlogs/";
    private final String imgDirPath = "/Users/tesi/Desktop/FingerLibrary/img/";
    private final String peakDirPath = "/Users/tesi/Desktop/FingerLibrary/peaks/";
    private final String otaDirPath = "/Users/tesi/Desktop/ota/";
    private static DirectoryInfo instance;

    private DirectoryInfo() {}

    public static DirectoryInfo getInstance() {
        if (instance == null) {
            instance = new DirectoryInfo();
        }
        return instance;
    }

    public String getAudioDirPath() {
        return audioDirPath;
    }

    public String getLogDirPath() {
        return logDirPath;
    }

    public String getrDirPath() {
        return rDirPath;
    }

    public String getImgDirPath() {
        return imgDirPath;
    }

    public String getPeakDirPath() {
        return peakDirPath;
    }

    public String getOtaDirPath() {
        return otaDirPath;
    }
}
