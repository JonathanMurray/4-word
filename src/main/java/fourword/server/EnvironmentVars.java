package fourword.server;

/**
 * Created by jonathan on 2015-07-01.
 */
public class EnvironmentVars {

    public static String homeDir(){
        return getEnvVar("FOUR_WORD_HOME", ".");
    }

    public static int serverPort(){

        String val = System.getenv("FOUR_WORD_SERVER_PORT");
        if(val == null){
            val = System.getenv("PORT");
            if(val == null){
                val = "4444";
            }
        }
        return Integer.parseInt(val);
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
