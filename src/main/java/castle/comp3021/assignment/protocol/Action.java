package castle.comp3021.assignment.protocol;

import castle.comp3021.assignment.protocol.exception.ActionException;

public abstract class Action {
    protected Game game;
    protected String[] args;

    public Action(Game game, String[] args) {
        this.game = game;
        this.args = args;
    }

    // perform the action
    public abstract void perform() throws ActionException;
}
