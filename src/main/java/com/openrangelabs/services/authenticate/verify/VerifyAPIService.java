package com.openrangelabs.services.authenticate.verify;

import com.twilio.Twilio;
import com.twilio.exception.AuthenticationException;
import com.twilio.rest.lookups.v1.PhoneNumber;
import com.twilio.rest.verify.v2.service.Verification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class VerifyAPIService {

    @Value("${twilio.accountSID}")
    String accountSID;

    @Value("${twilio.serviceSID}")
    String serviceSID;

    @Value("${twilio.authtoken}")
    String authToken;

    public boolean initialise() {
        try {
            log.info("Initialising Twilio Verify.");
            Twilio.init(accountSID, authToken);
            Twilio.getRestClient();
            return true;
        }
        catch (AuthenticationException e) {
            log.info("Unable to initialise Twilio Verify");
            return false;
        }
    }

    public boolean sendVerifyCode(String to , String channel){
        initialise();
        log.info("Sending verify code to " + to + " by " + channel);
        Verification verification = Verification.creator(
                serviceSID,
                to,
                channel)
                .create();
        if (!verification.getStatus().equals("pending")) {
            log.info("Unable to send verify code to " + to + " by " + channel);
            return false;
        }else{
            log.info("Sent verify code to " + to + " by " + channel);
            return true;
        }
    }

    public String formatPhoneNumber(String mobileNumber){
        try{
            initialise();
            log.info("Formatting phone number for verify sms code.");
            PhoneNumber phoneNumber = PhoneNumber.fetcher(new com.twilio.type.PhoneNumber(mobileNumber)).setCountryCode("US").fetch();
            return phoneNumber.getPhoneNumber().toString();
        }catch(Exception e){
            log.info("Phone number invalid for verify sms code.");
            log.error(e.getMessage());
            return null;
        }
    }
}
