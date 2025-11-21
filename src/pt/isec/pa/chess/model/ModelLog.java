package pt.isec.pa.chess.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * The ModelLog class provides a singleton logging mechanism for the chess game,
 * with support for property change notifications to observers.
 *
 * <p>Features include:
 * <ul>
 *   <li>Singleton instance access</li>
 *   <li>Log message storage</li>
 *   <li>Duplicate message prevention</li>
 *   <li>Observer notification</li>
 * </ul>
 */

public class ModelLog {
    /**
     * The singleton instance of ModelLog.
     */
    private static ModelLog modelLog;
    /**
     * A list to store log messages.
     */
    private final List<String> logs = new ArrayList<>();

    /**
     * PropertyChangeSupport instance to manage property change listeners.
     */
    private final PropertyChangeSupport pcs;

    /**
     * The last log message to prevent duplicates.
     */
    private String lastLog = null;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private ModelLog() {
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Returns the singleton instance of ModelLog.
     *
     * @return the singleton instance of ModelLog
     */
    public static synchronized ModelLog getInstance() {
        if (modelLog == null) {
            modelLog = new ModelLog();
        }
        return modelLog;
    }

    /**
     * Adds a PropertyChangeListener to the ModelLog.
     *
     * @param listener the PropertyChangeListener to add
     */
    public void addPCListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Removes a PropertyChangeListener from the ModelLog.
     *
     * @param listener the PropertyChangeListener to remove
     */
    public void removePCListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Adds a log message to the ModelLog.
     * If the log message is the same as the last one, it will not be added again.
     *
     * @param log the log message to add
     */
    public void addLog(String log) {
        if (log.equals(lastLog)) {
            return;
        }
        lastLog = log;

        logs.add(log);
        pcs.firePropertyChange("log", null, log);
    }

    /**
     * Returns a copy of the list of log messages.
     *
     * @return a list of log messages
     */
    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    /**
     * Clears the log messages and notifies observers.
     */
    public void clearLogs() {
        logs.clear();
        pcs.firePropertyChange("LimparLogs", null, null);
    }

}
