package com.kala.military.application.ports.in;

import com.kala.military.application.dto.BattleRequest;
import com.kala.military.application.dto.BattleResultResponse;

public interface BattleUseCasePort {

    BattleResultResponse simulateBattle(BattleRequest request);
}
