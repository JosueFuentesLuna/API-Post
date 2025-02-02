package itst.socialraccoon.api.models.compositekeys;

import itst.socialraccoon.api.models.PostModel;
import itst.socialraccoon.api.models.UserModel;

import java.io.Serializable;

public class ReactionPK implements Serializable {
    private UserModel idUser;
    private PostModel idPost;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ReactionPK reactionPK))
            return false;
        return idUser.getIdUser() == reactionPK.idUser.getIdUser() && idPost.getIdPost() == reactionPK.idPost.getIdPost();
    }

    @Override
    public int hashCode() {
        return idUser.getIdUser() + idPost.getIdPost();
    }

    public UserModel getIdUser() {
        return idUser;
    }

    public PostModel getIdPost() {
        return idPost;
    }

    public ReactionPK(UserModel idUser, PostModel idPost) {
        this.idUser = idUser;
        this.idPost = idPost;
    }

    public ReactionPK() {
    }
}
