package org.example.wyspring.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 课程视图对象
 */
@Data
public class CourseVO {

    private Long id;

    private String title;

    private String subtitle;

    private String coverImage;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer episodesCount;

    private Integer studentsCount;

    private Integer categoryId;

    private String categoryName;

    /**
     * 讲师信息
     */
    private TeacherVO teacher;

    /**
     * 章节列表
     */
    private List<ChapterVO> chapters;

    @Data
    public static class TeacherVO {
        private Long id;
        private String name;
        private String avatar;
        private String title;
    }

    @Data
    public static class ChapterVO {
        private Long id;
        private String title;
        private Integer sortOrder;
        private String videoUrl;
        private Integer duration;
    }
}
