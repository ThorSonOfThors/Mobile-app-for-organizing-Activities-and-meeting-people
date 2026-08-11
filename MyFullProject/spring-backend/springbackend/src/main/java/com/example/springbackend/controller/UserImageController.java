package com.example.springbackend.controller;

import com.example.springbackend.entity.UserImage;
import com.example.springbackend.service.UserImageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserImageController {

    private final UserImageService service;

    public UserImageController(
            UserImageService service
    ) {
        this.service = service;
    }

    @PostMapping(
            value = "/{id}/images",
            consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserImage uploadImage(
            @PathVariable Long id,
            @RequestParam("file")
            MultipartFile file
    ) throws Exception {

        String filename =
                UUID.randomUUID()
                        + "_"
                        + file.getOriginalFilename();

        Path uploadPath =
                Paths.get(
                        "uploads",
                        filename
                );

        Files.copy(
                file.getInputStream(),
                uploadPath
        );

        UserImage image =
                new UserImage();

        image.setUserId(id);

        image.setImagePath(
                filename
        );

        return service.save(image);
    }


    @GetMapping("/{id}/images")
    public java.util.List<UserImage>
        getImages(
                @PathVariable Long id
        ) {
                return service.getImages(id);
        }

        @DeleteMapping("/{id}/images/{imageId}")
        public void deleteImage(
                @PathVariable Long id,
                @PathVariable Long imageId
        ) {
        service.delete(imageId);
        }


        @GetMapping(
        value = "/profile-image/{imageId}",
        produces = MediaType.IMAGE_JPEG_VALUE
        )
        public @ResponseBody byte[] getProfileImage(
                @PathVariable Long imageId
        ) throws Exception {

        UserImage image = service.findById(imageId);

        Path path = Paths.get(
                "uploads",
                image.getImagePath()
        );

        return Files.readAllBytes(path);
        }



    


}
