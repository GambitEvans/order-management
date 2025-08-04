package com.example.order_management.util;

import com.example.order_management.dto.DateRangeDTO;

import java.time.LocalDate;
import java.util.Optional;

public final class DateUtil {
    private DateUtil() {
    
    }
    
    public static DateRangeDTO normalizeDateRange(LocalDate start, LocalDate end) {
        var today = LocalDate.now();
        
        var startDate = Optional.ofNullable(start).orElse(today);
        var endDate = Optional.ofNullable(end).orElse(today);
        
        if(startDate.isAfter(endDate)) throw new IllegalArgumentException("Data inicial não pode ser depois da data final");
        
        return new DateRangeDTO(startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay().minusNanos(1));
    }
}
