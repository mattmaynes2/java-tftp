package core.cli;

public class HelpCommand extends Command{

    @Override
    public void execute(CommandHandler handler) {
        handler.handleHelpCommand(this);
    }

}
