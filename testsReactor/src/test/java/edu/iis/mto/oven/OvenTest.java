package edu.iis.mto.oven;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OvenTest {

    @Mock
    HeatingModule heatingModule;

    @Mock
    Fan fan;

    List<ProgramStage> programStageList = List.of(
            ProgramStage.builder()
                    .withStageTime(50)
                    .withHeat(HeatType.GRILL)
                    .withTargetTemp(100)
                    .build(),
            ProgramStage.builder()
                    .withStageTime(20)
                    .withHeat(HeatType.HEATER)
                    .withTargetTemp(100)
                    .build()
    );

    BakingProgram bakingProgram;

    Oven oven;

    @BeforeEach
    void setUp() {
        oven = new Oven(heatingModule, fan);
        bakingProgram = BakingProgram.builder()
                .withStages(programStageList)
                .withInitialTemp(100)
                .build();
    }
    @Test
    void whenCantHeatOvenShoudReturnExeption() throws HeatingException {
        doThrow(HeatingException.class).when(heatingModule).heater(any(HeatingSettings.class));
        Assertions.assertThrows(OvenException.class, () -> oven.runProgram(bakingProgram));

    }

}
