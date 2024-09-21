package com.example.fileparser.consumer;

import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.fileparser.FileParserApplication.*;

@Component
@Setter
public class FileConsumerWriter implements Runnable{
    private String pathOutput= "";

    @Override
    public void run() {
        Thread.currentThread().setName("FileConsumerWriter-Thread");
        try {
            while (true) {
                metaOutput();
                imagesOutput();
                textOutput();
            }
        } catch (InterruptedException | IOException e) {
           e.printStackTrace();
        }
    }

    public void metaOutput() throws InterruptedException, IOException {
           JSONObject json = JSON_QUEUE.take();
           Path name = (Path)json.get("Path");

           String directoryPath = createDirectory(pathOutput, name.getFileName().toString());

           File file = new File(directoryPath,  "file_meta.json");
           FileWriter writer = new FileWriter(file);
           writer.write(json.toString());
           writer.close();
           System.out.println("consumer meta is working");
    }
    public void imagesOutput() throws InterruptedException, IOException {

        Map<Path, List<byte[]>> map_images = IMAGES_QUEUE.take();
        List<byte[]> images = null;
        String name = null;
        for(Map.Entry<Path,List<byte[]>> entry: map_images.entrySet()){
            images = entry.getValue();
            name = String.valueOf(entry.getKey().getFileName());
        }

        String directoryPath = createDirectory(pathOutput, name);
        String directory_image = createDirectory(directoryPath, "images_folder");

        int count =1;
        for (byte[] image: images){
            FileUtils.writeByteArrayToFile(new File(directory_image, "image"+count + ".jpeg"),image);
            count++;
        }
        System.out.println("consumer images is working");
    }
    public void textOutput() throws InterruptedException, IOException {
        Map<Path,String> map_text = TEXT_QUEUE.take();
        String text = null;
        String name = null;
        for(Map.Entry<Path,String> entry: map_text.entrySet()){
            text = entry.getValue();
            name = String.valueOf(entry.getKey().getFileName());
        }
        String directoryPath = createDirectory(pathOutput, name);

        File file = new File(directoryPath,  "file_text.txt");
        FileWriter writer = new FileWriter(file);
        assert text != null;
        writer.write(text);
        writer.close();
        System.out.println("consumer text is working");
    }
    private String createDirectory(String path,String name){
        File directory = new File(path, name);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directory.getPath();
    }
}


