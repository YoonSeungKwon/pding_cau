package yoon.capstone.application.service.manager;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.common.enums.Category;

@Service
@Primary
@RequiredArgsConstructor
public class MockProfileManager implements ProfileManager{
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
