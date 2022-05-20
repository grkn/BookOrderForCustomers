package com.reading.is.good.service;

import com.reading.is.good.mongorepos.CustomerRepository;
import com.reading.is.good.resource.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);

    private final MongoTemplate mongoTemplate;
    private final CustomerRepository customerRepository;

    public Collection<StatisticsResource> getStatistics(String username) {
        LOGGER.info("Statistics for user {} start performing", username);
        List<OrderStatisticsResource> orderStatisticsResources = customerRepository.findOrderCountByMonth(username);
        List<BookStatisticsResource> bookStatisticsResources = customerRepository.findBookCountByMonth(username);
        List<TotalPriceStatisticsResource> totalPriceStatisticsResources = customerRepository.findTotalPriceByMonth(username);

        Map<Integer, StatisticsResource> statisticsResources = new HashMap<>();
        calculateOrderCount(orderStatisticsResources, statisticsResources);
        calculateBookCount(bookStatisticsResources, statisticsResources);
        createTotalPrice(totalPriceStatisticsResources, statisticsResources);

        LOGGER.info("Statistics for user {} finish performing", username);
        return statisticsResources.values();
    }

    private void createTotalPrice(List<TotalPriceStatisticsResource> totalPriceStatisticsResources, Map<Integer, StatisticsResource> statisticsResources) {
        for (TotalPriceStatisticsResource totalPriceStatisticsResource : totalPriceStatisticsResources) {
            String month = totalPriceStatisticsResource.getId();
            Integer monthIndex = Integer.parseInt(month);

            if (statisticsResources.get(monthIndex) == null) {
                statisticsResources.put(monthIndex, StatisticsResource.builder()
                        .month(Month.of(monthIndex).name())
                        .totalPrice(totalPriceStatisticsResource.getTotalPrice())
                        .build());
            } else {
                StatisticsResource statisticsResource = statisticsResources.get(monthIndex);
                if (statisticsResource.getTotalPrice() == null) {
                    statisticsResource.setTotalPrice(BigDecimal.valueOf(0.0));
                }
                statisticsResource.setTotalPrice(statisticsResource.getTotalPrice().add(totalPriceStatisticsResource.getTotalPrice()));
                statisticsResources.put(monthIndex, statisticsResource);
            }
        }
    }

    private void calculateBookCount(List<BookStatisticsResource> bookStatisticsResources, Map<Integer, StatisticsResource> statisticsResources) {
        Map<BookStatisticsIdResource, StatisticsResource> filter = new HashMap<>();
        for (BookStatisticsResource bookStatisticsResource : bookStatisticsResources) {
            BookStatisticsIdResource bookStatisticsIdResource = bookStatisticsResource.getId();
            if (filter.get(bookStatisticsIdResource) == null) {
                filter.put(bookStatisticsIdResource, StatisticsResource.builder()
                        .month(Month.of(bookStatisticsIdResource.getMonth()).name())
                        .totalBookCount(bookStatisticsResource.getTotalBookCount())
                        .build());
            } else {
                StatisticsResource statisticsResource = filter.get(bookStatisticsIdResource);
                statisticsResource.setTotalBookCount(statisticsResource.getTotalBookCount() + bookStatisticsResource.getTotalBookCount());
            }
        }

        for (Map.Entry<BookStatisticsIdResource, StatisticsResource> entry : filter.entrySet()) {
            if (statisticsResources.get(entry.getKey().getMonth()) == null) {
                statisticsResources.put(entry.getKey().getMonth(), StatisticsResource.builder()
                        .month(Month.of(entry.getKey().getMonth()).name())
                        .totalBookCount(entry.getValue().getTotalBookCount())
                        .build());
            } else {
                StatisticsResource statisticsResource = statisticsResources.get(entry.getKey().getMonth());
                statisticsResource.setTotalBookCount(statisticsResource.getTotalBookCount() + entry.getValue().getTotalBookCount());
            }
        }
    }

    private void calculateOrderCount(List<OrderStatisticsResource> orderStatisticsResources, Map<Integer, StatisticsResource> statisticsResources) {
        for (OrderStatisticsResource orderStatisticsResource : orderStatisticsResources) {
            String month = orderStatisticsResource.getId();
            Integer monthIndex = Integer.parseInt(month);

            if (statisticsResources.get(monthIndex) == null) {
                statisticsResources.put(monthIndex, StatisticsResource.builder()
                        .month(Month.of(monthIndex).name())
                        .totalOrderCount(orderStatisticsResource.getTotalOrderCount())
                        .build());
            } else {
                StatisticsResource statisticsResource = statisticsResources.get(monthIndex);
                statisticsResource.setTotalOrderCount(statisticsResource.getTotalOrderCount() + orderStatisticsResource.getTotalOrderCount());
                statisticsResources.put(monthIndex, statisticsResource);
            }
        }
    }
}

