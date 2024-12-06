package yoon.capstone.application.service.manager;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yoon.capstone.application.common.enums.Category;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.ProjectException;
import yoon.capstone.application.common.exception.UtilException;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ProfileManager implements ProfileManager {

    private final AmazonS3Client amazonS3Client;
    private final String bucket = "cau-artech-capstone";;

    private final String region = "ap-northeast-2";

    @Override
    public String updateProfile(MultipartFile file, long memberIndex){

        String url;
        UUID uuid = UUID.randomUUID();

        if (!file.getContentType().startsWith("image")) {
            throw new UtilException(ExceptionCode.NOT_IMAGE_FORMAT);
        }
        try {
            String fileName = uuid + file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/members/" + memberIndex + "/" + fileName;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            System.out.println(file.getContentType());
            url = fileUrl;
            amazonS3Client.putObject(bucket +"/members/" + memberIndex, fileName, file.getInputStream(), objectMetadata);
        } catch (Exception e){
            e.printStackTrace();
            throw new ProjectException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }

        return url;
    }

    @Override
    public String updateProject(MultipartFile file, Category category) {
        String url;
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
            throw new UtilException(ExceptionCode.NOT_IMAGE_FORMAT);
        }
        UUID uuid = UUID.randomUUID();
        try {
            String fileName = uuid + file.getOriginalFilename();
            String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/projects/"+fileName;
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            System.out.println(file.getContentType());
            url = fileUrl;
            amazonS3Client.putObject(bucket +"/projects", fileName, file.getInputStream(), objectMetadata);
        } catch (Exception e){
            throw new ProjectException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
        return url;
    }

    @Override
    public void deleteImage(String fileName) {
        try{
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket + "/projects", fileName.substring(fileName.indexOf("/projects/")+10)));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
