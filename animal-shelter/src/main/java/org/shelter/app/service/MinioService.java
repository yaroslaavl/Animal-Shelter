package org.shelter.app.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.shelter.app.database.entity.Pet;
import org.shelter.app.database.entity.User;
import org.shelter.app.database.repository.PetRepository;
import org.shelter.app.database.repository.UserRepository;
import org.shelter.app.exception.PetNotFoundException;
import org.shelter.app.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;

    @Value("${minio.bucket-name}")
    String bucket;

    @Value("${minio.url}")
    private String url;

    private final MinioClient minioClient;
    public static String MINIO_INTERNAL_URL = "minio";
    public static String MINIO_PUBLIC_URL = "localhost";

    @SneakyThrows
    @Transactional
    public void uploadImage(MultipartFile file, Long petId) {
        User user = userRepository.findByEmail(securityContext())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Pet pet = (petId != null) ? petRepository.findById(petId).orElse(null) : null;

        boolean foundBucket = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        log.info("Checking if bucket {} exists...", bucket);

        if (!foundBucket) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }

        String identifier = (pet == null) ? user.getEmail() : pet.getName();
        Optional.ofNullable(findExisting(identifier, petId)).ifPresent(this::delete);

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String termination = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1)
                : "png";

        String objectName = "";
        if (pet == null) {
            objectName = "avatars/" + user.getEmail() + "." + termination;
        } else {
            objectName = "pets-images/" + pet.getName() + "." + termination;
        }

        minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                        .bucket(bucket)
                        .config("""
                                {
                                            "Version": "2012-10-17",
                                            "Statement": [
                                                {
                                                    "Effect": "Allow",
                                                    "Principal": "*",
                                                    "Action": "s3:GetObject",
                                                    "Resource": "arn:aws:s3:::%s/*"
                                                }
                                            ]
                                        }
                                """.formatted(bucket)
                        )
                        .build()
        );

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        if (pet == null) {
            user.setProfilePicture(url.contains(MINIO_INTERNAL_URL)
                    ? url.replace(MINIO_INTERNAL_URL, MINIO_PUBLIC_URL) + "/" + bucket + "/" + objectName
                    : url + "/" + bucket + "/" + objectName);
            userRepository.save(user);
        } else {
            pet.setImageUrl(url.contains(MINIO_INTERNAL_URL)
                    ? url.replace(MINIO_INTERNAL_URL, MINIO_PUBLIC_URL) + "/" + bucket + "/" + objectName
                    : url + "/" + bucket + "/" + objectName);
            petRepository.save(pet);
        }
    }

    @SneakyThrows
    private String findExisting(String objectName, Long petId) {
        String folder = petId != null ? "pets-images/" : "avatars/";

        Iterable<Result<Item>> results =
                minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).prefix(folder).build());

        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.objectName().startsWith(folder + objectName)) {
                return item.objectName();
            }
        }
        return null;
    }

    @SneakyThrows
    private void delete(String objectName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder().bucket(bucket).object(objectName).build()
        );
        log.info("Deleted object: {}", objectName);
    }

    public String getUserAvatar(String objectName, Boolean isPet) {
        if (isPet) {
            Pet pet = petRepository.findPetByName(objectName)
                    .orElseThrow(() -> new PetNotFoundException("Pet not found"));

            if (pet.getImageUrl() != null) {
                String existing = findExisting(pet.getName(), pet.getId());
                if (existing != null) {
                    return pet.getImageUrl();
                }
            }
        } else {
            User user = userRepository.findByEmail(objectName)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (user.getProfilePicture() != null) {
                String existing = findExisting(user.getEmail(), null);
                if (existing != null) {
                    return user.getProfilePicture();
                }
            }
        }

        return null;
    }

    private String securityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
