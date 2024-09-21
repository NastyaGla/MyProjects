package com.example.fileparser.handler.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.fileparser.FileParserApplication.*;

@Component
public class FileHandler implements Runnable{



    @Override
    public void run() {
        Thread.currentThread().setName("FileHandler-Thread");
        try {
            parseWithTika();
        } catch (InterruptedException  e) {
            throw new RuntimeException(e);
        }
    }

    public void parseWithTika() throws InterruptedException  {

        while (true){
            List<byte[]> images = new ArrayList<>();
            Map<Path,byte[]> pathAndFile= FILES_QUEUE.take();

            PDFParserConfig pdfParserConfig = new PDFParserConfig();
            pdfParserConfig.setExtractInlineImages(true);
            AutoDetectParser parser = new AutoDetectParser();
            ContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();

            EmbeddedDocumentExtractor embeddedDocumentExtractor =
                    new EmbeddedDocumentExtractor() {
                        @Override
                        public boolean shouldParseEmbedded(Metadata metadata) {
                            return true;
                        }
                        @Override
                        public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml) throws IOException {
                            byte[] out = stream.readAllBytes();
                            images.add(out);
                        }
                    };

            ParseContext context = new ParseContext();
            context.set(PDFParserConfig.class, pdfParserConfig);
            context.set(EmbeddedDocumentExtractor.class, embeddedDocumentExtractor);

            for (Map.Entry<Path,byte[]> entry: pathAndFile.entrySet()) {
                Path pathInput = entry.getKey();
                try (InputStream inputStream = new ByteArrayInputStream(entry.getValue())) {
                    parser.parse(inputStream, handler, metadata, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Map<String,Object> mapMetadata = new ConcurrentHashMap<>();
                String[] nameMetadata = metadata.names();
                for (String name: nameMetadata){
                    mapMetadata.put(name,metadata.get(name));
                }
                JSONObject json = getJson(mapMetadata);
                json.put("Path", pathInput);
                Map<Path,List<byte[]>> result_images = new HashMap<>();
                Map<Path,String> result_text = new HashMap<>();
                result_images.put(pathInput, images);
                result_text.put(pathInput,handler.toString());

                JSON_QUEUE.put(json);
                IMAGES_QUEUE.put(result_images);
                TEXT_QUEUE.put(result_text);
                System.out.println("handler is working");
            }
        }
    }
    private JSONObject getJson(Map<String,Object> strJson){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type typeObject = new TypeToken<HashMap<String, Object>>() {}.getType();
        String jsonData = gson.toJson(strJson, typeObject);
        JSONObject json = new JSONObject(jsonData);
        return json;
    }
}
