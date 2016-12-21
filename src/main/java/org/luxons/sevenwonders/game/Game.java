package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;

public class Game {

    private final long id;

    private final Settings settings;

    private final List<Player> players;

    private final Table table;

    private final Decks decks;

    private final List<Card> discardedCards;

    private final Map<Integer, Move> preparedMoves;

    private Map<Integer, List<Card>> hands;

    private int currentAge = 0;

    public Game(long id, Settings settings, List<Player> players, List<Board> boards, Decks decks) {
        this.id = id;
        this.settings = settings;
        this.players = players;
        this.table = new Table(boards);
        this.decks = decks;
        this.discardedCards = new ArrayList<>();
        this.hands = new HashMap<>();
        this.preparedMoves = new HashMap<>();
        startNewAge();
    }

    public long getId() {
        return id;
    }

    public boolean containsUser(String userName) {
        return players.stream().anyMatch(p -> p.getUserName().equals(userName));
    }

    private void startNewAge() {
        currentAge++;
        hands = decks.deal(currentAge, table.getNbPlayers());
    }

    public List<PlayerTurnInfo> startTurn() {
        return hands.entrySet()
                    .stream()
                    .map(e -> table.createPlayerTurnInfo(e.getKey(), e.getValue()))
                    .peek(ptu -> ptu.setCurrentAge(currentAge))
                    .collect(Collectors.toList());
    }

    public void prepareCard(Move move) throws InvalidMoveException {
        if (!move.isValid(table)) {
            throw new InvalidMoveException();
        }
        preparedMoves.put(move.getPlayerIndex(), move);
    }

    public boolean areAllPlayersReady() {
        return preparedMoves.size() == players.size();
    }

    public List<Move> playTurn() {
        List<Move> playedMoves = mapToList(preparedMoves);

        // cards need to be all placed first as some effects depend on just-played cards
        placePreparedCards();
        playPreparedCards();
        preparedMoves.clear();

        return playedMoves;
    }

    private static List<Move> mapToList(Map<Integer, Move> movesPerPlayer) {
        List<Move> moves = new ArrayList<>(movesPerPlayer.size());
        for (int p = 0; p < movesPerPlayer.size(); p++) {
            Move move = movesPerPlayer.get(p);
            if (move == null) {
                throw new MissingPreparedMoveException(p);
            }
            moves.add(move);
        }
        return moves;
    }

    private void placePreparedCards() {
        preparedMoves.forEach((playerIndex, move) -> {
            switch (move.getType()) {
                case PLAY:
                    table.placeCard(playerIndex, decks.getCard(move.getCardName()));
                    break;
                case UPGRADE_WONDER:
                    table.upgradeWonderStage(playerIndex);
                    break;
                case DISCARD:
                    discardedCards.add(decks.getCard(move.getCardName()));
                    break;
            }
        });
    }

    private void playPreparedCards() {
        preparedMoves.forEach((playerIndex, move) -> {
            switch (move.getType()) {
                case PLAY:
                    table.activateCard(playerIndex, decks.getCard(move.getCardName()));
                    break;
                case UPGRADE_WONDER:
                    table.activateCurrentWonderStage(playerIndex);
                    break;
                case DISCARD:
                    table.discard(playerIndex, settings.getDiscardedCardGold());
                    break;
            }
        });
    }

    private static class MissingPreparedMoveException extends RuntimeException {
        MissingPreparedMoveException(int playerIndex) {
            super("Player " + playerIndex + " is not ready to play");
        }
    }

    private static class InvalidMoveException extends RuntimeException {
    }
}
