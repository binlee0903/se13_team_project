package org.se13.online;

import com.google.gson.Gson;
import org.se13.game.action.TetrisAction;
import org.se13.view.tetris.TetrisGameEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TetrisClientSocket {
    private static final Logger log = LoggerFactory.getLogger(TetrisClientSocket.class);
    private Gson gson = new Gson();
    private int userId;
    private Socket socket;
    private DataInputStream in;
    private BufferedOutputStream out;

    public TetrisClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void write(TetrisAction action) throws IOException {
        log.info("Player{} request {}", userId, action);

        int capacity = 4 + 4;
        ByteBuffer buffer = ByteBuffer.allocate(capacity);

        buffer.putInt(userId);
        buffer.putInt(action.getCode());

        out.write(buffer.array());
        out.flush();
    }

    public Object read() throws IOException {
        int length = in.readInt(); // 몇 바이트만큼 읽어야 하는지 체크
        if (length == 4) {
            return in.readInt();
        }
        byte[] data = new byte[length];
        int readed = 0;
        while ((readed = readed + in.read(data, readed, data.length - readed)) != data.length) ;
        return new TetrisEventPacket.Builder(data).build(gson);
    }
}
