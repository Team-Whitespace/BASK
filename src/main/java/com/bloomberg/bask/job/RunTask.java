package com.bloomberg.bask.job;

import com.bloomberg.bask.task.Task;
import com.bloomberg.bask.system.SystemProducer;
import com.bloomberg.bask.system.TaskManager;
import com.bloomberg.bask.util.PropertiesHelper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunTask {

    private static final Logger logger = LoggerFactory.getLogger(RunTask.class);

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts(
            "task",
            "The filename of the task properties file"
        ).withRequiredArg().ofType(String.class).required();

        OptionSet options = parser.parse(args);

        RunTask runTask = new RunTask();
        runTask.run((String) options.valueOf("task"));
    }

    public void run(String filename) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(filename));
            Class<?> clazz = Class.forName(props.getProperty("task.class"));
            Task task = (Task)clazz.newInstance();
            List<String> streams = PropertiesHelper.valuesToList(props.getProperty("input.streams"));
            SystemProducer producer = new SystemProducer(PropertiesHelper.extractConfig("kafka.producer.", props));
            Properties consumerConfig = PropertiesHelper.extractConfig("kafka.consumer.", props);
            TaskManager taskManager = new TaskManager(consumerConfig, producer);
            taskManager.run(task, streams);
        } catch (FileNotFoundException fe) {
            logger.error("{} not found", filename, fe);
            throw new RuntimeException();
        } catch (IOException ioe) {
            logger.error("{} could no be loaded", filename, ioe);
            throw new RuntimeException();
        } catch (ClassNotFoundException ce) {
            logger.error("{} class not found", props.getProperty("task.class"), ce);
            throw new RuntimeException();
        } catch (InstantiationException ie) {
            logger.error("Instantiation exception", ie);
            throw new RuntimeException();
        } catch (IllegalAccessException iae) {
            logger.error("Illegal accesss", iae);
            throw new RuntimeException();
        }
    }
}
