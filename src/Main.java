package src;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import src.Type.*;

import java.io.*;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		/*
		// tests PrimitiveType.equals() :
		Type integer = new PrimitiveType(Type.Base.INT);
		Type integer2 = new PrimitiveType(Type.Base.INT);
		Type booooool = new PrimitiveType(Type.Base.BOOL);

		System.out.println(integer.equals(integer2));	//true
		System.out.println(integer.equals(booooool));	//false


		// tests ArrayType.equals()
		Type arrayInt = new ArrayType(integer);
		Type arrayInt2 = new ArrayType(integer);
		Type arrayBool = new ArrayType(booooool);
		Type arrayArrayInt = new ArrayType(arrayInt);
		System.out.println(arrayInt.equals(arrayInt2));		//true
		System.out.println(arrayInt.equals(arrayBool));		//false
		System.out.println(arrayInt.equals(arrayArrayInt));	//false

		//tests FunctionType.equals() :
		ArrayList<Type> arg1 = new ArrayList<>();
		arg1.add(integer);
		arg1.add(integer2);
		Type func1 = new FunctionType(booooool, arg1);
		Type func2 = new FunctionType(integer, arg1);
		System.out.println(func1.equals(booooool)); //false
		System.out.println(func1.equals(func1));	//true
		System.out.println(func1.equals(func2));	//false

		//tests UnknownType.equals()
		Type cOnFuSiOn = new UnknownType();
		Type cOnFuSiOn2 = new UnknownType();
		Type unknown = new UnknownType("aHAAAAAAA", 12);
		Type unknown2 = new UnknownType("aHAAAAAAA", 12);
		System.out.println(cOnFuSiOn.equals(cOnFuSiOn));	//true
		System.out.println(cOnFuSiOn.equals(cOnFuSiOn2));	//false
		System.out.println(unknown2.equals(unknown));		//true
		System.out.println(unknown2.equals(integer));		//false
		System.out.println(cOnFuSiOn.equals(integer));		//false

		// tests PrimitiveType.contains() :
		System.out.println(integer.contains((UnknownType) cOnFuSiOn));	//false

		// tests UnknownsType.contains() :
		System.out.println(cOnFuSiOn.contains((UnknownType) cOnFuSiOn));	//true
		System.out.println(cOnFuSiOn2.contains((UnknownType) cOnFuSiOn));	//false
		System.out.println(unknown.contains((UnknownType) unknown2));		//true

		//tests ArrayType.contains()
		Type array1 = new ArrayType(cOnFuSiOn);
		Type array2 = new ArrayType(array1);
		System.out.println(array1.contains((UnknownType) cOnFuSiOn)); 	//true
		System.out.println(array2.contains((UnknownType) cOnFuSiOn)); 	//true
		System.out.println(arrayInt.contains((UnknownType) cOnFuSiOn));	//false

		//tests FunctionType.contains()
		ArrayList<Type> arg2 = new ArrayList<>();
		arg2.add(integer);
		arg2.add(cOnFuSiOn);
		Type func3 = new FunctionType(cOnFuSiOn, arg1);
		Type func4 = new FunctionType(cOnFuSiOn, arg2);
		ArrayList<Type> arg3 = new ArrayList<>();
		arg3.add(func4);
		arg3.add(func2);
		ArrayList<Type> arg4 = new ArrayList<>();
		Type func5 = new FunctionType(integer, arg2);
		Type func6 = new FunctionType(integer, arg1);
		Type func7 = new FunctionType(booooool, arg3);
		Type func8 = new FunctionType(booooool, arg4);
		Type func9 = new FunctionType(cOnFuSiOn, arg4);
		System.out.println(func1.contains((UnknownType) cOnFuSiOn)); 	//false
		System.out.println(func2.contains((UnknownType) cOnFuSiOn)); 	//false
		System.out.println(func3.contains((UnknownType) cOnFuSiOn)); 	//true
		System.out.println(func4.contains((UnknownType) cOnFuSiOn));	//true
		System.out.println(func5.contains((UnknownType) cOnFuSiOn));	//true
		System.out.println(func6.contains((UnknownType) cOnFuSiOn));	//false
		System.out.println(func7.contains((UnknownType) cOnFuSiOn));	//true
		System.out.println(func8.contains((UnknownType) cOnFuSiOn));	//false
		System.out.println(func9.contains((UnknownType) cOnFuSiOn));	//true



		System.out.println(integer.unify(integer2));	//null
		System.out.println(integer.unify(cOnFuSiOn));	//{UnknownType(#, 0)=INT}
		System.out.println(cOnFuSiOn.unify(booooool));	//{UnknownType(#, 0)=BOOL}
		System.out.println(cOnFuSiOn.unify(cOnFuSiOn2));
		// System.out.println(integer.unify(booooool));	//error


		System.out.println(func1.contains(cOnFuSiOn)); 	//false
		System.out.println(func2.contains(cOnFuSiOn)); 	//false
		System.out.println(func3.contains(cOnFuSiOn)); 	//true
		System.out.println(func4.contains(cOnFuSiOn));	//true
		System.out.println(func5.contains(cOnFuSiOn));	//true
		System.out.println(func6.contains(cOnFuSiOn));	//false
		System.out.println(func7.contains(cOnFuSiOn));	//true
		System.out.println(func8.contains(cOnFuSiOn));	//false
		System.out.println(func9.contains(cOnFuSiOn));	//true

		 */

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

		System.out.println("Types finales"+ visitor.getTypes());
	}
}
