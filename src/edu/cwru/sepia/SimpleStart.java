package edu.cwru.sepia;

import edu.cwru.sepia.start.StartListener;
import edu.cwru.sepia.start.StartWindow;

/**
 * An entry point to Sepia that allows for visual configuration of runtime parameters.
 * @author Gary
 *
 */
public final class SimpleStart {

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

    private SimpleStart() {}
}
