package itst.social_raccoon_api.services;

import itst.social_raccoon_api.models.ImageProfileModel;
import itst.social_raccoon_api.models.ProfileModel;
import itst.social_raccoon_api.models.UserModel;
import itst.social_raccoon_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private ImageProfileService imageProfileService;

    @Autowired
    @Lazy
    private ReactionService reactionService;

    @Autowired
    private ProfileService profileService;

    private final String defaultProfileImageUrl = "/uploads/default-profile.png";

    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    public UserModel findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public UserModel save(UserModel user) {
        ProfileModel profile = user.getProfile();
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found");
        }
        ImageProfileModel defaultImageProfile = new ImageProfileModel();
        defaultImageProfile.setProfile(profile);
        defaultImageProfile.setImageUrl(defaultProfileImageUrl);
        defaultImageProfile.setImageThumbnailUrl(defaultProfileImageUrl);
        imageProfileService.save(defaultImageProfile);
        profile.setImages(Set.of(defaultImageProfile));
        user.setProfile(profile);
        UserModel newUser = userRepository.save(user);
        return newUser;
    }

    @Transactional
    public UserModel save(UserModel user, MultipartFile profileImage) {
        try {
            ProfileModel profile = user.getProfile();
            if (profile == null) {
                throw new IllegalArgumentException("Profile not found");
            }
            String imageUrl = imageStorageService.storeImage(profileImage);
            ImageProfileModel defaultImageProfile = new ImageProfileModel();
            defaultImageProfile.setProfile(profile);
            defaultImageProfile.setImageUrl(imageUrl);
            defaultImageProfile.setImageThumbnailUrl(imageUrl);
            profile.setImages(Set.of(defaultImageProfile));
            user.setProfile(profile);
            UserModel newUser = userRepository.save(user);
            return newUser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean deleteProfileImage(Integer userId) {
        UserModel user = findById(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found");
        }
        ImageProfileModel imageProfile = imageProfileService.getImageProfileByUserId(userId);
        if (imageProfile.getImageUrl().equals(defaultProfileImageUrl)) {
            throw new IllegalArgumentException("The user has the default profile image");
        }
        imageProfile.setImageUrl(defaultProfileImageUrl);
        imageProfile.setImageThumbnailUrl(defaultProfileImageUrl);
        imageProfileService.update(imageProfile);
        return true;
    }


    @Transactional
    public void deleteUser(Integer userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        reactionService.deleteByUserId(user);
        userRepository.delete(user);
    }

    public void deleteById(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }
        userRepository.deleteById(id);
    }
}