package org.shelter.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.shelter.app.database.entity.Notification;
import org.shelter.app.dto.NotificationCreateDto;
import org.shelter.app.dto.NotificationReadDto;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "adoptionRequest.id", target = "adoptionRequestId")
    NotificationReadDto toDto(Notification notification);

    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "adoptionRequestId", target = "adoptionRequest.id")
    Notification toEntity(NotificationCreateDto notificationCreateEditDto);
}
