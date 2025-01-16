package src;

import org.antlr.v4.runtime.ParserRuleContext;

public class TyperError extends RuntimeException {
    private final ParserRuleContext ctx;
    private final int offset;
    public TyperError(String message, ParserRuleContext ctx, int offset) {
        super(message);
        this.ctx = ctx;
        this.offset = offset;
    }

    public TyperError(String message, ParserRuleContext ctx) {
        this(message, ctx, 0);
    }

    public ParserRuleContext getCtx() {
        return ctx;
    }

    public void printError(String file){
        int lineIndex = ctx.start.getLine();
        int columnIndex = ctx.start.getCharPositionInLine();
        String line = file.split("\n")[lineIndex-1];
        System.err.println("Error line "
                + lineIndex + " column "
                + columnIndex + " : " + this.getMessage()
                + "\n" + line
                + "\n" + " ".repeat(columnIndex + offset) + "^"
        );
    }
}
