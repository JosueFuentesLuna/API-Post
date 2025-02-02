package itst.socialraccoon.api.services;

import itst.socialraccoon.api.models.CommentModel;
import itst.socialraccoon.api.repositories.CommentRepository;
import jakarta.transaction.Transactional;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public CommentModel save(CommentModel comment) {
        return commentRepository.save(comment);
    }

    public CommentModel delete(Integer id){
        CommentModel comment = commentRepository.findById(id).get();
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found");
        }
        commentRepository.deleteById(id);
        return comment;
    }
    public CommentModel findById(Integer id) {
        return commentRepository.findById(id).get();
    }

    public List<CommentModel> getCommentsByPostId(Integer postId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<CommentModel> commentPage = commentRepository.getCommentsByPostId(postId, pageRequest);
        return commentPage.getContent();
    }

    public List<CommentModel> getCommentsByUserId(Integer userId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<CommentModel> commentPage = commentRepository.getCommentsByUserId(userId, pageRequest);
        return commentPage.getContent();
    }

    public List<CommentModel> getCommentsByPostIdAndUserId(Integer postId, Integer userId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<CommentModel> commentPage = commentRepository.getCommentsByPostIdAndUserId(postId, userId, pageRequest);
        return commentPage.getContent();
    }

    public CommentModel getCommentByPostIdAndUserIdAndCommentId(Integer postId, Integer userId, Integer commentId) {
        return commentRepository.getComment(postId, userId, commentId);
    }
}