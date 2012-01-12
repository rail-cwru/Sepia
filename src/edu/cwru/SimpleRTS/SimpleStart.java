package edu.cwru.SimpleRTS;

import edu.cwru.SimpleRTS.start.StartWindow;
import edu.cwru.SimpleRTS.start.StartListener;

public class SimpleStart {

    public static void main(String[] args) {

        StartWindow startWindow = new StartWindow();
        startWindow.addStartListener(new StartListener() {
            @Override
            public void start(String[] args) {
                new SimpleStartThread(args).start();
            }
        });

        startWindow.pack();
        startWindow.setVisible(true);

    }

    public static class SimpleStartThread extends Thread {

        private final String[] args;

        public SimpleStartThread(String[] args) {
            this.args = args;
        }

        @Override
        public void run() {
            try {
                Main.main(args);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

}
