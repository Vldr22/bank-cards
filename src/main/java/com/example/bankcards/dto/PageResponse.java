package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Постраничный ответ с пагинацией")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    @Schema(description = "Список элементов")
    private List<T> content;

    @Schema(description = "Номер текущей страницы", example = "0")
    private int pageNumber;

    @Schema(description = "Размер страницы", example = "20")
    private int pageSize;

    @Schema(description = "Общее количество элементов", example = "100")
    private long totalElements;

    @Schema(description = "Общее количество страниц", example = "5")
    private int totalPages;

    @Schema(description = "Последняя ли страница", example = "false")
    private boolean last;

    public static <T> PageResponse<T> of (Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

}
