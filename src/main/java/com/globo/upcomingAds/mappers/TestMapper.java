package com.globo.upcomingAds.mappers;

import com.globo.upcomingAds.dtos.response.LabelsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TestMapper {

    LabelsDTO labelsToLabels(LabelsDTO labelsDTO);
}
