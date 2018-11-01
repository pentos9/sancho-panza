package com.spacex.panza.http;

import com.spacex.panza.util.PrintUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private HttpRequest request;

    private boolean readingChunks;

    private HttpData partialContent;

    private final StringBuilder responseContent = new StringBuilder();

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);

    private HttpPostRequestDecoder decoder;

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            URI uri = new URI(request.uri());
            if (!uri.getPath().startsWith("/form")) {
                writeMenu(channelHandlerContext);
                return;
            }

            responseContent.setLength(0);
            responseContent.append("Welcome To the Sancho Panza Web Server\r\n");
            responseContent.append("======");
            responseContent.append("Version" + request.protocolVersion().text() + "\r\n");
            responseContent.append("REQUEST_URI:" + request.uri() + "\r\n\r\n");

            responseContent.append("\r\n\r\n");

            for (Map.Entry<String, String> entry : request.headers()) {
                responseContent.append("HEADER:" + entry.getKey() + "=" + entry.getValue() + "\r\n");
            }

            responseContent.append("\r\n\r\n");

            Set<Cookie> cookies = null;
            String value = request.headers().get(HttpHeaderNames.COOKIE);
            if (value == null) {
                cookies = Collections.emptySet();
            } else {
                cookies = ServerCookieDecoder.STRICT.decode(value);
            }

            for (Cookie cookie : cookies) {
                responseContent.append("COOKIE:" + cookie + "\r\n");
            }

            responseContent.append("\r\n");

            QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());
            Map<String, List<String>> uriAttributes = decoderQuery.parameters();
            for (Map.Entry<String, List<String>> entry : uriAttributes.entrySet()) {
                for (String attrVal : entry.getValue()) {
                    responseContent.append("URI:" + entry.getKey() + "=" + attrVal + "\r\n");
                }
            }

            responseContent.append("\r\n\r\n");

            if (request.method().equals(HttpMethod.GET)) {
                responseContent.append("\r\n\r\nEND OF GET CONTENT\r\n");
                return;
            }

            try {
                decoder = new HttpPostRequestDecoder(factory, request);

            } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
                handleException(channelHandlerContext, e);
            }

            readingChunks = HttpUtil.isTransferEncodingChunked(request);
            responseContent.append("Is Chunked: " + readingChunks + "\r\n");
            responseContent.append("IsMultipart: " + decoder.isMultipart() + "\r\n");

            if (readingChunks) {
                responseContent.append("Chunks:");
                readingChunks = true;
            }

        }

        // check if the decoder was constructed before

        if (decoder == null) {
            if (msg instanceof HttpContent) {
                HttpContent chunk = (HttpContent) msg;
                try {
                    decoder.offer(chunk);
                } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
                    handleException(channelHandlerContext, e);
                    return;
                }

                responseContent.append('o');
                readHttpDataChunkByChunk();
                if (chunk instanceof LastHttpContent) {
                    writeResponse(channelHandlerContext.channel());
                    readingChunks = false;
                    reset();
                }

            }
        } else {
            writeResponse(channelHandlerContext.channel());
        }
    }

    private void handleException(ChannelHandlerContext channelHandlerContext, HttpPostRequestDecoder.ErrorDataDecoderException e) {
        e.printStackTrace();
        responseContent.append(e.getMessage());
        writeResponse(channelHandlerContext.channel());
        channelHandlerContext.channel().close();
        return;
    }

    private void reset() {
        request = null;
        decoder.destroy();
        decoder = null;
    }

    private void readHttpDataChunkByChunk() {
        PrintUtil.println(String.format("readHttpDataChunkByChunk"));
    }

    private void writeHttpData(InterfaceHttpData data) {
        PrintUtil.println(String.format("writeHttpData"));
    }

    private void writeResponse(Channel channel) {
        PrintUtil.println(String.format("writeResponse"));
    }

    private void writeMenu(ChannelHandlerContext channelHandlerContext) {
        PrintUtil.println(String.format("writeMenu"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
