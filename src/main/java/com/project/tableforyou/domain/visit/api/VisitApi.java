package com.project.tableforyou.domain.visit.api;

import com.project.tableforyou.security.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "[가게 방문 확인 API]", description = "가게 방문 관련 API")
public interface VisitApi {

    @Operation(summary = "사용자가 방문한 가게 불러오기 *", description = "사용자가 방문한 가게를 불러오는 API입니다.")
    ResponseEntity<?> readVisitRestaurant(@AuthenticationPrincipal PrincipalDetails principalDetails);
}
