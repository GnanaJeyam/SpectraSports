package com.spectra.sports.constant;

public final class SuccessOrErrorMessages {
    // Success Messages
    public static final String INVALID_USER = "Invalid User. Please try again";
    public static final String SIGN_UP_SUCCESS_FUL = "Sign up SuccessFul";
    public static final String USER_UPDATED = "User Updated SuccessFully";
    public static final String IMAGE_UPLOADED_SUCCESSFULLY = "Image uploaded Successfully";
    public static final String STUDENT_ATTENDANCE_DETAILS_ARE_UPDATED = "Student attendance details are updated";
    public static final String GET_BY_USER_ID = "Get by User Id";
    public static final String GET_MENTOR_DETAIL_BY_MENTOR_ID_WITH_MAPPED_FLAG = "Get Mentor Detail by mentor id with mapped flag";
    public static final String GET_ACADEMY_DETAIL_BY_ACADEMY_ID_WITH_MAPPED_FLAG = "Get Academy Detail by academy id with mapped flag";
    public static final String GET_ALL_NEARBY_MENTORS = "Get All Nearby Mentors";
    public static final String GET_ALL_STUDENTS_BY_MENTOR_ID = "Get All Students By Mentor Id";
    public static final String GET_ALL_ACADEMY_WITH_MAPPED_KEY = "Get All Academy with mapped key";
    public static final String PASSWORD_UPDATED = "Password Updated";
    public static final String USER_MAPPING_ADDED = "User Mapping Added";
    public static final String GET_ALL_MENTORS = "Get All Mentors";
    public static final String GET_ALL_STUDENTS_ATTENDANCE_DETAIL_BY_MENTOR_ID = "Get All students attendance detail by mentor id";
    public static final String GET_ALL_STUDENTS_ATTENDANCE_DETAIL = "Get All students attendance details";
    public static final String GET_STUDENT_ATTENDANCE_DETAIL_BY_ID = "Get student attendance detail by id";
    public static final String GET_ALL_MENTORS_BY_ACADEMY_ID = "Get All Mentors By Academy ID";
    public static final String GET_ALL_ACADEMY_AND_MENTORS_BY_STUDENT = "Get All Academy and Mentors By Student";
    public static final String GET_ALL_USERS_BY_SEARCH = "Get All Users by search";
    public static final String VERIFICATION_EMAIL_SENT = "Verification Email Sent";
    public static final String VALID_OTP = "Valid Otp";
    public static final String OTP_SENT_TO_THE_USER = "Otp Sent to the User";
    public static final String SIGN_IN_SUCCEED = "Sign in Succeed";
    public static final String VERIFIED_MESSAGE =  """
            <html>
            <head>
            <title> SpectraSports </title>
            </head>
            <body>
            <b> Congratulations, %s! Your account has been verified </b>
            </body>
            </html
    """;

    // Email Templates
    public static final String OTP_MESSAGE = """
                <html>
                <body>
                <h5>Hello %s </h5>
                <p>Please find your OTP below.</p>
                <p style=\"font-size:40px\"> %s </p>
                </body>   
                </html>
    """;
    public static String OTP_SUBJECT = "Welcome back to SpectraSports";
    public static String SIGNUP_SUBJECT = "Welcome to SpectraSports, %s";
    public static String SIGNUP_CONTENT = """
            <html>
            <body>
            <p> Please click this <a href=\"%s\">link</a> to verify</p>
            </body>
            </html>
    """;
    public static String SUBSCRIPTION_SUBJECT = "New Subscription Mapped to you";
    public static String SUBSCRIPTION_CONTENT = """
            <html>
            <body>
            <p> %s </p>
            </body>
            </html>
    """;


    // Error Messages
    public static final String USER_CANNOT_BE_NULL = "User Cannot be null";
    public static final String STUDENT_RATING_DETAIL_ID_CANT_BE_NULL = "Student Rating Detail Id cannot be null.";
    public static final String DUPLICATE_USER = "Duplicate User, Please try with a different email or Phone Number";
    public static final String USER_NOT_FOUND = "User Not Found";
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";
    public static final String USER_NOT_VERIFIED_YET = "User not verified yet.";
    public static final String INVALID_USERNAME_OR_EMAIL = "Invalid Username or Email.";
    public static final String INVALID_OTP = "Invalid otp";
    public static final String ROLE_CANNOT_BE_NULL = "Role cannot be null";
    public static final String SOMETHING_WENT_WRONG_WHILE_GENERATING_THE_TOKEN = "Something went wrong while generating the token";
}
