package yoon.capstone.application.service.manager.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.common.enums.Category;
import yoon.capstone.application.service.manager.ProfileManager;

@Service
@Primary
@RequiredArgsConstructor
public class MockProfileManager implements ProfileManager {
    @Override
    public String updateProfile(MultipartFile file, long memberIndex) {
        return file.getName();
    }

    @Override
    public String updateProject(MultipartFile file, Category category) {
        if(file == null)return "file";
        return file.getName();
    }

    @Override
    public void deleteImage(String name) {

    }
}
