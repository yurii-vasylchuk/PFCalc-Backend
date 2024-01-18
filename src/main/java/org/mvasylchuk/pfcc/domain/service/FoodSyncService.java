package org.mvasylchuk.pfcc.domain.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Getter;
import lombok.Setter;
import org.mvasylchuk.pfcc.common.jpa.Pfcc;
import org.mvasylchuk.pfcc.domain.entity.FoodEntity;
import org.mvasylchuk.pfcc.domain.entity.FoodType;
import org.mvasylchuk.pfcc.domain.repository.FoodRepository;
import org.mvasylchuk.pfcc.platform.error.ApiErrorCode;
import org.mvasylchuk.pfcc.platform.error.PfccException;
import org.mvasylchuk.pfcc.user.UserService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class FoodSyncService {

    private final CsvMapper mapper = new CsvMapper();
    private final CsvSchema schema = mapper.typedSchemaFor(FoodSyncDto.class).withHeader();

    public FoodSyncService(FoodRepository foodRepository, UserService userService) {
        this.foodRepository = foodRepository;
        this.userService = userService;
    }

    private final FoodRepository foodRepository;
    private final UserService userService;

    public void sync(byte[] data) {
        try (MappingIterator<FoodSyncDto> iter = mapper.readerFor(FoodSyncDto.class).with(schema).readValues(data)) {
            List<FoodEntity> foodEntityList = new ArrayList<>();

            while (iter.hasNext()) {
                FoodSyncDto n = iter.next();
                FoodEntity foodEntity = new FoodEntity(
                        n.id,
                        n.name,
                        FoodType.INGREDIENT,
                        new Pfcc(n.protein, n.fat, n.carbohydrates, n.calories),
                        false,
                        userService.currentUser(),
                        n.description,
                        false,
                        null
                );
                foodEntityList.add(foodEntity);
            }
            foodRepository.saveAll(foodEntityList);
        } catch (IOException e) {
            throw new PfccException(e, ApiErrorCode.INVALID_CSV_FILE);
        }
    }

    @Getter
    @Setter
    private static class FoodSyncDto {
        private Long id;
        private String name;
        private BigDecimal protein;
        private BigDecimal fat;
        private BigDecimal carbohydrates;
        private BigDecimal calories;
        private String description;

    }
}
