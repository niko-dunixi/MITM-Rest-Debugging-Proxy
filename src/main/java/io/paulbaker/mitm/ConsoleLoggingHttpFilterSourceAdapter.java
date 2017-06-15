package io.paulbaker.mitm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created by paul.baker on 6/15/17.
 */
public class ConsoleLoggingHttpFilterSourceAdapter extends HttpFiltersSourceAdapter {

    private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");

    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext context) {
        System.out.println("-----------------------------------------------------");
        HttpMethod method = originalRequest.method();
        String originalUri = originalRequest.uri();

        // Allow the client to connect to the server the first time.
        if (HttpMethod.CONNECT.equals(method) && Objects.nonNull(context) && originalUri.endsWith(":443")) {
            String url = "https://" + originalUri.replaceFirst(":443$", "");
            System.out.println("(Manipulating connection request for successful HTTPS: " + originalUri + " -> " + url + ")");
            context.channel().attr(CONNECTED_URL).set(url);
            originalRequest.setUri(url);
        }
        return new HttpFiltersAdapter(originalRequest) {

            @Override
            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                if (httpObject instanceof HttpRequest) {
                    HttpRequest request = (HttpRequest) httpObject;
                    System.out.println("CLIENT " + request.method() + " -> " + request.uri());
                } else if (httpObject instanceof LastHttpContent) {
                    LastHttpContent copy = ((LastHttpContent) httpObject).copy();
                    String body = copy.content().toString(StandardCharsets.UTF_8);
                    System.out.println("-- Content:" + body);
                } else {
                    System.err.println(httpObject.getClass());
                }
                // Returning null is fine. It indicates to the proxy-server that it
                // needs to make that connection for us.
                return null;
            }

            @Override
            public HttpObject serverToProxyResponse(HttpObject httpObject) {
                if (httpObject instanceof HttpResponse) {
                    HttpResponse response = (HttpResponse) httpObject;
                    System.out.println("SERVER RESPONSE");
                    System.out.println("-- Status: " + response.getStatus());
                    response.headers().forEach(entry -> {
                        System.out.println("-- " + entry.getKey() + ":" + entry.getValue());
                    });
                } else if (httpObject instanceof LastHttpContent) {
                    LastHttpContent response = (LastHttpContent) httpObject;
                    ByteBuf content = response.content();
                    String outputString = "-- Content:" + content.toString(StandardCharsets.UTF_8);
                    System.out.println(outputString);
                } else {
                    System.err.println(httpObject.getClass());
                }
                return httpObject;
            }
        };
    }
}
