package org.example.path;

import org.example.model.Game;
import org.example.path.dao.MonsterTarget;

/**
 * Получает состояние мира на текущий момент, возвращает номер монстра,
 * которого мы должны даубить следующим, и как к нему нужно подойти.
 */
public interface PathFinder {
    MonsterTarget getNextMonsterInfo(Game game);
}
