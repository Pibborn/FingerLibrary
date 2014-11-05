package mattia.fingerlib;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.File;

/**
 * === PER ORA INUTILIZZATA ====
 * le funzionalità sono state spostate in FingerprintGenerator
 * classe che si occupa di creare la tabella hash con cui si verificano i match e trovare la traccia con cui è più probabile il match
 * Created by mattia cerrato on 22/10/14.
 */
public class MatchGenerator {
    public Multimap<String, TimePair> matchMap;

    public MatchGenerator() {
        this.matchMap = ArrayListMultimap.create();
    }

    public void match() {
        DirectoryInfo dirInfo = DirectoryInfo.getInstance();
        File ota = new File(dirInfo.getOtaDirPath()+"ota_track.mp3");
    }
}
