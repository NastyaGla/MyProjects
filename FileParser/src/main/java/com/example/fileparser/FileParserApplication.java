package com.example.fileparser;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.example.fileparser.config.ApplicationConfig;
import com.example.fileparser.consumer.FileConsumerWriter;
import com.example.fileparser.handler.parser.FileHandler;
import com.example.fileparser.producer.FileProducer;
import com.example.fileparser.util.FileParserArgs;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class FileParserApplication {
    public static int FILES_QUEUE_SIZE = 2;
    public static BlockingQueue<Map<Path, byte[]>> FILES_QUEUE = new ArrayBlockingQueue<>(FILES_QUEUE_SIZE);
    public static BlockingQueue<Map<Path,String>> TEXT_QUEUE = new ArrayBlockingQueue<>(FILES_QUEUE_SIZE);
    public static BlockingQueue<JSONObject> JSON_QUEUE = new ArrayBlockingQueue<>(FILES_QUEUE_SIZE);
    public static BlockingQueue<Map<Path, List<byte[]>>> IMAGES_QUEUE = new ArrayBlockingQueue<>(FILES_QUEUE_SIZE);

    public static void main(String[] args) throws IOException, InterruptedException {


        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationConfig.class);

        FileParserArgs jArgs = new FileParserArgs();
        JCommander parserCmd = JCommander.newBuilder().addObject(jArgs).build();
        try {
            parserCmd.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getLocalizedMessage());
            parserCmd.usage();
        }
        boolean check = jArgs.isHelp();
       if(check) {
            parserCmd.usage();
            System.exit(-1);
        }


        ExecutorService executorService = Executors.newFixedThreadPool(3);

        String folderPathInput = jArgs.getIfp();
        String folderPathOutput = jArgs.getOfp();

        FileProducer fileProducer = applicationContext.getBean(FileProducer.class);
        fileProducer.setPathInput(folderPathInput);

        FileConsumerWriter fileConsumerWriter = applicationContext.getBean(FileConsumerWriter.class);
        fileConsumerWriter.setPathOutput(folderPathOutput);

        executorService.submit(fileProducer);
        executorService.submit(applicationContext.getBean(FileHandler.class));
        executorService.submit(fileConsumerWriter);
        executorService.shutdown();
        if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
            executorService.shutdownNow();
        }
    }
}