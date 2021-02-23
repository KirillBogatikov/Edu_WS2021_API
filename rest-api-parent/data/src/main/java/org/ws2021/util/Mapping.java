package org.ws2021.util;

import java.util.ArrayList;
import java.util.List;

import org.ws2021.sql.ModelMapper;

public class Mapping {
    public static <T> ModelMapper<List<T>> listOf(ModelMapper<T> mapper) {
        return (r) -> {
            ArrayList<T> list = new ArrayList<>();

            while (r.next()) {
                list.add(mapper.map(r));
            }

            return list;
        };
    }
}
