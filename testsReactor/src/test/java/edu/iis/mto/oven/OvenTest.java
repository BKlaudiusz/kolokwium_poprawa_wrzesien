package edu.iis.mto.oven;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class OvenTest {

    @Mock
    HeatingModule heatingModule;

    @Mock
    Fan fan;

    int SAMPLE_TEMPERATURE = 100;
    int ZERO_TEMPERATURE = 0;
    int SAMPLE_STAGE_TIME = 50;

    List<ProgramStage> programStageList = List.of(
            ProgramStage.builder()
                    .withStageTime(SAMPLE_STAGE_TIME)
                    .withHeat(HeatType.GRILL)
                    .withTargetTemp(SAMPLE_TEMPERATURE)
                    .build(),
            ProgramStage.builder()
                    .withStageTime(SAMPLE_STAGE_TIME)
                    .withHeat(HeatType.HEATER)
                    .withTargetTemp(SAMPLE_TEMPERATURE)
                    .build()
    );

    List<ProgramStage> programStageListWithHEATER = List.of(
            ProgramStage.builder()
                    .withStageTime(SAMPLE_STAGE_TIME)
                    .withHeat(HeatType.HEATER)
                    .withTargetTemp(SAMPLE_TEMPERATURE)
                    .build());

    List<ProgramStage> programStageListWithGrill = List.of(
            ProgramStage.builder()
                    .withStageTime(SAMPLE_STAGE_TIME)
                    .withHeat(HeatType.GRILL)
                    .withTargetTemp(SAMPLE_TEMPERATURE)
                    .build());

    BakingProgram bakingProgram;

    Oven oven;

    @BeforeEach
    void setUp() {
        oven = new Oven(heatingModule, fan);
        bakingProgram = BakingProgram.builder()
                .withStages(programStageList)
                .withInitialTemp(SAMPLE_TEMPERATURE)
                .build();
    }
    @Test
    void whenCantHeatOvenShouldReturnException() throws HeatingException {
        doThrow(HeatingException.class).when(heatingModule).heater(any(HeatingSettings.class));
        Assertions.assertThrows(OvenException.class, () -> oven.runProgram(bakingProgram));
    }

    @Test
    void whenCantNOtgrillShoudReturnExeption() throws HeatingException {
        doThrow(HeatingException.class).when(heatingModule).grill(any(HeatingSettings.class));
        bakingProgram = BakingProgram.builder()
                .withStages(programStageListWithGrill)
                .withInitialTemp(ZERO_TEMPERATURE)
                .build();

        Assertions.assertThrows(OvenException.class, () -> oven.runProgram(bakingProgram));
    }

    @Test
    void whenIsTHERM_CIRCULATIONShouldRunFanAndOffFan()
    {
        List<ProgramStage> programStageList1 = List.of(
                ProgramStage.builder()
                        .withStageTime(SAMPLE_STAGE_TIME)
                        .withHeat(HeatType.THERMO_CIRCULATION)
                        .withTargetTemp(SAMPLE_TEMPERATURE)
                        .build());

        bakingProgram = BakingProgram.builder()
                .withStages(programStageList1)
                .withInitialTemp(SAMPLE_TEMPERATURE)
                .build();

        oven.runProgram(bakingProgram);

        InOrder callOrder = inOrder(fan);
        callOrder.verify(fan).on();
        callOrder.verify(fan).off();
    }
    @Test
   void whenHeatTypeIsGrillShouldCheckFanIsOnAndTurnOffHim()
    {
        bakingProgram = BakingProgram.builder()
                .withStages(programStageListWithGrill)
                .withInitialTemp(SAMPLE_TEMPERATURE)
                .build();


        when(fan.isOn()).thenReturn(true);
        oven.runProgram(bakingProgram);

        InOrder callOrder = inOrder(fan);
        callOrder.verify(fan).isOn();
        callOrder.verify(fan).off();
    }

    @Test
    void whenHeatTypeIsGrillShouldTurnOnGrillModule() throws HeatingException {

        bakingProgram = BakingProgram.builder()
                .withStages(programStageListWithGrill)
                .withInitialTemp(ZERO_TEMPERATURE)
                .build();

        oven.runProgram(bakingProgram);

        verify(heatingModule, times(1)).grill(any(HeatingSettings.class));
        verify(heatingModule, times(0)).heater(any(HeatingSettings.class));
    }
    @Test
    void whenHeatTypeIsHeaterAndStartTemperatureBiggestThat0ShouldHeaterRun2Times() throws HeatingException {

        bakingProgram = BakingProgram.builder()
                .withStages(programStageListWithHEATER)
                .withInitialTemp(SAMPLE_TEMPERATURE)
                .build();

        oven.runProgram(bakingProgram);

        verify(heatingModule, times(2)).heater(any(HeatingSettings.class));
    }

    @Test
    void whenCoolAtFinishInBackingProgramIsTrueShouldTurnOnFanApterBakingProgram()  {

        bakingProgram = BakingProgram.builder()
                .withStages(programStageListWithHEATER)
                .withInitialTemp(50).withCoolAtFinish(true)
                .build();

        when(fan.isOn()).thenReturn(true);

        oven.runProgram(bakingProgram);


        verify(fan, times(1)).on();
    }

}
