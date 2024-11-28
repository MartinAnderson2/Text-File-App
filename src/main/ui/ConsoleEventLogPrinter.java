package ui;

import model.Event;
import model.EventLog;

// Represents a console printer for printing all of the events in the EventLog to the console
public class ConsoleEventLogPrinter {
    // EFFECTS: prints out each event in the EventLog to the console with space between each one
    public static void printEvents() {
        for (Event event : EventLog.getInstance()) {
            System.out.println(event.toString());
            System.out.println();
        }
    }
}
