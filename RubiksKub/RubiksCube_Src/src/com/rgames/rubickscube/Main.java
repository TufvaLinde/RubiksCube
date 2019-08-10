package com.rgames.rubickscube;

/**
 */
public final class Main {

    private Main() {
    }
    
    /**
     * @param args
     */
    public static void main(final String[] args) {
        final RubicksCubeGame game = RubicksCubeGame.getInstance();
        game.run();
    }
}
