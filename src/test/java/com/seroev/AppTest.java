package com.seroev;

import com.seroev.parser.Parser;
import org.junit.jupiter.api.*;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    byte[] sourceFileBytes;
    final Parser parser = new Parser();

    @BeforeEach
    public void getBytesFromBin() {
        String binFileName = "data-1.bin";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(binFileName)).getFile());

        sourceFileBytes = new byte[0];
        try {
            sourceFileBytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Содержимое bin файла должно считывать корректно")
    void shouldCorrectlyReadBinFile() {
        String binFileContent = DatatypeConverter.printHexBinary(sourceFileBytes);

        assertEquals(binFileContent,
                "01000400A83292560200030004710203000B008E8E8E2090AEACA0E8AAA004001D000B00070084EBE0AEAAAEAB0C000200204E0D00020000020E000200409C04001D000B00070084EBE0AEAAAEAB0C000200204E0D00020000020E000200409C");
    }

    @Test
    @DisplayName("Должен записываться корректный json")
    void shouldWriteCorrectJson() {
        String json = parser.getJsonFromBytes(sourceFileBytes);
        assertEquals(json, "{\"datetime\":\"2016-01-10T10:30:00\",\"orderNumber\":160004," +
                "\"customerName\":\"ООО Ромашка\",\"items\":[{\"name\":\"Дырокол\",\"price\":20000,\"quantity\":2.0,\"sum\":40000}]}");
    }

    @Test
    @DisplayName("Должен выдавать ошибку из-за неверного номера тэга")
    void shouldThrowIllegalArgumentExceptionWithMessageAboutWrongTagType() {
        sourceFileBytes[0] = 15;
        final byte[] sourceFileBytesWithException = Arrays.copyOfRange(sourceFileBytes, 0, sourceFileBytes.length);

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> parser.getJsonFromBytes(sourceFileBytesWithException));
    }

    @Test
    @DisplayName("Должен выдавать ошибку из-за некорректного значения length")
    void shouldThrowIllegalArgumentExceptionDueToIncorrectLength() {
        final byte[] sourceFileBytesWithException = Arrays.copyOfRange(sourceFileBytes, 0, sourceFileBytes.length - 4);

        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> parser.getJsonFromBytes(sourceFileBytesWithException));
    }

    @Test
    @DisplayName("Полученный json должен соответствовать эталонному")
    void shouldEqualsToStandard() throws IOException {
        String json = parser.getJsonFromBytes(sourceFileBytes);
        String outputJsonName = "result.json";
        Path outputPath = Paths.get(outputJsonName);
        try {
            Files.write(outputPath, json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String standardJsonName = "jsonStandard.json";

        ClassLoader classLoader = getClass().getClassLoader();
        File fileJson = new File(Objects.requireNonNull(classLoader.getResource(standardJsonName)).getFile());

        byte[] file1Bytes = Files.readAllBytes(outputPath);
        byte[] file2Bytes = Files.readAllBytes(fileJson.toPath());

        String file1 = new String(file1Bytes, StandardCharsets.UTF_8);
        String file2 = new String(file2Bytes, StandardCharsets.UTF_8);

        assertEquals(file1, file2);
    }
}