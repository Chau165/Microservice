package com.group1.app.metadata.mapper.staff;

import com.group1.app.shift.dto.response.StaffResponse;
import com.group1.app.shift.entity.Staff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StaffMapper {
    StaffResponse toResponse(Staff staff);
}
