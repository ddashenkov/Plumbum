package edu.ddashenkov.plumbum.webadapter;

import java.util.stream.Stream;

final class Starter {

    private Starter() {
        // Prevent utility class instantiation.
    }

    public static void main(String[] args) {
        Stream.of(LoginController.create(),
                  RecordController.create())
              .forEach(Controller::serve);
    }
}
