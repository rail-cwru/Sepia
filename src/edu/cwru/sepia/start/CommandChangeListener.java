package edu.cwru.sepia.start;

/**
 * Listens for when an event occurs that
 * might change the command being written.
 */
public interface CommandChangeListener {
    public void commandChanged();
}
