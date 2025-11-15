package se.liu.alfsj019.main;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The class that creates the file handler for the logger in each class.
 * The most important class regarding the error handling of the project.
 */
public class FileHandlerCreator
{
    public static Logger attachFileHandler(Logger logger, String className) {
	try {
	    logger.setLevel(Level.WARNING);
	    FileHandler fh = new FileHandler("log" + File.separator + className + "LogFile.log");
	    SimpleFormatter formatter = new SimpleFormatter();
	    fh.setFormatter(formatter);
	    logger.addHandler(fh);
	    return logger;
	} catch (IOException e) {
	    logger.log(Level.WARNING, "FileHandler not found", e);
	    return logger;
	}
    }
}
