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
    private final Integer port;
    private final long defaultTimeoutSeconds = 15;

    private final SshClient client;
    private ClientSession session;


    public ShellClient(String hostname, String username, String password, Integer port) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.port = port;
        client = SshClient.setUpDefaultClient();
        client.start();
    }


    private ClientSession getSession() {

        if(session == null) {
            log.info("getSession() - Creating SSH session");
            try {
                session = client.connect(username, hostname, port).verify(defaultTimeoutSeconds, TimeUnit.SECONDS).getSession();
                session.addPasswordIdentity(password);
                session.auth().verify(defaultTimeoutSeconds, TimeUnit.SECONDS);
            } catch (IOException | UnsupportedOperationException e) {
                log.error(e.getMessage());
            }
        }

        return session;
    }


    public String execute(String command) {

        log.info("execute() - command: {}", command);
        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
             ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
             ClientChannel channel = getSession().createChannel(Channel.CHANNEL_EXEC, command)) // to execute remote commands
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
        } catch (UnsupportedOperationException | IOException e) {
            session = null;
        }

        return null;
    }



    public String read(String file) {

        log.info("read() - file: {}", file);

        ScpClientCreator creator = ScpClientCreator.instance();
        ScpClient client = creator.createScpClient(getSession());
        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream()) {
            client.download(file, responseStream);
            return responseStream.toString();
        } catch (UnsupportedOperationException | IOException  e) {
            session = null;
        }

        return null;
    }


}
