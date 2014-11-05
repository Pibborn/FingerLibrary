package mattia.fingerlib;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import be.tarsos.dsp.SpectralPeakProcessor;

/**
 * Created by mattia cerrato on 09/10/14.
 */
public class PeakLogger {
    String logPath;
    String peakPath;
    String rPath;

    public String getrPath() {
        return rPath;
    }

    public void setrPath(String rPath) {
        this.rPath = rPath;
    }

    public PeakLogger(String logPath, String peakPath, String rPath) {
        this.logPath = logPath;
        this.peakPath = peakPath;
        this.rPath = rPath;
    }

    public String getLogPath() {
        return this.logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setPeakPath(String peakPath) {
        this.peakPath = peakPath;
    }

    public void writeIntro() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(logPath, false)));
            String s = "Event Bin Frequency Volume";
            writer.println(s);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try { writer.close(); } catch (Exception ex) {}
        }
    }

    public void writeLogLine(List<SpectralPeakProcessor.SpectralPeak> peakList, int lineNumber) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(logPath, true)));
            for (SpectralPeakProcessor.SpectralPeak sp : peakList) {
                String s = lineNumber + " " + sp.getBin() + " " + sp.getFrequencyInHertz() + " " + sp.getMagnitude();
                writer.println(s);
                sp.setTimeStamp(lineNumber); //todo: qui non serve a niente ma l'idea non è sbagliata
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
        try { writer.close(); } catch (Exception ex) {}
    }
    }

    public void writeMapLog(ArrayListMultimap<Integer, SpectralPeakProcessor.SpectralPeak> peakMap) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(logPath, true)));
            for (SpectralPeakProcessor.SpectralPeak peak : peakMap.values()) {
                String s = peak.getTimeStamp() + " " + peak.getBin() + " " + peak.getFrequencyInHertz() + " " + peak.getMagnitude();
                writer.println(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try { writer.close(); } catch (Exception ex) {}
        }
    }

    public void writePeakIntro() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(peakPath, false)));
            String s = "Time1 Bin1 Time2 Bin2 Hash";
            writer.println(s);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try { writer.close(); } catch (Exception ex) {}
        }
    }

    public void writePeakLine(LogHashGenerator hashGenerator) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(peakPath, true)));
            String s = hashGenerator.getTime1()+ " " + hashGenerator.getBin1() + " " + hashGenerator.getTime2() + " " + hashGenerator.getBin2() + " " + hashGenerator.getPeakPair().toString();
            writer.println(s);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try { writer.close(); } catch (Exception ex) {}
        }
    }

    public void writeHistogramLog(int [] timeArr) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(rPath, false)));
            String s = "";
            for (int i = 0; i < timeArr.length; i++) {
                s = timeArr[i] + "";
                if (timeArr[i] > 0)
                    writer.println(s);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try { writer.close(); } catch (Exception ex) {}
        }
    }

    public List<Double>[] parseLogFile(String filePath) {
        Scanner input = null;
        try {
            input = new Scanner(new File(logPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<Double> timeList = new ArrayList<Double>();
        List<Double> binList = new ArrayList<Double>();
        List<Double> volumeList = new ArrayList<Double>();
        List<Double> frequencyList = new ArrayList<Double>();

        input.nextLine(); //la prima riga è l'intestazione
        while (input.hasNext()) {
            double time = Double.parseDouble(input.next());
            double bin = Double.parseDouble(input.next());
            double frequency = Double.parseDouble(input.next());
            double volume = Double.parseDouble(input.next());
            if (frequency < 10000 && frequency > 400) {
                timeList.add(time);
                binList.add(bin);
                frequencyList.add(frequency);
                volumeList.add(volume);
            }
        }
        List<Double>[] arr = new List[4];
        arr[0] = timeList;
        arr[1] = binList;
        arr[2] = frequencyList;
        arr[3] = volumeList;
        return arr;
    }


}
