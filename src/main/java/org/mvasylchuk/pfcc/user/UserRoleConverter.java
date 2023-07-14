package org.mvasylchuk.pfcc.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class UserRoleConverter implements AttributeConverter<List<UserRole>, String> {

    public static final String DELIMITER = ",";

    @Override
    public String convertToDatabaseColumn(List<UserRole> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        return attribute.stream().map(UserRole::name).collect(Collectors.joining(DELIMITER));
    }

    @Override
    public List<UserRole> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Arrays.stream(dbData.split(DELIMITER)).map(UserRole::valueOf).toList();
    }
}
