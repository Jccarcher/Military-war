package com.kala.military.adapters.in.rest;

import com.kala.military.application.dto.ArmyResponse;
import com.kala.military.application.dto.BattleResultResponse;
import com.kala.military.application.dto.UnitResponse;
import com.kala.military.application.ports.in.ArmyUseCasePort;
import com.kala.military.application.ports.in.BattleUseCasePort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Verifies the HTTP contract of the REST adapter in isolation from the rest of the hexagon. */
@WebMvcTest(ArmyController.class)
final class ArmyControllerWebMvcTest {

    private static final String ARMY_ID = "5f10db6e-3f79-4e4c-8ac6-58a1db7a6b8b";
    private static final String RIVAL_ID = "6ac91bb8-2e4c-4dab-a6b8-5d3e7f121a02";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArmyUseCasePort armyUseCasePort;

    @MockitoBean
    private BattleUseCasePort battleUseCasePort;

    @Test
    void shouldReturnCreatedWithArmyBodyWhenCivilizationIsValid() throws Exception {
        given(armyUseCasePort.createArmy(any())).willReturn(sampleArmy());

        mockMvc.perform(post("/api/v1/armies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"civilization\":\"chinos\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ARMY_ID))
                .andExpect(jsonPath("$.civilization").value("china"))
                .andExpect(jsonPath("$.gold").value(1000))
                .andExpect(jsonPath("$.units[0].type").value("Piquero"));
    }

    @Test
    void shouldReturnArmyWhenItExists() throws Exception {
        given(armyUseCasePort.getArmy(ARMY_ID)).willReturn(sampleArmy());

        mockMvc.perform(get("/api/v1/armies/" + ARMY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ARMY_ID))
                .andExpect(jsonPath("$.battleHistory").isArray());
    }

    @Test
    void shouldTrainUnitAndReturnUpdatedArmy() throws Exception {
        given(armyUseCasePort.trainUnit(any())).willReturn(sampleArmy());

        mockMvc.perform(post("/api/v1/armies/" + ARMY_ID + "/train")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"unitType\":\"Piquero\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ARMY_ID))
                .andExpect(jsonPath("$.units[0].type").value("Piquero"));
    }

    @Test
    void shouldTransformUnitAndReturnUpdatedArmy() throws Exception {
        given(armyUseCasePort.transformUnit(any())).willReturn(sampleArmy());

        mockMvc.perform(post("/api/v1/armies/" + ARMY_ID + "/transform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceType\":\"Piquero\",\"targetType\":\"Arquero\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.civilization").value("china"));
    }

    @Test
    void shouldSimulateBattleAndReturnItsResult() throws Exception {
        given(battleUseCasePort.simulateBattle(any()))
                .willReturn(new BattleResultResponse("victory", ARMY_ID, RIVAL_ID, "Battle simulated successfully"));

        mockMvc.perform(post("/api/v1/battle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstArmyId\":\"" + ARMY_ID + "\",\"secondArmyId\":\"" + RIVAL_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("victory"))
                .andExpect(jsonPath("$.winnerId").value(ARMY_ID))
                .andExpect(jsonPath("$.loserId").value(RIVAL_ID));
    }

    @Test
    void shouldReturnBadRequestWhenBattleIdsAreMissing() throws Exception {
        mockMvc.perform(post("/api/v1/battle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstArmyId\":\"" + ARMY_ID + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El identificador del segundo ejército es obligatorio"));
    }

    @Test
    void shouldReturnBadRequestWhenCivilizationIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/armies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La civilización es obligatoria"));
    }

    @Test
    void shouldReturnBadRequestInsteadOfServerErrorWhenUnitTypeIsMissing() throws Exception {
        mockMvc.perform(post("/api/v1/armies/" + ARMY_ID + "/train")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El tipo de unidad es obligatorio"));
    }

    @Test
    void shouldReturnBadRequestWhenTransformationTypesAreMissing() throws Exception {
        mockMvc.perform(post("/api/v1/armies/" + ARMY_ID + "/transform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceType\":\"Piquero\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El tipo de unidad de destino es obligatorio"));
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsNotReadable() throws Exception {
        mockMvc.perform(post("/api/v1/battle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El cuerpo de la solicitud es inválido"));
    }

    @Test
    void shouldTranslateBusinessFailureIntoBadRequest() throws Exception {
        given(armyUseCasePort.getArmy(any())).willThrow(new IllegalArgumentException("Ejército no encontrado"));

        mockMvc.perform(get("/api/v1/armies/" + ARMY_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ejército no encontrado"));
    }

    private static ArmyResponse sampleArmy() {
        return new ArmyResponse(ARMY_ID, "china", 1000,
                List.of(new UnitResponse("Piquero", 5, 0)), List.of());
    }
}
