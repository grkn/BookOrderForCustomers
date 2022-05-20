package com.reading.is.good.controller;

import com.reading.is.good.constant.ChallengeConstant;
import com.reading.is.good.resource.OrderStatisticsResource;
import com.reading.is.good.resource.StatisticsResource;
import com.reading.is.good.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ChallengeConstant.BASE_URL)
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping(value = "/statistics")
    @PreAuthorize(value = "hasRole('ROLE_USER')")
    public ResponseEntity<Collection<StatisticsResource>> getStatistics(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(statisticsService.getStatistics(userName));
    }
}
