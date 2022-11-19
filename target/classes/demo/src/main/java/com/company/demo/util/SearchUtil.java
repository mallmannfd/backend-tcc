package com.company.demo.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.company.demo.util.dto.PageQueryDTO;

public class SearchUtil {

    private SearchUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Pageable toPageable(PageQueryDTO pageQuery) {
        var size = pageQuery.getSize();
        if (size == 0) {
            size = Integer.MAX_VALUE;
        }
        return PageRequest.of(pageQuery.getPage(), size, toSort(pageQuery.getSort()));
    }

    public static Sort toSort(List<String> sort) {
        var sortObj = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            var orders = new ArrayList<Order>();
            for (var item : sort) {
                if (item.contains(":")) {
                    var split = item.split(":");
                    orders.add(new Order(Direction.fromOptionalString(split[1]).orElse(Direction.ASC), split[0]));
                } else {
                    orders.add(new Order(Direction.ASC, item));
                }
            }
            sortObj = Sort.by(orders);
        }
        return sortObj;
    }
}
