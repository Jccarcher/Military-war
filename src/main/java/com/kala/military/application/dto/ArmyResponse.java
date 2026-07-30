package com.kala.military.application.dto;

import java.util.List;

public record ArmyResponse(String id, String civilization, int gold, List<UnitResponse> units, List<String> battleHistory) {
}
