package org.example.wyspring.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.wyspring.entity.Comment;

import java.util.List;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 分页查询评论列表（顶层评论）
     */
    @Select("SELECT c.*, u.nickname as user_nickname, u.avatar as user_avatar " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE c.target_type = #{targetType} AND c.target_id = #{targetId} " +
            "AND c.parent_id = 0 AND c.status = 1 AND c.is_deleted = 0 " +
            "ORDER BY c.created_at DESC")
    IPage<Comment> selectTopLevelPage(Page<Comment> page,
                                      @Param("targetType") Integer targetType,
                                      @Param("targetId") Long targetId);

    /**
     * 查询评论的回复列表
     */
    @Select("SELECT c.*, u.nickname as user_nickname, u.avatar as user_avatar " +
            "FROM comments c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE c.parent_id = #{parentId} AND c.status = 1 AND c.is_deleted = 0 " +
            "ORDER BY c.created_at")
    List<Comment> selectRepliesByParentId(@Param("parentId") Long parentId);

    /**
     * 增加点赞数
     */
    @Update("UPDATE comments SET likes = likes + 1 WHERE id = #{id}")
    int incrementLikes(@Param("id") Long id);

    /**
     * 查询目标的评论数
     */
    @Select("SELECT COUNT(*) FROM comments " +
            "WHERE target_type = #{targetType} AND target_id = #{targetId} " +
            "AND status = 1 AND is_deleted = 0")
    int countByTarget(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);
}
