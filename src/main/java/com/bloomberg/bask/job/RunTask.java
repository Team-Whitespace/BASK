package com.bloomberg.bask.job;

import com.bloomberg.bask.system.SystemProducer;
import com.bloomberg.bask.system.TaskManager;
import com.bloomberg.bask.task.InitableTask;
import com.bloomberg.bask.task.StreamTask;
import com.bloomberg.bask.util.PropertiesHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
            String taskClass = props.getProperty("task.class");
            Class<?> clazz = Class.forName(taskClass);
            if (!StreamTask.class.isAssignableFrom(clazz)) {
                throw new NotATaskException(taskClass + " is not a task");
            }
            StreamTask task = (StreamTask)clazz.newInstance();
            List<String> streams = PropertiesHelper.valuesToList(props.getProperty("input.streams"));
            SystemProducer producer = new SystemProducer(PropertiesHelper.extractConfig("kafka.producer.", props));
            Properties consumerConfig = PropertiesHelper.extractConfig("kafka.consumer.", props);
            TaskManager taskManager = new TaskManager(consumerConfig, producer);
            if (InitableTask.class.isAssignableFrom(clazz)) {
                clazz.getMethod("init", Properties.class).invoke(task, props);
            }
            taskManager.run(task, streams);
        } catch (IOException|ClassNotFoundException|InstantiationException|
                IllegalAccessException|InvocationTargetException|NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }
}
