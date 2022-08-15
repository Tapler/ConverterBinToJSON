package com.seroev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.seroev.parser.Parser;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String sourceFile = args[0];
        String outputFile = args[1];

        File file = new File(sourceFile);
        byte[] sourceFileBytes = new byte[0];
        try {
            sourceFileBytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            LOGGER.error(String.format("Не удалось обработать файл: %s", sourceFile), e);
            e.printStackTrace();
        }

        Parser parser = new Parser();
        LOGGER.info(String.format("Исходный документ: %s", DatatypeConverter.printHexBinary(sourceFileBytes)));

        String json = parser.getJsonFromBytes(sourceFileBytes);

        Path outputPath = Paths.get(outputFile);
        try {
            Files.write(outputPath, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error(String.format("Не удалось записать в файл: %s", outputFile), e);
            e.printStackTrace();
        }

        LOGGER.info("Создан json: " + outputPath + ", файл расположен: " + outputPath.toAbsolutePath());
    }
}
