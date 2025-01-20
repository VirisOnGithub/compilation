package src;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import src.Asm.Program;

import java.io.*;

public class Main {
	public static void main(String[] args) {
		// 1 = generate assembly code
		// 0 = generate linear code
		final int CODE_TO_GENERATE = 1;
		// 1 = use conflict graph
		// 0 = don't use conflict graph
		final int USE_CONFLICT_GRAPH = 1;

		//lecture de input.txt
		String fichier = "input.txt";
		StringBuilder input = new StringBuilder();

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
			System.out.println(e.getMessage());
		}

		// GROUPE 1
		grammarTCLLexer lexer = new grammarTCLLexer(CharStreams.fromString(input.toString())); // analyse lexicale de la String s
		CommonTokenStream tokens = new CommonTokenStream(lexer); // récupération des terminaux
		grammarTCLParser parser = new grammarTCLParser(tokens); // constructeur de l'analyseur syntaxique
		grammarTCLParser.MainContext tree = parser.main(); // création de l'AST

		TyperVisitor visitor = new TyperVisitor();
		visitor.visit(tree); // unification des types

		// GROUPE 2
		CodeGenerator codeGenerator = new CodeGenerator(visitor.getTypes());
		Program program = codeGenerator.visit(tree); // génération du code linéaire

		if (CODE_TO_GENERATE == 1) {
			// GROUPE 3
			ControlGraph controlGraph = new ControlGraph(program); // graphe de controle
			ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program); // graphe de conflit

			AssemblerGenerator generator = new AssemblerGenerator(program, conflictGraph, USE_CONFLICT_GRAPH); // génération du code assembler
			String assemblyCode = generator.generateAssembly();

			// écriture dans prog.asm
			try {
				FileWriter fileWriter = new FileWriter("prog.asm"); // écrit le programme dans prog.asm
				fileWriter.write(assemblyCode);
				fileWriter.flush();
				fileWriter.close();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		else {
			// écriture dans prog.asm
			try {
				FileWriter fileWriter = new FileWriter("prog.asm"); // écrit le programme dans prog.asm
				fileWriter.write(program.toString());
				fileWriter.flush();
				fileWriter.close();
			}
			catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

	}
}
