package org.mvasylchuk.pfcc.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Page<T> {
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private Integer totalElements;
    private List<T> data;

}
