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
import java.util.Collections;
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
    PrintWriter logWriter;
    PrintWriter peakWriter;
    PrintWriter rWriter;

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

    public void initLogWriter(boolean append) {
        try {
            this.logWriter = new PrintWriter(new BufferedWriter(new FileWriter(logPath, append)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initPeakWriter(boolean append) {
        try {
            this.peakWriter = new PrintWriter(new BufferedWriter(new FileWriter(peakPath, append)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initRWriter(boolean append) {
        try {
            this.rWriter = new PrintWriter(new BufferedWriter(new FileWriter(rPath, append)));
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void setRPath(String rPath) {
        this.rPath = rPath;
    }

    public void writeIntro() {
        try {
            String s = "Event Bin Frequency Volume";
            logWriter.println(s);
        } finally {
            try { logWriter.close(); } catch (Exception ex) {}
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
        ArrayList<SpectralPeakProcessor.SpectralPeak> peakList = new ArrayList<SpectralPeakProcessor.SpectralPeak>(peakMap.values());
        Collections.sort(peakList, new PeakTimeComparer());
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(logPath, true)));
            for (SpectralPeakProcessor.SpectralPeak peak : peakList) {
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
        String s = "Time1 Bin1 Time2 Bin2 Hash";
        peakWriter.println(s);
        try { peakWriter.close(); } catch (Exception ex) {}
    }

    public void writePeakLine(LogHashGenerator hashGenerator) {
        String s = hashGenerator.getTime1()+ " " + hashGenerator.getBin1() + " " + hashGenerator.getTime2() + " " + hashGenerator.getBin2() + " " + hashGenerator.getPeakPair().toString();
        peakWriter.println(s);
    }

//    public void writeHistogramLog(int[] timeArr) {
//        PrintWriter writer = null;
//        try {
//            writer = new PrintWriter(new BufferedWriter(new FileWriter(rPath, false)));
//            String s = "";
//            for (int i = 0; i < timeArr.length; i++) {
//                s = timeArr[i] + "";
//                if (timeArr[i] > 0)
//                    writer.println(s);
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            try { writer.close(); } catch (Exception ex) {}
//        }
//    }

    public void writeRIntro() {
        rWriter.println("timeDifference");
        try { rWriter.close(); } catch (Exception ex) {}
    }

    public void writeHistogramLine(int timeDifference) {
        rWriter.println(timeDifference);
    }

    public void closeRLog() {
        try {
            this.rWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
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
            if (frequency < 10000) {
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
