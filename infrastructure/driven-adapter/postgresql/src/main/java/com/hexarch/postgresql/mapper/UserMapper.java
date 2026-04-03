package com.hexarch.postgresql.mapper;

import com.hexarch.User;
import com.hexarch.postgresql.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    UserEntity toEntity(User user);

    User toDomain(UserEntity userEntity);
}
