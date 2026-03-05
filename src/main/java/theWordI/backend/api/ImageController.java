package theWordI.backend.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import theWordI.backend.domain.file.service.ImageService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("image") MultipartFile image)
    {
        if (image.isEmpty())
        {
            return ResponseEntity.badRequest().body("파일이 없습니다.");
        }

        try {
            String imageUrl = imageService.uploadImage(image);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        }
        catch(IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 중 오류가 발생했습니다.");
        }
    }

}
