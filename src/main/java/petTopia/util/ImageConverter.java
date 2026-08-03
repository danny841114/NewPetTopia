package petTopia.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;

@Slf4j
public class ImageConverter {
    // 讀取檔案之 Mime Type
    public static String getMimeType(byte[] image) {
        String defaultMimeType = "image/jpeg";

        if (image == null || image.length == 0) return defaultMimeType;

        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(image))) {
            if (iis == null) return defaultMimeType;

            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                String formatName = reader.getFormatName().toLowerCase();

                return switch (formatName) {
                    case "png" -> "image/png";
                    case "gif" -> "image/gif";
                    case "bmp" -> "image/bmp";
                    case "webp" -> "image/webp";
                    default -> "image/jpeg";
                };
            }
        } catch (IOException e) {
            log.error("Get mine type from image failed", e);
        }

        return defaultMimeType;
    }

    // 單張照片轉 base64
    public static String byteToBase64(byte[] bytes) {
        if (bytes == null) return null;
        return Base64.getEncoder().encodeToString(bytes);
    }

    // 將專案內的檔案轉為 byte[]
    public static byte[] convertUrlToByteArray(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    // 將外部檔案轉成 byte[]
    public static byte[] processImage(MultipartFile file) throws IOException {
        return file.getBytes();
    }
}