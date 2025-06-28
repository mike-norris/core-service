package com.openrangelabs.services.tools;

import com.openrangelabs.middleware.OrlCommon;

public class Commons extends OrlCommon {

    public static final String CMDB_CONTACT_EXCHANGE = "x.cmdb-contact";
    public static final String CMDB_CONTACT_DLX_EXCHANGE = "x.cmdb-contact-dlx";
    public static final String CMDB_CONTACT_QUEUE = "q.cmdb-contact";
    public static final String CMDB_CONTACT_DLQ_QUEUE = "q.cmdb-contact-dlq";

    public static final String SUPPORT_EXCHANGE = "x.support";
    public static final String SUPPORT_DLX_EXCHANGE = "x.support-dlx";
    public static final String SUPPORT_UPDATE = "q.support-update";
    public static final String SUPPORT_UPDATE_DLQ = "q.support-update-dlq";

    public static final String SIGNNOW_EXCHANGE = "x.signnow-documents";
    public static final String SIGNNOW_DLX_EXCHANGE = "x.signnow-documents-dlq";
    public static final String ROSTER_DOCUMENTS_QUEUE = "q.roster-user-documents";
    public static final String ROSTER_USER_DOCUMENT_DLQ_QUEUE = "q.roster-user-documents-dlq";

    //email
    public static final String MESSAGING_EXCHANGE = "x.messaging";
    public static final String MESSAGING_EXCHANGE_DLX = "x.messaging-dlx";
    public static final String EMAIL_QUEUE = "q.email";
    public static final String EMAIL_QUEUE_DLQ = "q.email-dlq";

    Commons() { super(); }

    public static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String usernameGenerator(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First and last name cannot be null");
        }

        firstName = firstName.toLowerCase();
        lastName = lastName.toLowerCase();

        int maxLength = 20; // Maximum allowed length
        int availableLength = maxLength - 1; // Account for the period (.)

        // Find the best balance for firstName and lastName
        int firstMax = availableLength / 2;  // Half of available length
        int lastMax = availableLength - firstMax; // Remaining length for lastName

        // Trim names if needed
        String firstPart = firstName.length() > firstMax ? firstName.substring(0, firstMax) : firstName;
        String lastPart = lastName.length() > lastMax ? lastName.substring(0, lastMax) : lastName;

        return firstPart + "." + lastPart;
    }
}
