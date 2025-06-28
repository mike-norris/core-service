package com.openrangelabs.services.user.profile;

import com.openrangelabs.services.user.profile.dao.UserBloxopsDAO;
import com.openrangelabs.services.user.profile.model.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

@Slf4j
@Service
public class ProfileService {
    @Value("${userProfileImageLocation}")
    String path;
    UserBloxopsDAO userBloxopsDAO;

    @Autowired
    public ProfileService(UserBloxopsDAO userBloxopsDAO) {
        this.userBloxopsDAO = userBloxopsDAO;
    }

    public UserProfile retrieveUserProfile(int id, String email) throws Exception {
        UserProfile userProfile = userBloxopsDAO.getUserProfile(id);
        if (null == userProfile) {
            userBloxopsDAO.createUserProfile(id, email, false);
            userProfile = userBloxopsDAO.getUserProfile(id);
        }
        String filename = userProfile.getUserImageFilename();

        if (null == filename) {
            filename = "";
        }

        if(!filename.isEmpty()) {
            try {
                byte[] file = readUserProfileImageFile(filename);
                userProfile.setUserImage(file);
            } catch(Exception e) {
                log.info(e.getMessage());
            }
        }

        if(null != userProfile) {
            return userProfile;
        }

        userBloxopsDAO.createUserProfile(id, email, false);
        return userBloxopsDAO.getUserProfile(id);
    }

    public void updateUserProfileImage(int id, int imageId) {
        userBloxopsDAO.updateUserProfileImage(id,imageId);
    }

    public void updateUserProfileEmail(int id, String email) {
        userBloxopsDAO.updateUserProfileEmail(id, email);
    }

    public void saveUserProfileFilename(int id ,String filename) {
        userBloxopsDAO.updateUserProfileImageFilename(id,filename);
    }
    public void writeUserProfileImageFile(String filename, byte[] fileBytes) {
        try {
            ByteArrayInputStream image = new ByteArrayInputStream(fileBytes);
            BufferedImage bufferedImage = ImageIO.read(image);
            ImageIO.write(bufferedImage, "jpg", new File(path,filename) );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public byte[] readUserProfileImageFile(String filename) throws IOException {
        BufferedImage userImage = ImageIO.read(new File(path+"/"+filename));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(userImage, "jpg", outputStream );
        byte [] file = outputStream.toByteArray();
        return file;
    }
    public boolean deletePreviousImage(String filename)  {
        File userImage = new File(path+"/"+filename);

        if(userImage.delete())
        {
            return true;
        }
        else
        {
            return false;
        }

    }
    public void deleteUserProfileFilename(int id ) {
        userBloxopsDAO.updateUserProfileImageFilename(id,null);
    }

}
