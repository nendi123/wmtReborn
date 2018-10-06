package nendi.wmtreborn;

/**
 * Created by kseta on 3/30/18.
 */

public class Constant {
    public static final String my_shared_preferences = "my_shared_preferences";

    public final static String TAG_USERNAME = "username";
    public static final String session_status = "session_status";
    public static final String TAG_LOGIN = "loginUser";
    public static final String TAG_OPERATOR = "operator";
    public static final String TAG_PERUSAHAAN = "company";
    public static final String TAG_HEADUJI = "headuji";
    public static final String TAG_DETUJI = "detuji";
    public static final String TAG_JSON_OBJ = "json_obj_req";

    //TODO : rapihin ini jadi klo utk tag pindah obj pake class baru, dll panggilnya tinggal constant.somehting.. dst
    //contoh class lagi
    public class SOMETHING{

    }
    //TODO: ini masukin ke class util
    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
    }
}
