package org.shelter.app.dto;

import lombok.Data;
import org.shelter.app.validation.ImageAction;
import org.shelter.app.validation.ImageValid;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageUploadDto {

    @ImageValid(groups = ImageAction.class)
    private MultipartFile file;
}
