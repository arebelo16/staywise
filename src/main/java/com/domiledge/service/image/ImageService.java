package com.domiledge.service.image;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @PostConstruct
    public void registerImageIOPlugins() {
        // This will scan and activate TwelveMonkeys plugin to support WEBP
        ImageIO.scanForPlugins();
    }

    public String convertToPngAndSave(MultipartFile file, Long userId, UUID propertyId) {
        String outputFilename = UUID.randomUUID() + "_cover.png";
        Path uploadDir = Paths.get("uploads", userId.toString(), propertyId.toString());
        Path outputPath = uploadDir.resolve(outputFilename);

        try {
            // Create directories if they don't exist
            Files.createDirectories(uploadDir);

            // Read input image
            BufferedImage inputImage = ImageIO.read(file.getInputStream());
            if (inputImage == null) {
                throw new IllegalArgumentException("Unsupported or corrupted image format.");
            }

            // Convert to ARGB PNG with transparency
            BufferedImage convertedImage = new BufferedImage(
                    inputImage.getWidth(),
                    inputImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D g2d = convertedImage.createGraphics();
            g2d.drawImage(inputImage, 0, 0, null);
            g2d.dispose();

            // Save to disk as PNG
            ImageIO.write(convertedImage, "png", outputPath.toFile());

            return "/uploads/" + userId + "/" + propertyId + "/" + outputFilename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to process or save image", e);
        }
    }
}