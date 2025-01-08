package src;

import src.Type.*;

import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello world");

		// tests PrimitiveType.equals() :
		Type integer = new PrimitiveType(Type.Base.INT);
		Type integer2 = new PrimitiveType(Type.Base.INT);
		Type booooool = new PrimitiveType(Type.Base.BOOL);
		/*
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


*/
		//tests UnknownType.equals()
		Type cOnFuSiOn = new UnknownType();
		Type cOnFuSiOn2 = new UnknownType();
		/*
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

		*/

		System.out.println(integer.unify(integer2));	//null
		System.out.println(integer.unify(cOnFuSiOn));	//{UnknownType(#, 0)=INT}
		System.out.println(cOnFuSiOn.unify(booooool));	//{UnknownType(#, 0)=BOOL}
		System.out.println(cOnFuSiOn.unify(cOnFuSiOn2));
		// System.out.println(integer.unify(booooool));	//error


	}
}
