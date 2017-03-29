/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.davidg95.jtill.javafxjtill;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Class which updates labels with the current time.
 *
 * @author David
 */
public class ClockThread extends Thread {

    /**
     * The ClockThread.
     */
    private static final ClockThread CLOCK_THREAD;

    protected boolean isRunning;

    protected List<Label> dataTimeLabels; //The labels on the list.

    protected SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    protected SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

    protected Time time; //The time.

    protected int format; //The chosen format.

    private boolean flash;

    /**
     * Value = 1.
     *
     * Formats the labels with only the time.
     */
    public static final int TIME_FORMAT = 1;

    /**
     * Value = 2.
     *
     * Formats the labels with only the date.
     */
    public static final int DATE_FORMAT = 2;

    /**
     * Value = 3.
     *
     * Formats the labels with the time and the data in the format HH:MM
     * DD/MM/YYYY.
     */
    public static final int DATE_TIME_FORMAT = 3;

    public ClockThread() {
        this.isRunning = true;
        this.dataTimeLabels = new ArrayList<>();
        format = 1;
    }

    static {
        CLOCK_THREAD = new ClockThread();
        CLOCK_THREAD.start();
    }

    /**
     * Method to add a JavaFX Label to the list of labels to update the time to.
     * A call is made to Platform.runLater whenever the time is updated.
     *
     * @param label the JavaFX label to update with the time.
     */
    public static void addClockLabel(Label label) {
        CLOCK_THREAD.addLabel(label);
    }

    /**
     * Returns the current time.
     *
     * @return the current Time.
     */
    public static Time getTime() {
        return CLOCK_THREAD.getCurrentTime();
    }

    /**
     * Set the format to display the time in. Options- TIME_FORMAT - Displays
     * only the time. DATE_FORMAT - Displays only the date. DATE_TIME_FORMAT -
     * Displays the data and the time.
     *
     * @param format the format to display.
     */
    public static void setFormat(int format) {
        CLOCK_THREAD.setClockFormat(format);
    }

    @Override
    public void run() {
        while (isRunning) {
            Calendar currentCalendar = Calendar.getInstance();
            Date currentTime = currentCalendar.getTime();
            time = new Time(currentCalendar.getTimeInMillis());
            for (Label l : dataTimeLabels) {
                Platform.runLater(() -> {
                    String t;
                    switch (format) {
                        case 1: //If time format was selected.
                            t = timeFormat.format(currentTime);
//                            if (flash) {
//                                t = t.replace(":", " ");
//                            }
//                            flash = !flash;
                            l.setText(t);
                            break;
                        case 2: //If date format was selected.
                            l.setText(dateFormat.format(currentTime));
                            break;
                        default: //If date and time format was selected.
                            t = dateTimeFormat.format(currentTime);
//                            if (flash) {
//                                t = t.replace(":", " ");
//                            }
//                            flash = !flash;
                            l.setText(t);
                            break;
                    }
                });
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
            }
        }
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void addLabel(Label label) {
        dataTimeLabels.add(label);
    }

    public Time getCurrentTime() {
        return time;
    }

    public void setClockFormat(int format) {
        this.format = format;
    }

}
