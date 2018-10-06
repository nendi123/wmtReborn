package nendi.wmtreborn.model;

/**
 * Created by eWork on 2/15/2018.
 */

public class login {
    private static String userID;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
