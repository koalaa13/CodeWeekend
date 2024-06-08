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

    private class BestMoves {
        private static final Comparator<State> compareByTotalExp = Comparator.comparingLong(o -> o.game.getHero().getTotalExp());
        static List<Comparator<State>> comps = List.of(
                Comparator.comparingLong(o -> o.game.getGoldGained()),
                compareByTotalExp,
                (o1, o2) -> {
                    if (o1.game.getTravelsCount() != o2.game.getTravelsCount()) {
                        return Long.compare(o2.game.getTravelsCount(), o1.game.getTravelsCount());
                    }
                    return compareByTotalExp.compare(o1, o2);
                },
                Comparator.comparingLong(o -> o.game.getHero().getTotalExp() * 5L + o.game.getGoldGained()),
                Comparator.comparingLong(o -> o.game.getHero().getFatigue())
        );
        private final PriorityQueue<State> bestByGold = new PriorityQueue<>(comps.get(0));

        private final PriorityQueue<State> bestByExp = new PriorityQueue<>(comps.get(1));

        private final PriorityQueue<State> bestByMoves = new PriorityQueue<>(comps.get(2));

        private final PriorityQueue<State> bestByComplex = new PriorityQueue<>(comps.get(3));

        private final PriorityQueue<State> bestByFatigue = new PriorityQueue<>(comps.get(4));

        private <T> void insertToQueue(PriorityQueue<T> queue, T elem, int limit) {
            if (!queue.contains(elem)) {
                queue.add(elem);
                if (queue.size() > limit) {
                    queue.poll();
                }
            }
        }

        public void addNewState(State state) {
            insertToQueue(bestByGold, state, 3);
            insertToQueue(bestByExp, state, 3);
//            insertToQueue(bestByMoves, state, 5);
            insertToQueue(bestByComplex, state, 15);
            insertToQueue(bestByFatigue, state, 10);
        }

        public List<State> getAllStates() {
            Set<State> set = new HashSet<>();
            set.addAll(bestByExp);
            set.addAll(bestByGold);
//            set.addAll(bestByMoves);
            set.addAll(bestByComplex);
            set.addAll(bestByFatigue);
            return new ArrayList<>(set);
        }
    }

//    public SolverConstants constants;

//    public CleverSolver(SolverConstants constants) {
//        this.constants = constants;
//    }

    @Override
    public List<Move> solve(Game game) {
        long startTime = System.currentTimeMillis();
        int movesLimit = (int) game.getNumTurns();
        List<BestMoves> answers = new ArrayList<>();
        for (int i = 0; i < game.getNumTurns(); i++) answers.add(new BestMoves());
        var initialState = new State(game, new ArrayList<>());
        answers.get(0).addNewState(initialState);
        for (int iter = 0; iter < movesLimit; iter++) {
            List<State> allStates = answers.get(iter).getAllStates();
            if (iter % 50 == 0) {
                long spentTime = (System.currentTimeMillis() - startTime) / 1000;
                long gold =  allStates.stream().map(s -> s.game.getGoldGained()).max(Comparator.naturalOrder()).orElse(0L);
                System.out.println((iter + 1) + "/" + movesLimit + ". Max gold: " + gold + ". Time: " + spentTime);
            }
            for (State s : allStates) {
                for (int mI = 0; mI < s.game.getField().getMonsters().size(); mI++) {
                    if (s.game.getField().getMonsters().get(mI).isKilled()) {
                        continue;
                    }
                    State nState = s.makeCopy();
                    Game nGame = nState.game;
                    Monster monster = nGame.getField().snapshotMonster(mI);

                    List<TravelMove> travelMoves = MoveUtils.moveToShotRange(nGame.getHero(), monster);
                    CombatUtils.getDamage(nGame.getHero(), travelMoves, nGame.getField().getMonsters());

                    long movesToKill = CombatUtils.movesToKill(nGame.getHero(), monster);
                    CombatUtils.getDamage(nGame.getHero(), nGame.getHero().getX(),
                            nGame.getHero().getY(), nGame.getField().getMonsters(), movesToKill - 1);

                    List<AttackMove> attackMoves = CombatUtils.killMonster(nGame.getHero(), monster);
                    nState.moves.addAll(travelMoves);
                    nState.moves.addAll(attackMoves);
                    nGame.setGoldGained(nGame.getGoldGained() + nGame.getHero().getGold(monster.getGold()));
                    nGame.setTravelsCount(nGame.getTravelsCount() + travelMoves.size());
                    CombatUtils.getDamage(nGame.getHero(), nGame.getHero().getX(),
                            nGame.getHero().getY(), nGame.getField().getMonsters(), 1);
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
