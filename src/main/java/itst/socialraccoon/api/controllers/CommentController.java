package itst.socialraccoon.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import itst.socialraccoon.api.services.CommentService;
import itst.socialraccoon.api.dtos.CommentDTO;
import itst.socialraccoon.api.models.CommentModel;
import itst.socialraccoon.api.models.PostModel;
import itst.socialraccoon.api.models.UserModel;
import itst.socialraccoon.api.services.PostService;
import itst.socialraccoon.api.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("comments")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Tag(name = "Comments", description = "Provides methods to manage comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/post/{postId}")
    @Operation(
            summary = "Create a comment for a post",
            description = "Create a new comment for a specific post in the database",
            requestBody = @RequestBody(
                    description = "Comment to be created",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CommentModel.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Comment",
                                    value = "{\n" +
                                            "  \"comment\": \"This is a comment\",\n" +
                                            "  \"user\": {\n" +
                                            "    \"idUser\": 1\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Comment created",
                    content = @Content(
                            schema = @Schema(implementation = CommentDTO.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Comment",
                                    value = "{\n" +
                                            "  \"idComment\": 1,\n" +
                                            "  \"idUser\": 1,\n" +
                                            "  \"idPost\": 1,\n" +
                                            "  \"comment\": \"This is a comment\",\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "User or Post not found",
                    content = @Content
            )
    })
    public ResponseEntity<CommentDTO> create(
            @PathVariable Integer postId,
            @org.springframework.web.bind.annotation.RequestBody CommentModel comment) {
        if (comment.getUser() == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        UserModel user = userService.findById(comment.getUser().getIdUser());
        PostModel post = postService.findById(postId);

        if (user == null || post == null) {
            throw new EntityNotFoundException("User or Post not found");
        }

        comment.setPost(post);
        CommentModel createdComment = commentService.save(comment);
        return new ResponseEntity<>(convertToDto(createdComment), HttpStatus.CREATED);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment", description = "Delete a comment by its id")
    public ResponseEntity<Void> delete(
            @PathVariable Integer commentId) {
        commentService.delete(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/post/{postId}", params = {"page", "pageSize"})
    @Operation(summary = "Get comments by post id with pagination", description = "Get all comments by post id with optional user filter and pagination")
    public ResponseEntity<List<CommentDTO>> findByPostId(
            @PathVariable Integer postId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        List<CommentModel> comments;
        if (userId != null) {
            comments = commentService.getCommentsByPostIdAndUserId(postId, userId, page, pageSize);
        } else {
            comments = commentService.getCommentsByPostId(postId, page, pageSize);
        }
        if (comments.isEmpty()) {
            throw new NoSuchElementException("No comments found for the given criteria.");
        }
        return new ResponseEntity<>(comments.stream().map(this::convertToDto).toList(), HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userId}", params = {"page", "pageSize"})
    @Operation(summary = "Get comments by user id with pagination", description = "Get all comments by user id with pagination")
    public ResponseEntity<List<CommentDTO>> findByUserId(
            @PathVariable Integer userId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        List<CommentModel> comments = commentService.getCommentsByUserId(userId, page, pageSize);
        if (comments.isEmpty()) {
            throw new NoSuchElementException("No comments found for the given user.");
        }
        return new ResponseEntity<>(comments.stream().map(this::convertToDto).toList(), HttpStatus.OK);
    }

    @PutMapping("/{commentId}")
    @Operation(
            summary = "Update comment",
            description = "Update a comment by its id",
            requestBody = @RequestBody(
                    description = "Comment to be updated",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CommentModel.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Comment",
                                    value = "{\n" +
                                            "  \"comment\": \"This is an updated comment\",\n" +
                                            "}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Comment updated",
                    content = @Content(
                            schema = @Schema(implementation = CommentDTO.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Comment",
                                    value = "{\n" +
                                            "  \"idComment\": 1,\n" +
                                            "  \"idUser\": 1,\n" +
                                            "  \"idPost\": 1,\n" +
                                            "  \"comment\": \"This is an updated comment\",\n" +
                                            "}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Comment not found",
                    content = @Content
            )
    })
    public ResponseEntity<CommentDTO> update(
            @PathVariable Integer commentId,
            @org.springframework.web.bind.annotation.RequestBody CommentModel comment) {
        CommentModel commentToUpdate = commentService.findById(commentId);
        commentToUpdate.setComment(comment.getComment());
        commentToUpdate.setDate(comment.getDate());
        CommentModel updatedComment = commentService.save(commentToUpdate);
        return new ResponseEntity<>(convertToDto(updatedComment), HttpStatus.OK);
    }

    public CommentDTO convertToDto(CommentModel comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }
}
