package femcoders25.mykitchen_hub.cloudinary;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceTest {

    @Mock
    private Cloudinary cloudinary;

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
}
