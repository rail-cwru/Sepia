package edu.cwru.SimpleRTS;

import javax.swing.JFrame;

import edu.cwru.SimpleRTS.start.StartWindow;

public class SimpleStart {

    public static void main(String[] args) {

        StartWindow startWindow = new StartWindow();
        startWindow.pack();
        startWindow.setVisible(true);

    }
}
