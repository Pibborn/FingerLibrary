package mattia.fingerlib;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import org.math.plot.Plot2DPanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Created by mattia cerrato on 08/10/14.
 */
public class PlotImageGenerator {
    String imgPath;
    String trackPath;
    String logDirPath;
    List<Double> xAxisList;
    List<Double> yAxisList;

    public PlotImageGenerator(String imgPath, String audioPath, String logPath) {
        this.imgPath = imgPath;
        this.trackPath = audioPath;
        this.logDirPath = logPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public String getAudioPath() {
        return trackPath;
    }

    public String getLogPath() {
        return logDirPath;
    }

    public void setAudioPath(String audioPath) {
        this.trackPath = audioPath;
    }

    public void setLogPath(String logPath) {
        this.logDirPath = logPath;
    }

    public void setxAxisList(List<Double> xAxisList) {
        this.xAxisList = xAxisList;
    }

    public void setyAxisList(List<Double> yAxisList) {
        this.yAxisList = yAxisList;
    }

    public void generateScatterplot() {
        String fileName = trackPath.replace(".mp3", "").replace(".wav", "");
        File file = new File(logDirPath+fileName+".log");

        double[] timeArr = new double[xAxisList.size()];
        double[] binArr = new double[yAxisList.size()];
        for (int i = 0; i < xAxisList.size(); i++) {
            timeArr[i] = xAxisList.get(i);
            binArr[i] = yAxisList.get(i);
        }

        //grafico
        Plot2DPanel plot = new Plot2DPanel();
        plot.setAxisLabels("tempo (audio-eventi)", "frequenza");
        plot.setSize(1600,1050);
        plot.addScatterPlot("test", timeArr, binArr);
        JFrame frame = new JFrame("test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600,1050);
        frame.setBackground(Color.WHITE);
        frame.setUndecorated(true);
        frame.getContentPane().add(plot);
        frame.setVisible(true);

        //render immagine
        BufferedImage outImage = new BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = outImage.createGraphics();
        plot.paint(graphics);
        File outFile = new File(imgPath);
        try {
            ImageIO.write(outImage, "jpg", outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphics.dispose();
        frame.dispose();
    }

    public void generateHistogram(int[] timeArr) {
        String fileName = trackPath.replace(".mp3", "").replace(".wav", "");
        File file = new File(logDirPath+fileName+".log");
        double[] doubleTimeArr = Doubles.toArray(Ints.asList(timeArr));
        Plot2DPanel plot = new Plot2DPanel();
        plot.setAxisLabels("time difference", "occorrenze");
        plot.setSize(1600,1050);
        plot.addHistogramPlot("histogram", doubleTimeArr, 100);
        JFrame frame = new JFrame("test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1600,1050);
        frame.setBackground(Color.WHITE);
        frame.setUndecorated(true);
        frame.getContentPane().add(plot);
        frame.setVisible(true);

        //render immagine
        BufferedImage outImage = new BufferedImage(plot.getWidth(), plot.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = outImage.createGraphics();
        plot.paint(graphics);
        File outFile = new File(imgPath+"_histogram");
        try {
            ImageIO.write(outImage, "jpg", outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphics.dispose();
        frame.dispose();
    }
}
