package biz.nellemann.svci;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClientCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class ShellClient {

    private final static Logger log = LoggerFactory.getLogger(ShellClient.class);

    private final String hostname;
    private final String username;
    private final String password;
    private final int port = 22;
    private final long defaultTimeoutSeconds = 15;

    private final SshClient client;


    public ShellClient(String hostname, String username, String password) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        client = SshClient.setUpDefaultClient();
        client.start();
    }


    public String execute(String command) throws UnsupportedOperationException, IOException {

        log.info("execute() - command: {}", command);
        try (ClientSession session = client.connect(username, hostname, port).verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                 ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                 ClientChannel channel = session.createChannel(Channel.CHANNEL_EXEC, command)) // to execute remote commands
            {
                channel.setOut(responseStream);
                channel.setErr(errorStream);
                try {
                    channel.open().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
                    try (OutputStream pipedIn = channel.getInvertedIn()) {
                        pipedIn.write(command.getBytes());
                        pipedIn.flush();
                    }
                    channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED),
                        TimeUnit.SECONDS.toMillis(defaultTimeoutSeconds));
                    String error = errorStream.toString();
                    if (!error.isEmpty()) {
                        throw new UnsupportedOperationException(error);
                    }
                    return responseStream.toString();
                }
                finally {
                    channel.close(false);
                }
            }
        }
    }


    public String read(String file) throws UnsupportedOperationException, IOException {

        log.info("read() - file: {}", file);
        try (ClientSession session = client.connect(username, hostname, port).verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession()) {
            session.addPasswordIdentity(password);
            session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);

            ScpClientCreator creator = ScpClientCreator.instance();
            ScpClient client = creator.createScpClient(session);
            try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream()) {
                client.download(file, responseStream);
                return responseStream.toString();
            }
        }
    }

}
