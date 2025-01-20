package src;

import src.Asm.CondJump;
import src.Asm.Instruction;
import src.Asm.JumpCall;
import src.Asm.Mem;
import src.Asm.Program;
import src.Asm.Stop;
import src.Asm.Ret;
//import org.antlr.v4.runtime.CharStreams;
//import org.antlr.v4.runtime.CommonTokenStream;
//import src.Type.*;

//import java.io.*;
//import java.util.ArrayList;

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
	}*/

	Program program = new Program();

	Instruction instr0 = new Mem("L0", Mem.Op.ST, 0, 1) {};
	Instruction instr1 = new Instruction("L1", "XOR R1000 R1000 R1000") {};
	Instruction instr2 = new Instruction("L2", "SUBi R1000 R1000 1") {};
	Instruction instr3 = new Instruction("L3", "PRINT R1001") {};
	Instruction instr4 = new CondJump("L4", CondJump.Op.JEQU, 1000, 1001, "LABEL2") {};
	Instruction instr5 = new CondJump("L5", CondJump.Op.JINF, 1000, 1001, "L6") {};
	Instruction instr6 = new CondJump("L6", CondJump.Op.JSUP, 1000, 1001, "L7") {};
	Instruction instr7 = new JumpCall("L7", JumpCall.Op.CALL, "FUNC1") {};
	Instruction instr8 = new JumpCall("L8", JumpCall.Op.JMP, "END") {};
	Instruction instr9 = new Stop("L9") {};
	Instruction instr10 = new Instruction("LABEL1", "ADD R1002 R1003 R1004") {};
	Instruction instr11 = new Instruction("L11", "MUL R1005 R1006 R1007") {};
	Instruction instr12 = new JumpCall("L12", JumpCall.Op.JMP, "END") {};
	Instruction instr13 = new Instruction("LABEL2", "DIV R1008 R1009 R1010") {};
	Instruction instr14 = new Instruction("L14", "PRINT R1011") {};
	Instruction instr15 = new Ret("END") {};

	// Instructions utilisant plus de registres
	Instruction instr16 = new Instruction("L15", "ADD R1012 R1013 R1014") {};
	Instruction instr17 = new Instruction("L16", "MUL R1015 R1016 R1017") {};
	Instruction instr18 = new Instruction("L17", "SUB R1018 R1019 R1020") {};
	Instruction instr19 = new Instruction("L18", "DIV R1021 R1022 R1023") {};
	Instruction instr20 = new Instruction("L19", "AND R1024 R1025 R1026") {};
	Instruction instr21 = new Instruction("L20", "OR R1027 R1028 R1029") {};
	Instruction instr22 = new Instruction("L21", "ADDi R1030 R1031 5") {};
	Instruction instr23 = new Instruction("L22", "XOR R1032 R1033 R1034") {};
	Instruction instr24 = new Instruction("L23", "ADD R1035 R1036 R1037") {};
	Instruction instr25 = new Instruction("L24", "SUB R1038 R1039 R1040") {};
	Instruction instr26 = new Instruction("L25", "MUL R1041 R1042 R1043") {};
	Instruction instr27 = new Instruction("L26", "DIV R1044 R1045 R1046") {};
	Instruction instr28 = new Instruction("L27", "PRINT R1047") {};
	Instruction instr29 = new Instruction("L28", "ADD R1048 R1049 R1050") {};
	Instruction instr30 = new Instruction("L29", "SUB R1051 R1052 R1053") {};
	Instruction instr31 = new Instruction("L30", "MUL R1054 R1055 R1056") {};
	Instruction instr32 = new Instruction("L31", "DIV R1057 R1058 R1059") {};
	Instruction instr33 = new Instruction("L32", "PRINT R1060") {};

	program.addInstruction(instr0);
	program.addInstruction(instr1);
	program.addInstruction(instr2);
	program.addInstruction(instr3);
	program.addInstruction(instr4);
	program.addInstruction(instr5);
	program.addInstruction(instr6);
	program.addInstruction(instr7);
	program.addInstruction(instr8);
	program.addInstruction(instr9);
	program.addInstruction(instr10);
	program.addInstruction(instr11);
	program.addInstruction(instr12);
	program.addInstruction(instr13);
	program.addInstruction(instr14);
	program.addInstruction(instr15);
	program.addInstruction(instr16);
	program.addInstruction(instr17);
	program.addInstruction(instr18);
	program.addInstruction(instr19);
	program.addInstruction(instr20);
	program.addInstruction(instr21);
	program.addInstruction(instr22);
	program.addInstruction(instr23);
	program.addInstruction(instr24);
	program.addInstruction(instr25);
	program.addInstruction(instr26);
	program.addInstruction(instr27);
	program.addInstruction(instr28);
	program.addInstruction(instr29);
	program.addInstruction(instr30);
	program.addInstruction(instr31);
	program.addInstruction(instr32);
	program.addInstruction(instr33);

	ControlGraph controlGraph = new ControlGraph(program);
    ConflictGraph conflictGraph = new ConflictGraph(controlGraph, program);

    AssemblerGenerator generator = new AssemblerGenerator(program, conflictGraph);

    String assemblyCode = generator.generateAssembly();
    System.out.println(assemblyCode);

	}
}
