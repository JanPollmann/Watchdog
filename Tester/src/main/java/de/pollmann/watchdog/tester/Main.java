package de.pollmann.watchdog.tester;

import de.pollmann.watchdog.tester.app.AppContext;
import de.pollmann.watchdog.tester.app.FastLoopApp;
import de.pollmann.watchdog.tester.app.RunnableTesterApp;

public class Main {

    public  static void main(String[] args) {
        AppContext appContext = new AppContext();
        FastLoopApp app = new RunnableTesterApp(appContext);
        app.start();
    }

}
