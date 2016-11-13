package net.frozenbit.plugin5zig.cubecraft.commands.stalker;

import com.google.gson.reflect.TypeToken;
import net.frozenbit.plugin5zig.cubecraft.Util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class MojangResponseHandler implements ResponseHandler<List<UserData>> {
    private static final TypeToken<List<UserData>> RESPONSE_TYPE = new TypeToken<List<UserData>>() {
    };

    @Override
    public List<UserData> handleResponse(HttpResponse response) throws IOException {
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
        String jsonResponseStr = EntityUtils.toString(entity);
        return Util.GSON.fromJson(jsonResponseStr, RESPONSE_TYPE.getType());
    }

}
