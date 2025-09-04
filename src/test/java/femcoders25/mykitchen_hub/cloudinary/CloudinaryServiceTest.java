package femcoders25.mykitchen_hub.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import femcoders25.mykitchen_hub.common.exception.InvalidImageFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private com.cloudinary.Uploader uploader;

    @Mock
    private MultipartFile multipartFile;

    private CloudinaryService cloudinaryService;

    @BeforeEach
    void setUp() {
        cloudinaryService = new CloudinaryService(cloudinary);
    }

    @Test
    void extractPublicIdFromUrl_WithVersion_ShouldReturnPublicId() {
        String url = "https://res.cloudinary.com/test/image/upload/v1234567890/sample_image.jpg";
        String result = cloudinaryService.extractPublicIdFromUrl(url);
        assertEquals("sample_image", result);
    }

    @Test
    void extractPublicIdFromUrl_WithoutVersion_ShouldReturnPublicId() {
        String url = "https://res.cloudinary.com/test/image/upload/sample_image.jpg";
        String result = cloudinaryService.extractPublicIdFromUrl(url);
        assertEquals("sample_image", result);
    }

    @Test
    void extractPublicIdFromUrl_WithComplexName_ShouldReturnPublicId() {
        String url = "https://res.cloudinary.com/test/image/upload/v1234567890/my-recipe-image-2024.png";
        String result = cloudinaryService.extractPublicIdFromUrl(url);
        assertEquals("my-recipe-image-2024", result);
    }

    @Test
    void extractPublicIdFromUrl_NullUrl_ShouldReturnNull() {
        String result = cloudinaryService.extractPublicIdFromUrl(null);
        assertNull(result);
    }

    @Test
    void extractPublicIdFromUrl_EmptyUrl_ShouldReturnNull() {
        String result = cloudinaryService.extractPublicIdFromUrl("");
        assertNull(result);
    }

    @Test
    void extractPublicIdFromUrl_DefaultImageUrl_ShouldReturnNull() {
        String result = cloudinaryService.extractPublicIdFromUrl(cloudinaryService.getDefaultImageUrl());
        assertNull(result);
    }

    @Test
    void extractPublicIdFromUrl_InvalidUrl_ShouldReturnNull() {
        String url = "https://example.com/not-a-cloudinary-url.jpg";
        String result = cloudinaryService.extractPublicIdFromUrl(url);
        assertNull(result);
    }

    @Test
    void extractPublicIdFromUrl_WithWhitespace_ShouldReturnNull() {
        String result = cloudinaryService.extractPublicIdFromUrl("   ");
        assertNull(result);
    }

    @Test
    void extractPublicIdFromUrl_FileNameWithoutExtension_ShouldReturnFileName() {
        String url = "https://res.cloudinary.com/test/image/upload/sample_image";
        String result = cloudinaryService.extractPublicIdFromUrl(url);
        assertEquals("sample_image", result);
    }

    @Test
    void extractPublicIdFromUrl_EmptyPartsAfterUpload_ShouldReturnNull() {
        String url = "https://res.cloudinary.com/test/image/upload/";
        String result = cloudinaryService.extractPublicIdFromUrl(url);
        assertEquals("", result);
    }

    @Test
    void uploadFile_Success() throws IOException {
        when(multipartFile.getBytes()).thenReturn("test image data".getBytes());
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(cloudinary.uploader()).thenReturn(uploader);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://res.cloudinary.com/test/image/upload/sample.jpg");
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        Map<String, Object> result = cloudinaryService.uploadFile(multipartFile);

        assertNotNull(result);
        assertEquals("https://res.cloudinary.com/test/image/upload/sample.jpg", result.get("secure_url"));
        verify(uploader).upload(any(byte[].class), anyMap());
    }

    @Test
    void uploadFile_InvalidFile_ThrowsException() {
        when(multipartFile.getSize()).thenReturn(10L * 1024 * 1024);

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.uploadFile(multipartFile));
    }

    @Test
    void deleteFile_ValidPublicId_Success() throws IOException {
        String publicId = "sample_image";
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq(publicId), anyMap())).thenReturn(Map.of("result", "ok"));

        cloudinaryService.deleteFile(publicId);

        verify(uploader).destroy(eq(publicId), anyMap());
    }

    @Test
    void deleteFile_NullPublicId_DoesNothing() throws IOException {
        cloudinaryService.deleteFile(null);

        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void deleteFile_EmptyPublicId_DoesNothing() throws IOException {
        cloudinaryService.deleteFile("");

        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void deleteFile_WhitespacePublicId_DoesNothing() throws IOException {
        cloudinaryService.deleteFile("   ");

        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void uploadImageSafely_NullImage_ReturnsDefaultUrl() {
        String result = cloudinaryService.uploadImageSafely(null);

        assertEquals(cloudinaryService.getDefaultImageUrl(), result);
    }

    @Test
    void uploadImageSafely_EmptyImage_ReturnsDefaultUrl() {
        when(multipartFile.isEmpty()).thenReturn(true);

        String result = cloudinaryService.uploadImageSafely(multipartFile);

        assertEquals(cloudinaryService.getDefaultImageUrl(), result);
    }

    @Test
    void uploadImageSafely_ValidImage_Success() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("test image data".getBytes());
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(cloudinary.uploader()).thenReturn(uploader);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://res.cloudinary.com/test/image/upload/sample.jpg");
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        String result = cloudinaryService.uploadImageSafely(multipartFile);

        assertEquals("https://res.cloudinary.com/test/image/upload/sample.jpg", result);
    }

    @Test
    void uploadImageSafely_InvalidImageFile_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(10L * 1024 * 1024);

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.uploadImageSafely(multipartFile));
    }

    @Test
    void uploadImageSafely_UploadException_ReturnsDefaultUrl() throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("test image data".getBytes());
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("Upload failed"));

        String result = cloudinaryService.uploadImageSafely(multipartFile);

        assertEquals(cloudinaryService.getDefaultImageUrl(), result);
    }

    @Test
    void replaceImageSafely_OldImageIsDefault_UploadsNewImage() throws IOException {
        String oldImageUrl = cloudinaryService.getDefaultImageUrl();
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("test image data".getBytes());
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(cloudinary.uploader()).thenReturn(uploader);

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://res.cloudinary.com/test/image/upload/new.jpg");
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        String result = cloudinaryService.replaceImageSafely(oldImageUrl, multipartFile);

        assertEquals("https://res.cloudinary.com/test/image/upload/new.jpg", result);
        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void replaceImageSafely_OldImageIsCloudinary_DeletesOldAndUploadsNew() throws IOException {
        String oldImageUrl = "https://res.cloudinary.com/test/image/upload/old_image.jpg";
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("test image data".getBytes());
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(cloudinary.uploader()).thenReturn(uploader);

        when(uploader.destroy(eq("old_image"), anyMap())).thenReturn(Map.of("result", "ok"));
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://res.cloudinary.com/test/image/upload/new.jpg");
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        String result = cloudinaryService.replaceImageSafely(oldImageUrl, multipartFile);

        assertEquals("https://res.cloudinary.com/test/image/upload/new.jpg", result);
        verify(uploader).destroy(eq("old_image"), anyMap());
    }

    @Test
    void replaceImageSafely_DeleteFails_StillUploadsNew() throws IOException {
        String oldImageUrl = "https://res.cloudinary.com/test/image/upload/old_image.jpg";
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("test image data".getBytes());
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(cloudinary.uploader()).thenReturn(uploader);

        when(uploader.destroy(eq("old_image"), anyMap())).thenThrow(new IOException("Delete failed"));
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", "https://res.cloudinary.com/test/image/upload/new.jpg");
        when(uploader.upload(any(byte[].class), anyMap())).thenReturn(uploadResult);

        String result = cloudinaryService.replaceImageSafely(oldImageUrl, multipartFile);

        assertEquals("https://res.cloudinary.com/test/image/upload/new.jpg", result);
    }

    @Test
    void validateImageFile_NullFile_ThrowsException() {
        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(null));
    }

    @Test
    void validateImageFile_EmptyFile_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(multipartFile));
    }

    @Test
    void validateImageFile_FileTooLarge_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(10L * 1024 * 1024);

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(multipartFile));
    }

    @Test
    void validateImageFile_InvalidContentType_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("text/plain");

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(multipartFile));
    }

    @Test
    void validateImageFile_NullContentType_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn(null);

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(multipartFile));
    }

    @Test
    void validateImageFile_NullFilename_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(multipartFile));
    }

    @Test
    void validateImageFile_InvalidExtension_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(multipartFile));
    }

    @Test
    void validateImageFile_NoExtension_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test");

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(multipartFile));
    }

    @Test
    void validateImageFile_ExtensionAtEnd_ThrowsException() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.");

        assertThrows(InvalidImageFileException.class, () -> cloudinaryService.validateImageFile(multipartFile));
    }

    @Test
    void validateImageFile_ValidFile_Success() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");

        assertDoesNotThrow(() -> cloudinaryService.validateImageFile(multipartFile));
    }
}
