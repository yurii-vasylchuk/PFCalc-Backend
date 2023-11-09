package org.mvasylchuk.pfcc.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.dto.PfccDto;
import org.mvasylchuk.pfcc.domain.dto.DishDto;
import org.mvasylchuk.pfcc.domain.dto.CommandMealDto;
import org.mvasylchuk.pfcc.user.Language;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private String email;
    private String name;
    private Language preferredLanguage;
    private PfccDto aims;
    private Boolean profileConfigured;
    private List<DishDto> dishes;
    private List<CommandMealDto> meals;
}
