package com.example.fileparser.producer;

import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.example.fileparser.FileParserApplication.FILES_QUEUE;

@Component
@Data
@Log4j
public class FileProducer implements Runnable{

    private String pathInput = "";

    @Override
    public void run() {
        Thread.currentThread().setName("FileProducer-Thread");
        log.info("Start FileProducer-Thread");

        try {
            filesIterator();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void filesIterator() throws IOException, InterruptedException {
        File folder = new File(pathInput);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file: listOfFiles) {
                if (file.isFile()) {
                    Path path = Paths.get(file.getPath());
                    byte[] byteFile = Files.readAllBytes(path);
                    Map<Path,byte[]> fileAndPath = new HashMap<>();
                    fileAndPath.put(path, byteFile);
                    FILES_QUEUE.put(fileAndPath);
                }
            }
        } else {
            log.warn("Folder is empty");
        }
    }

}
