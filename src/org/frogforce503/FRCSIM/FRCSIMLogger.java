/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.frogforce503.FRCSIM;

import java.io.IOException;
import java.util.Formatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Bryce
 */
public class FRCSIMLogger {
  static private FileHandler fileTxt;

  static public void setup() throws IOException {
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.INFO);
        fileTxt = new FileHandler("FRCSIMLog.fsd");
        fileTxt.setFormatter(new SimpleFormatter());
        fileTxt.setLevel(Level.INFO);
        logger.addHandler(fileTxt);
    }

    private FRCSIMLogger() {
    }
}
