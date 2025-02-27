package src;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import src.Asm.Program;

import java.io.*;

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
			System.out.println(e.getMessage());
		}

		grammarTCLLexer lexer = new grammarTCLLexer(CharStreams.fromString(input.toString())); // analyse lexicale de la String s
		CommonTokenStream tokens = new CommonTokenStream(lexer); // récupération des terminaux
		grammarTCLParser parser = new grammarTCLParser(tokens); // constructeur de l'analyseur syntaxique
		grammarTCLParser.MainContext tree = parser.main(); // création de l'AST

		TyperVisitor visitor = new TyperVisitor();
		visitor.visit(tree); // unification des types

		CodeGenerator codeGenerator = new CodeGenerator(visitor.getTypes());
		Program program = codeGenerator.visit(tree); // génération du code linéaire

		try {
			FileWriter fileWriter = new FileWriter("prog_lineaire.asm"); // écrit le programme dans prog.asm
			fileWriter.write(program.toString());
			fileWriter.flush();
			fileWriter.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}

		ControlGraph controlGraph = new ControlGraph(program);
		ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

		AssemblerGenerator generator = new AssemblerGenerator(program, conflictGraph);

		String assemblyCode = generator.generateAssembly();
		System.out.println(assemblyCode);

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
}