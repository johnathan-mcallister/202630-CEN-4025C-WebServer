/**
 * Author: Johnathan McAllister (McAdmin)
 * Date: 2026-07-02
 * Course:
 * Professor:
 * <p>
 * Purpose:
 * -
 * <p>
 * Constraints:
 * -
 */

package com.app.webserver.util;

import org.mindrot.jbcrypt.BCrypt;

public class PassUtil {

    public static String hash(String pswd) {
        return BCrypt.hashpw(pswd, BCrypt.gensalt());
    }

    public static boolean verify(String pswd, String hash) {
        return BCrypt.checkpw(pswd, hash);
    }

}
