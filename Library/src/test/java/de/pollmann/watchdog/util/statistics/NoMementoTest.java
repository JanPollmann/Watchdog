package de.pollmann.watchdog.util.statistics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoMementoTest {

  @Test
  void everythingIsNull() {
    NoMemento memento = new NoMemento();

    Assertions.assertNull(memento.getStart());
    Assertions.assertNull(memento.getBeginCall());
    Assertions.assertNull(memento.getStopCall());
    Assertions.assertNull(memento.getBeginResultConsuming());
    Assertions.assertNull(memento.getStopResultConsuming());
    Assertions.assertNull(memento.getEnd());
  }

}
