package edu.cwru.SimpleRTS.util;

import javax.swing.SwingUtilities;

public class SwingUtils {

    /**
     * Invokes {@code run} immediately if this is the
     * EDT; otherwise, the {@code Runnable} is invoked
     * on the EDT using {@code invokeLater}.
     */
    public static void invokeNowOrLater(Runnable run) {
        if (SwingUtilities.isEventDispatchThread()) {
            run.run();
        } else {
            SwingUtilities.invokeLater(run);
        }
    }

}
