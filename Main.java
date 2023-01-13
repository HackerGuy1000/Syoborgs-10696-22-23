package org.example;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private JLabel cameraScreen;
    private VideoCapture capture;
    private Mat image;
    private Mat newImage;
    public Main() {
        cameraScreen = new JLabel();
        cameraScreen.setBounds(0, 0, 1280, 720);
        add(cameraScreen);

        setSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void startCamera() {
        capture = new VideoCapture(0);
        image = new Mat();
        byte[] imgData;
        ImageIcon icon;
        while(true) {
            capture.read(image);

            OpenCvPipeline pipeline = new RGBPipeline();
            newImage = pipeline.processFrame(image);

            MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", newImage, buf);

            imgData = buf.toArray();
            icon = new ImageIcon(imgData);
            cameraScreen.setIcon(icon);
        }
    }



    public static void main(String[] args) {
        OpenCV.loadLocally();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Main main = new Main();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        main.startCamera();
                    }
                }).start();
            }
        });
    }
}
