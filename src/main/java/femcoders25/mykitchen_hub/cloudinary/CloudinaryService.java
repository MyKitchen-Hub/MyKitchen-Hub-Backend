package femcoders25.mykitchen_hub.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import femcoders25.mykitchen_hub.cloudinary.exception.InvalidImageFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class CloudinaryService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

    private final Cloudinary cloudinary;

    @Value("${cloudinary.default-image-url:http://localhost:8080/images/logo.png}")
    private String defaultImageUrl;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String getDefaultImageUrl() {
        return defaultImageUrl;
    }

    public Map<String, Object> uploadFile(MultipartFile file) throws IOException {
        validateImageFile(file);
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
    }

    public void deleteFile(String publicId) throws IOException {
        if (publicId != null && !publicId.trim().isEmpty()) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted image with publicId: {}", publicId);
        }
    }
    public String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty() || imageUrl.equals(getDefaultImageUrl())) {
            return null;
        }

        try {
            String[] parts = imageUrl.split("/");

            for (int i = 0; i < parts.length; i++) {
                if ("upload".equals(parts[i]) && i + 1 < parts.length) {
                    int publicIdIndex = i + 1;

                    if (parts[publicIdIndex].matches("v\\d+") && publicIdIndex + 1 < parts.length) {
                        publicIdIndex++;
                    }

                    if (publicIdIndex < parts.length) {
                        String fileName = parts[publicIdIndex];
                        int lastDotIndex = fileName.lastIndexOf('.');
                        return lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
                    }
                    break;
                }
            }

            log.warn("Could not extract publicId from URL: {}", imageUrl);
        } catch (Exception e) {
            log.error("Error extracting publicId from URL: {}, error: {}", imageUrl, e.getMessage());
        }

        return null;
    }

    public String uploadImageSafely(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return getDefaultImageUrl();
        }

        try {
            Map<String, Object> uploadResult = uploadFile(image);
            String imageUrl = (String) uploadResult.get("secure_url");
            log.info("Successfully uploaded image: {}", imageUrl);
            return imageUrl;
        } catch (InvalidImageFileException e) {
            log.error("Invalid image file: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to upload image, using default image. Error: {}", e.getMessage());
            return getDefaultImageUrl();
        }
    }

    public String replaceImageSafely(String oldImageUrl, MultipartFile newImage) {
        if (oldImageUrl != null && !oldImageUrl.equals(getDefaultImageUrl())) {
            try {
                String publicId = extractPublicIdFromUrl(oldImageUrl);
                if (publicId != null) {
                    deleteFile(publicId);
                }
            } catch (Exception e) {
                log.error("Failed to delete old image: {}, error: {}", oldImageUrl, e.getMessage());
            }
        }

        return uploadImageSafely(newImage);
    }

    public void validateImageFile(MultipartFile file) {
        if (file == null) {
            throw new InvalidImageFileException("File cannot be null");
        }

        if (file.isEmpty()) {
            throw new InvalidImageFileException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageFileException("File size exceeds " + (MAX_FILE_SIZE / 1024 / 1024) + "MB limit");
        }

        validateFileType(file);
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidImageFileException("Unsupported file type. Allowed types: " + ALLOWED_CONTENT_TYPES);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new InvalidImageFileException("File name cannot be null");
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidImageFileException("Unsupported file format. Allowed formats: " + ALLOWED_EXTENSIONS);
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new InvalidImageFileException("File must have a valid extension");
        }
        return filename.substring(lastDotIndex + 1);
    }
}
