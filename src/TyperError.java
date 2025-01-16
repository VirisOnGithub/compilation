package src;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Gère les erreurs afin d'afficher un message personnalisé avec la localisation de l'erreur,
 * et un extrait du code à modifier
 */
public class TyperError extends RuntimeException {

    /**
     * Le contexte pour avoir la position précise de l'erreur
     */
    private final ParserRuleContext ctx;

    /**
     * Valeur optionnelle de décalage afin de pouvoir mieux viser certaines erreurs (cf visitPrint)
     */
    private final int offset;

    /**
     * Crée un nouveau {@code TyperError}
     *
     * @param message Le message de l'erreur.
     * @param ctx Le contexte de l'erreur.
     * @param offset le décalage de l'erreur
     */
    public TyperError(String message, ParserRuleContext ctx, int offset) {
        super(message);
        this.ctx = ctx;
        this.offset = offset;
    }

    /**
     * Crée un nouveau {@code TyperError}
     *
     * @param message Le message de l'erreur.
     * @param ctx Le contexte de l'erreur.
     */
    public TyperError(String message, ParserRuleContext ctx) {
        this(message, ctx, 0);
    }

    /**
     * Getter du contexte
     *
     * @return Le contexte
     */
    public ParserRuleContext getCtx() {
        return ctx;
    }

    /**
     * Insertion de l'erreur dans le terminal
     *
     * @param fileContent Le contenu du fichier
     */
    public void printError(String fileContent) {
        int lineIndex = ctx.start.getLine();
        int columnIndex = ctx.start.getCharPositionInLine();
        String line = fileContent.split("\n")[lineIndex - 1];
        System.err.println("Error line "
                + lineIndex + " column "
                + columnIndex + " : " + this.getMessage()
                + "\n" + line
                + "\n" + " ".repeat(columnIndex + offset) + "^"
        );
    }
}