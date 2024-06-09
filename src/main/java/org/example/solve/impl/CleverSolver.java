package org.example.solve.impl;

import org.example.core.CombatUtils;
import org.example.core.MoveUtils;
import org.example.model.Game;
import org.example.model.Monster;
import org.example.model.move.AttackMove;
import org.example.model.move.Move;
import org.example.model.move.TravelMove;
import org.example.solve.SolveFileWriter;
import org.example.solve.Solver;
import org.example.solve.SolverConstants;

import java.io.IOException;
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
        private static List<Comparator<State>> getComps() {
            List<Comparator<State>> res = new ArrayList<>();
            List<Long> mods = List.of(1L, 10L, 100L);
            for (long gMod : mods) {
                for (long eMod : mods) {
                    for (long fMod : mods) {
                        for (long dMod : mods) {
                            res.add(Comparator.comparingLong(o ->
                                    eMod * o.game.getHero().getTotalExp() - 10 * fMod * o.game.getHero().getFatigue() +
                                            gMod * o.game.getGoldGained() + dMod * o.game.getHero().getShift()
                            ));
                        }
                    }
                }
            }
            return res;
        }

        static List<Comparator<State>> comps = getComps();
        private final List<PriorityQueue<State>> bestStates = comps.stream().map(PriorityQueue::new).toList();

        private <T> void insertToQueue(PriorityQueue<T> queue, T elem, int limit) {
            queue.add(elem);
            if (queue.size() > limit) {
                queue.poll();
            }
        }

        public synchronized void addNewState(State state) {
            bestStates.forEach(q -> insertToQueue(q, state, 10));
        }

        public List<State> getAllStates() {
            Set<State> set = new HashSet<>();
            bestStates.forEach(set::addAll);
            return new ArrayList<>(set);
        }
    }

//    public SolverConstants constants;

//    public CleverSolver(SolverConstants constants) {
//        this.constants = constants;
//    }

    private static void processState(State s, int monsterCount, int movesLimit, List<BestMoves> answers) {
        State nState = s.makeCopy();
        for (int mI = 0; mI < nState.game.getField().getMonsters().size(); mI++) {
            Game nGame = nState.game;
            Monster monster = nGame.getField().snapshotMonster(mI);

            List<TravelMove> travelMoves = MoveUtils.moveToShotRange(nGame.getHero(), monster);
            CombatUtils.getDamage(nGame.getHero(), travelMoves, nGame.getField().getMonsters());
            nState.moves.addAll(travelMoves);
            nGame.setTravelsCount(nGame.getTravelsCount() + travelMoves.size());

            if (monster.getId() <= monsterCount) {
                long movesToKill = CombatUtils.movesToKill(nGame.getHero(), monster);
                CombatUtils.getDamage(nGame.getHero(), nGame.getHero().getX(),
                        nGame.getHero().getY(), nGame.getField().getMonsters(), movesToKill - 1);

                List<AttackMove> attackMoves = CombatUtils.killMonster(nGame.getHero(), monster);
                nState.moves.addAll(attackMoves);
                nGame.setGoldGained(nGame.getGoldGained() + nGame.getHero().getGold(monster.getGold()));
                CombatUtils.getDamage(nGame.getHero(), nGame.getHero().getX(),
                        nGame.getHero().getY(), nGame.getField().getMonsters(), 1);
            }

            if (nState.moves.size() < movesLimit) {
                answers.get(nState.moves.size()).addNewState(nState);
            }
            nState = s.makeCopy();
        }
    }

    @Override
    public List<Move> solve(Game game, String name, SolveFileWriter solveFileWriter) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        final int movesLimit = (int) game.getNumTurns();
        game.getHero().initStart();

        // Fake monsters
        final int monsterCount = game.getField().getMonsters().size();
        int split = 20;
        int newMonsterId = monsterCount + 1;
        for (int i = 0; i <= split; i++) {
            for (int j = 0; j <= split; j++) {
                long x = game.getField().getWidth() * i / split;
                long y = game.getField().getHeight() * j / split;
                Monster monster = new Monster(x, y, 0, 1, 0, newMonsterId++, 0, 0);
                game.getField().getMonsters().add(monster);
            }
        }

        List<BestMoves> answers = new ArrayList<>();
        for (int i = 0; i < game.getNumTurns(); i++) answers.add(new BestMoves());
        var initialState = new State(game, new ArrayList<>());
        answers.get(0).addNewState(initialState);
        for (int iter = 0; iter < movesLimit; iter++) {
            List<State> allStates = answers.get(iter).getAllStates();
            if (iter % 20 == 0) {
                long expectedTime = (System.currentTimeMillis() - startTime) * (movesLimit - iter) / 1000 / (iter + 1);
                Optional<State> st = allStates.stream().max(Comparator.comparingLong(s -> s.game.getGoldGained()));
                if (st.isPresent()) {
                    System.out.printf("%s. %d/%d it. Max gold: %d. Fatigue: %d. Expected: %d s.%n",
                            name, iter + 1, movesLimit, st.get().game.getGoldGained(),
                            st.get().game.getHero().getFatigue(), expectedTime);
                    solveFileWriter.writeToFile(st.get().moves);
                }
            }
            if (monsterCount > 100) {
                List<Thread> threads = allStates.stream()
                        .map(s -> new Thread(() -> processState(s, monsterCount, movesLimit, answers))).toList();
                threads.forEach(Thread::start);
                for (Thread th : threads) th.join();
            } else {
                for (State s : allStates) {
                    processState(s, monsterCount, movesLimit, answers);
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
        System.out.println(name + ". Optimal answer: " + bestState.game.getGoldGained());
        return bestState.moves;
    }
}
