package core.cli;

import core.cli.CommandToken;

public abstract class Command {

	protected CommandToken token;

	public abstract void execute(CommandHandler handler);

    public CommandToken getToken(){
        return this.token;
    }

}
