package com.bloomberg.bask.job;

import com.bloomberg.bask.feed.Feed;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunFeed {

    private static final Logger logger = LoggerFactory.getLogger(RunFeed.class);

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts(
            "feed",
            "The filename of the feed properties file"
        ).withRequiredArg().ofType(String.class).required();

        OptionSet options = parser.parse(args);

        RunFeed runFeed = new RunFeed();
        runFeed.run((String) options.valueOf("feed"));
    }

    public void run(String filename) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(filename));
            Class<?> clazz = Class.forName(props.getProperty("feed.factory"));
            Object feedFactory = clazz.newInstance();
            Method method = clazz.getMethod("getFeed", Properties.class);
            Feed feed = (Feed)method.invoke(feedFactory, props);
            feed.start();
        } catch (FileNotFoundException fe) {
            logger.error("{} not found", filename, fe);
            throw new RuntimeException();
        } catch (IOException ioe) {
            logger.error("{} could no be loaded", filename, ioe);
            throw new RuntimeException();
        } catch (ClassNotFoundException ce) {
            logger.error("{} class not found", props.getProperty("feed.factory"), ce);
            throw new RuntimeException();
        } catch (InstantiationException ie) {
            logger.error("Instantiation exception", ie);
            throw new RuntimeException();
        } catch (IllegalAccessException iae) {
            logger.error("Illegal accesss", iae);
            throw new RuntimeException();
        } catch (NoSuchMethodException nsme) {
            throw new RuntimeException();
        } catch (InvocationTargetException ite) {
            throw new RuntimeException();
        }
    }
}
