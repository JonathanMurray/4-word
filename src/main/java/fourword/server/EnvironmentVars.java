package fourword.server;

/**
 * Created by jonathan on 2015-07-01.
 */
public class EnvironmentVars {

    public static String homeDir(){
        return getEnvVar("FOUR_WORD_HOME", ".");
    }

    public static int serverPort(){
        return Integer.parseInt(getEnvVar("FOUR_WORD_SERVER_PORT", "4444"));
    }

    public static String getEnvVar(String name, String defaultVal){
        String val = System.getenv(name);
        if(val != null){
            return val;
        }
        System.out.println("getEnvVar(" + name + ") gave null. Returning default val '" + defaultVal + "'");
        return defaultVal;
    }

}
