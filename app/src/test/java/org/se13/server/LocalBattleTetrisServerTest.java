package org.se13.server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.se13.game.event.ServerErrorEvent;
import org.se13.game.rule.GameLevel;
import org.se13.game.rule.GameMode;
import org.se13.sqlite.config.ConfigRepositoryImpl;
import org.se13.view.tetris.Player;

import static javafx.beans.binding.Bindings.when;
import static org.junit.jupiter.api.Assertions.*;

class LocalBattleTetrisServerTest {
    @Test
    @DisplayName("LocalBattleTetrisServer Test")
    void testLocalBattleTetrisServer() {
        LocalBattleTetrisServer server = new LocalBattleTetrisServer(GameLevel.EASY, GameMode.DEFAULT);
        Player player1 = new Player(1, new ConfigRepositoryImpl(0).getPlayerKeyCode());
        Player player2 = new Player(2, new ConfigRepositoryImpl(1).getPlayerKeyCode());
        player1.connectToServer(server);
        player2.connectToServer(server);

        server.broadcast(new ServerErrorEvent("Hello"));
    }

  @Test
  @DisplayName("handleStartGame Test when session is null")
  void testHandleStartGameNullSession() {
      LocalBattleTetrisServer server = new LocalBattleTetrisServer(GameLevel.EASY, GameMode.DEFAULT);
      server.handleStartGame(1);
  }

  @Test
  @DisplayName("handleStartGame Test when player is already ready")
  void testHandleStartGamePlayerAlreadyReady() {
      LocalBattleTetrisServer server = new LocalBattleTetrisServer(GameLevel.EASY, GameMode.DEFAULT);
      Player player = new Player(1, new ConfigRepositoryImpl(0).getPlayerKeyCode());
      player.connectToServer(server);
      server.handleStartGame(1);
  }

  @Test
  @DisplayName("handleStartGame Test when all players are ready")
  void testHandleStartGameAllPlayersReady() {
      LocalBattleTetrisServer server = new LocalBattleTetrisServer(GameLevel.EASY, GameMode.DEFAULT);
      Player player1 = new Player(1, new ConfigRepositoryImpl(0).getPlayerKeyCode());
      Player player2 = new Player(2, new ConfigRepositoryImpl(1).getPlayerKeyCode());
      player1.connectToServer(server);
      player2.connectToServer(server);
      server.handleStartGame(1);
      server.handleStartGame(2);
  }
}
