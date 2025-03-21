package org.shelter.app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.shelter.app.database.entity.Notification;
import org.shelter.app.dto.NotificationReadDto;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "adoptionRequest.id", target = "adoptionRequestId")
    NotificationReadDto toDto(Notification notification);
}
