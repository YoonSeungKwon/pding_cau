package yoon.capstone.application.service.manager.stub;

import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.common.enums.Category;
import yoon.capstone.application.service.manager.ProfileManager;

public class StubProfileManager implements ProfileManager {
    @Override
    public String updateProfile(MultipartFile file, long memberIndex) {
        return null;
    }

    @Override
    public String updateProject(MultipartFile file, Category category) {
        return null;
    }

    @Override
    public void deleteImage(String name) {

    }
}
