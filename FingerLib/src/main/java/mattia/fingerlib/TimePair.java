package mattia.fingerlib;

/**
 * classe che astrae il concetto di "punto tempo-tempo" dove un tempo è il tempo passato dall'inizio della traccia OTA
 * e l'altro è il tempo passato dall'inizio della traccia presente nel DB.
 * Created by mattia cerrato on 16/10/14.
 */
public class TimePair {
    public int time1;
    public int time2;

    public TimePair() {}

    public int getTime1() {
        return time1;
    }

    public void setTime1(int time1) {
        this.time1 = time1;
    }

    public int getTime2() {
        return time2;
    }

    public void setTime2(int time2) {
        this.time2 = time2;
    }
}
