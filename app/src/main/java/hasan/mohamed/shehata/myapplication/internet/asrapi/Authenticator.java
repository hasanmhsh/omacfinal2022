package hasan.mohamed.shehata.myapplication.internet.asrapi;

import com.google.auth.Credentials;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.StatusException;
public class Authenticator implements ClientInterceptor {

    private final Credentials myAtuthentication;

    private Metadata metadata;

    private Map<String, List<String>> stringListMap;

    Authenticator(Credentials auth) {
        myAtuthentication = auth;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> descriptorCallback, CallOptions options, final Channel channel) {
        return new ClientInterceptors.CheckedForwardingClientCall<ReqT, RespT>(
                channel.newCall(descriptorCallback, options)) {
            @Override
            protected void checkedStart(Listener<RespT> listener, Metadata metadata1)
                    throws StatusException {
                Metadata cachedSaved;
                URI uri = constructConnectionURI(channel, descriptorCallback);
                synchronized (this) {
                    Map<String, List<String>> recent = getRequiredInfo(uri);
                    if (stringListMap == null || stringListMap != recent) {
                        stringListMap = recent;
                        metadata = constructMetadata(stringListMap);
                    }
                    cachedSaved = metadata;
                }
                metadata1.merge(cachedSaved);
                delegate().start(listener, metadata1);
            }
        };
    }
    private static Metadata constructMetadata(Map<String, List<String>> info) {
        Metadata metadata1 = new Metadata();
        if (info != null) {
            for (String str : info.keySet()) {
                Metadata.Key<String> headerKey = Metadata.Key.of(
                        str, Metadata.ASCII_STRING_MARSHALLER);
                for (String str1 : info.get(str)) {
                    metadata1.put(headerKey, str1);
                }
            }
        }
        return metadata1;
    }



    private URI releaseConnection(URI connectionURI) throws StatusException {
        try {
            return new URI(connectionURI.getScheme(), connectionURI.getUserInfo(), connectionURI.getHost(), -1 /* port */,
                    connectionURI.getPath(), connectionURI.getQuery(), connectionURI.getFragment());
        } catch (URISyntaxException uriSyntaxException) {
            throw Status.UNAUTHENTICATED
                    .withDescription("Failed")
                    .withCause(uriSyntaxException).asException();
        }
    }

    private URI constructConnectionURI(Channel medium, MethodDescriptor<?, ?> descriptorCallback)
            throws StatusException {
        String cred = medium.authority();
        if (cred == null) {
            throw Status.UNAUTHENTICATED
                    .withDescription("Auth failed")
                    .asException();
        }
        final String protocol = "https";
        final int port = 443;
        String restOfURL = "/" + MethodDescriptor.extractFullServiceName(descriptorCallback.getFullMethodName());
        URI connectionURI;
        try {
            connectionURI = new URI(protocol, cred, restOfURL, null, null);
        } catch (URISyntaxException exception) {
            throw Status.UNAUTHENTICATED
                    .withDescription("Can't make connection")
                    .withCause(exception).asException();
        }
        if (connectionURI.getPort() == port) {
            connectionURI = releaseConnection(connectionURI);
        }
        return connectionURI;
    }

    private Map<String, List<String>> getRequiredInfo(URI connectionURI) throws StatusException {
        try {
            return myAtuthentication.getRequestMetadata(connectionURI);
        } catch (IOException ioException) {
            throw Status.UNAUTHENTICATED.withCause(ioException).asException();
        }
    }



}
