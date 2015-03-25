package superlifesize.com.stormy2;

import android.content.Context;
import android.graphics.Color;
import android.webkit.WebView;

import java.io.IOException;

/**
 * Created by nmilward on 3/6/15.
 */
public class GifWebView extends WebView {

    public static final String HTML_FORMAT = "<html><body style=\"text-align: center; vertical-align:right;background-color:transparent;\"><img src = \"%s\" /></body></html>";

    public GifWebView(Context context, String path) throws IOException {
        super(context);

        final String html = String.format(HTML_FORMAT, path);

        setBackgroundColor(Color.TRANSPARENT);
        loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

    }
}
