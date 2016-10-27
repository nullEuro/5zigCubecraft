package net.frozenbit.plugin5zig.cubecraft.commands.bancheck;

import eu.the5zig.mod.The5zigAPI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Http response handler that scrapes the appeal page of a user for his past punishments.
 * <p>
 * Here be dragons.
 */
public class BanResponseHandler implements ResponseHandler<List<Punishment>> {
    private static final Pattern PUNISHMENT_PATTERN
            = Pattern.compile("<strong>(BANNED|MUTED)</strong>" +
                              " by (.+?) at (.+?) for (.+?) (?:http|<|-).+?fa fa-fw fa-([^\"]+)");

    @Override
    public List<Punishment> handleResponse(HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        List<Punishment> punishments = new ArrayList<>();
        String html = EntityUtils.toString(entity).replaceAll("\\s+", " ");
        if (html.matches(".+has never joined the server.+")) {
            return null;
        }
        Matcher matcher = PUNISHMENT_PATTERN.matcher(html);
        while (matcher.find()) {
            Punishment.Type type = Punishment.Type.valueOf(matcher.group(1));
            Date date = null;
            try {
                date = new SimpleDateFormat("MMM d, yyyy K:mm:ss a", Locale.US).parse(matcher.group(3));
            } catch (ParseException e) {
                The5zigAPI.getLogger().warn("invalid date " + matcher.group(3), e);
            }
            String icon = matcher.group(5);
            Punishment.State state;
            switch (icon) {
                case "pencil-square-o":
                    state = Punishment.State.ACTIVE;
                    break;
                case "ban":
                    state = Punishment.State.APPEAL_DENIED;
                    break;
                case "exclamation-triangle":
                    state = Punishment.State.EXPIRED;
                    break;
                case "check":
                    state = Punishment.State.APPEALED;
                    break;
                case "clock-o":
                    state = Punishment.State.APPEAL_WAITING;
                    break;
                default:
                    state = null;
            }
            punishments.add(new Punishment(type, date, matcher.group(4), state));
        }

        return punishments;
    }
}
