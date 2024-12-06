package yoon.capstone.application.service.manager;

import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.common.enums.Category;

public interface ProfileManager {

    String updateProfile(MultipartFile file, long memberIndex);

    String updateProject(MultipartFile file, Category category);

    void deleteImage(String name);

}
