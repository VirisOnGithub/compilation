package src;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import src.Type.*;

import java.io.*;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		String fichier ="input.txt";
		StringBuilder input = new StringBuilder();

		//lecture du fichier texte
		try{
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			while ((ligne = br.readLine())!=null) {
				input.append(ligne).append("\n");
			}
			br.close();
		}
		catch (Exception e){
			System.out.println(e);
		}

		grammarTCLLexer lexer = new grammarTCLLexer(CharStreams.fromString(input.toString())); // analyse lexicale de la String s
		CommonTokenStream tokens = new CommonTokenStream(lexer); // récupération des terminaux

		grammarTCLParser parser = new grammarTCLParser(tokens); // constructeur de l'analyseur syntaxique
		grammarTCLParser.MainContext tree = parser.main(); // création de l'AST

		TyperVisitor visitor = new TyperVisitor(); // lancement de l'évaluateur

		visitor.visit(tree);

		System.out.println("Final Types "+ visitor.getTypes());
	}
}
