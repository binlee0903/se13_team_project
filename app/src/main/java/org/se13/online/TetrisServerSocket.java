package org.se13.online;

import com.google.gson.Gson;
import org.se13.game.action.TetrisAction;
import org.se13.game.event.TetrisEvent;
import org.se13.game.event.TetrisEventCode;
import org.se13.server.TetrisActionPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TetrisServerSocket {
    private static final Logger log = LoggerFactory.getLogger(TetrisServerSocket.class);
    private Gson gson = new Gson();
    private int userId;
    private Socket socket;
    private DataInputStream in;
    private BufferedOutputStream out;

    public TetrisServerSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void write(TetrisEvent event, int userId) throws IOException {
        log.info("Player{} event {}", userId, event);
        String json = gson.toJson(event);
        int capacity = 4 + 4 + json.getBytes().length; // userId = int = 4byte, eventCode = int = 4byte
        ByteBuffer buffer = ByteBuffer.allocate(4 + capacity); // packet length = int = 4byte;

        buffer.putInt(capacity);
        buffer.putInt(userId);
        buffer.putInt(TetrisEventCode.getEventCode(event));
        buffer.put(json.getBytes());

        out.write(buffer.array());
        out.flush();
    }

    public void write(TetrisEvent event) throws IOException {
        write(event, userId);
    }

    public void gameOver(boolean isGameOver) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4); // packet length = int = 4byte;
        buffer.putInt(4);
        buffer.putInt(isGameOver? 1 : 0);

        out.write(buffer.array());
        out.flush();
    }

    public TetrisActionPacket read() throws IOException {

        int length = 8;
        byte[] data = new byte[length];
        int readed = 0;
        while ((readed = readed + in.read(data, readed, data.length - readed)) != data.length) ;

        ByteBuffer buffer = ByteBuffer.wrap(data);
        int responseUserId = buffer.getInt();
        TetrisAction action = TetrisAction.fromCode(buffer.getInt());
        log.info("Player{} request {}", userId, action);

        return new TetrisActionPacket(responseUserId, action);
    }
}
