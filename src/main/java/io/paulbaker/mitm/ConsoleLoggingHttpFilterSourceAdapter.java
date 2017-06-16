package io.paulbaker.mitm;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

/**
 * Created by paul.baker on 6/15/17.
 */
public class ConsoleLoggingHttpFilterSourceAdapter extends HttpFiltersSourceAdapter {

    private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");

    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext context) {
        return new ConsoleLoggingHttpFiltersAdapter(originalRequest, context);
    }

    private class ConsoleLoggingHttpFiltersAdapter extends HttpFiltersAdapter {

        public ConsoleLoggingHttpFiltersAdapter(HttpRequest originalRequest, ChannelHandlerContext context) {
            super(originalRequest, context);
            System.out.println(new Timestamp(System.currentTimeMillis()) + "-----------------------------------------------------");
        }

        @Override
        public HttpResponse clientToProxyRequest(HttpObject httpObject) {
            if (httpObject instanceof FullHttpRequest) {
                FullHttpRequest clientRequest = (FullHttpRequest) httpObject;
                System.out.println("CLIENT " + clientRequest.method() + " -> " + clientRequest.uri());
                clientRequest.headers().forEach(entry -> System.out.println("-- " + entry.getKey() + ":" + entry.getValue()));
                String body = clientRequest.content().toString(StandardCharsets.UTF_8);
                System.out.println("-- Content:" + body);
            } else if (httpObject instanceof HttpRequest) {
                HttpRequest clientRequest = (HttpRequest) httpObject;
                System.out.println("CLIENT " + clientRequest.method() + " -> " + clientRequest.uri());
                clientRequest.headers().forEach(entry -> System.out.println("-- " + entry.getKey() + ":" + entry.getValue()));
            } else if (httpObject instanceof HttpContent) {
                HttpContent clientRequest = ((HttpContent) httpObject).copy();
                String body = clientRequest.content().toString(StandardCharsets.UTF_8);
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
                HttpResponse serverResponse = (HttpResponse) httpObject;
                System.out.println("SERVER RESPONSE");
                System.out.println("-- Status: " + serverResponse.status());
                serverResponse.headers().forEach(entry -> System.out.println("-- " + entry.getKey() + ":" + entry.getValue()));
            } else if (httpObject instanceof HttpContent) {
                HttpContent serverResponse = (HttpContent) httpObject;
                String content = serverResponse.content().toString(StandardCharsets.UTF_8);
                System.out.println("-- Content:" + content);
            } else {
                System.err.println(httpObject.getClass());
            }
            return httpObject;
        }
    }
}
