package org.example.solve.impl;

import org.example.core.CombatUtils;
import org.example.core.MoveUtils;
import org.example.model.Game;
import org.example.model.Monster;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;

import java.util.*;

public class CleverSolver extends Solver {
    private static int QUEUE_SIZE = 15;

    static class State {
        Game game;

        List<Move> moves;

        public State(Game game, List<Move> moves) {
            this.game = game;
            this.moves = moves;
        }

        public State makeCopy() {
            return new State(game.makeCopy(), new ArrayList<>(moves));
        }
    }

    static class BestMoves {
        static List<Comparator<State>> comps = List.of(
                Comparator.comparingLong(s -> s.game.getGoldGained()),
                (o1, o2) -> {
                    var h1 = o1.game.getHero();
                    var h2 = o2.game.getHero();
                    if (h1.getLevel() != h2.getLevel()) {
                        return Long.compare(h1.getLevel(), h2.getLevel());
                    }
                    return Long.compare(h1.getExp(), h2.getExp());
                }
        );
        PriorityQueue<State> bestByGold = new PriorityQueue<>(comps.get(0));

        PriorityQueue<State> bestByExp = new PriorityQueue<>(comps.get(1));

        public void addNewState(State state) {
            bestByGold.add(state);
            bestByExp.add(state);
            if (bestByGold.size() > QUEUE_SIZE) {
                bestByGold.poll();
            }
            if (bestByExp.size() > QUEUE_SIZE) {
                bestByExp.poll();
            }
        }

        public List<State> getAllStates() {
            Set<State> set = new HashSet<>();
            set.addAll(bestByExp);
            set.addAll(bestByGold);
            return new ArrayList<>(set);
        }
    }

    public SolverConstants constants;

    public CleverSolver(SolverConstants constants) {
        this.constants = constants;
    }

    @Override
    public List<Move> solve(Game game) {
        int movesLimit = (int) game.getNumTurns();
        List<BestMoves> answers = new ArrayList<>();
        for (int i = 0; i < game.getNumTurns(); i++) answers.add(new BestMoves());
        var initialState = new State(game, new ArrayList<>());
        answers.get(0).addNewState(initialState);
        for (int iter = 0; iter < movesLimit; iter++) {
            System.out.println("Iter " + (iter + 1) + " / " + movesLimit);
            List<State> allStates = answers.get(iter).getAllStates();
//            System.out.println("Golds: " + allStates.stream().map(s -> s.game.getGoldGained()).toList());
            for (State s : allStates) {
                for (int mI = 0; mI < s.game.getField().getMonsters().size(); mI++) {
                    if (s.game.getField().getMonsters().get(mI).isKilled()) {
                        continue;
                    }
                    State nState = s.makeCopy();
                    Game nGame = nState.game;
                    Monster monster = nGame.getField().snapshotMonster(mI);

                    List<TravelMove> travelMoves = MoveUtils.moveToShotRange(nGame.getHero(), monster);
                    List<AttackMove> attackMoves = CombatUtils.killMonster(nGame.getHero(), monster);
                    nState.moves.addAll(travelMoves);
                    nState.moves.addAll(attackMoves);
                    nGame.setGoldGained(nGame.getGoldGained() + monster.getGold());
                    if (nState.moves.size() < movesLimit) {
                        answers.get(nState.moves.size()).addNewState(nState);
                    }
                }
            }
        }
        State bestState = initialState;
        for (BestMoves bm : answers) {
            for (State s : bm.getAllStates()) {
                if (s.game.getGoldGained() > bestState.game.getGoldGained()) {
                    bestState = s;
                }
            }
        }
        System.out.println("Optimal answer: " + bestState.game.getGoldGained());
        return bestState.moves;
    }
}
