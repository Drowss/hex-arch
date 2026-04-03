package com.hexarch.api.person.mapper;

import com.hexarch.Login;
import com.hexarch.api.person.request.LoginRequest;
import com.hexarch.api.person.request.UserRequest;
import com.hexarch.api.person.response.UserResponse;
import org.mapstruct.Mapper;
import com.hexarch.User;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "email", expression = "java(extractEmail(userRequest.getMetadata()))")
    User toDomain(UserRequest userRequest);

    UserResponse toResponse(User userRequest);

    Login toDomain(LoginRequest loginRequest);

    default String extractEmail(Map<String, Object> metadata) {
        if (metadata == null) return "";

        Object email = metadata.get("email");
        return email != null ? email.toString() : "";
    }
}
