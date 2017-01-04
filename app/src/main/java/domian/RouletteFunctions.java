package domian;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by icaro on 03/01/17.
 */

    public class RouletteFunctions {
        public RouletteFunctions() {
        }

        static String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();

            int cp;
            while((cp = rd.read()) != -1) {
                sb.append((char)cp);
            }

            return sb.toString();
        }

        static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            InputStream is = (new URL(url)).openStream();

            JSONObject var6;
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                jsonText = jsonText.replace("[", "");
                jsonText = jsonText.replace("]", "");
                JSONObject json = new JSONObject(jsonText);
                var6 = json;
            } finally {
                is.close();
            }

            return var6;
        }
    }
