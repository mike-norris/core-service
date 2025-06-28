package com.openrangelabs.services.user.profile.model;
import lombok.Data;

@Data
public class UserProfile {
    String twoFactorProvider;
    String dashboardCardOrder;
    String grantorEmailAddress;
    String secondFactorMethod;
    int id;
    String emailAddress;
    String profileImage;
    String timeZone;
    String userImageFilename;
    byte[] userImage;
    boolean secondFactorEnabled;
    boolean sharedUser;
    String firstName;
    String lastName;
}
