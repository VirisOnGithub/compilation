package src;

import src.Type.Type;
import src.Type.UnknownType;

import java.util.*;

public class TypesStack {

	private final List<Map<UnknownType, Type>> stack;

	public TypesStack() {
		this.stack = new ArrayList<>();
	}

	public void addNewBlock() {
		stack.add(new HashMap<>());
	}

	public Map<UnknownType, Type> pop() {
		Map<UnknownType, Type> elementToPop = stack.getLast();
		this.stack.remove(this.stack.size()-1);
		return elementToPop;
	}

	public Map<UnknownType, Type> getLastStack () {
		int size = this.stack.size();
		return this.stack.get(size-1);
	}

	/**
	 * Rajoute la key et la value à la dernière map
	 * @param key UnknownType
	 * @param value Type
	 */
	public void putLastStack (UnknownType key, Type value) {
		if (this.stack.isEmpty()) {
			HashMap<UnknownType, Type> tmp = new HashMap<>();
			tmp.put(key, value);
			this.stack.add(tmp);
		} else {
			this.stack.get(this.stack.size()-1).put(key, value);
		}
	}

	/**
	 * Regarde dans toute la stack si ut exist en tant que clé ou valeur.
	 * @param ut UnknownType
	 * @return boolean
	 */
	public boolean contains(UnknownType ut) {
		int size = this.stack.size();
		for (int i = size-1; i >= 0; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			for (Map.Entry<UnknownType, Type> entry : tmp.entrySet()) {
                if (entry.getKey().equals(ut) || entry.getValue().equals(ut)) {
                    return true;
                }
			}
		}
		return false;
	}

	/**
	 * Cherche si la clé est contenue dans la stack.
	 * @param key UnknowType
	 * @return boolean, Renvoie true si la key est contenue dans au moins une des HashMap de la stack.
	 */
	public boolean containsKey(UnknownType key) {
		return this.getLastStackOfUT(key) != null;
	}

	public boolean containsVarName(String varName) {
		return this.getLastStackOfVarName(varName) != null;
	}

	/**
	 * Récupère le dernier layer où ut est une key ou une value de celui ci, retourne null sinon
	 * @param ut UnknownType
	 * @return Map<UnknownType, Type> || null, si ut n'est pas présent dans les map de this.stack
	 */
	public Map<UnknownType, Type> getLastStackOfUT(UnknownType ut) {
		Map<UnknownType, Type> result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			if (tmp.containsKey(ut) || tmp.containsValue(ut)) {
				result = tmp;
				notFound = false;
			}
		}
		return result;
	}

	public Map<UnknownType, Type> getLastStackOfVarName (String varName) {
		int size = this.stack.size();

		for (int i = size-1; i >= 0; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			for (UnknownType key : tmp.keySet()) {
                if (key.getVarName().equals(varName)) {
                    return tmp;
                }
			}
		}
		return null;
	}

	/**
	 * Retourne le Type de la key
	 * @param key
	 * @return
	 */
	public Type getTypeOfUT(UnknownType key) {
		Type result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			if (tmp.containsKey(key)) {
				result = tmp.get(key);
				notFound = false;
			}
		}
		return result;
	}

	/**
	 * Retourne le dernier type déclaré pour une variable de nom varName
	 * @param varName
	 * @return null
	 */
	public Type getLastTypeOfVarName (String varName) {
		Type result = null;
		int size = this.stack.size();
		boolean notFound = true;

		for (int i = size-1; i >= 0 && notFound; i--) {
			Map<UnknownType, Type> tmp = this.stack.get(i);
			for (UnknownType key : tmp.keySet()) {
                if (key.getVarName().equals(varName)) {
                    result   = tmp.get(key);
                    notFound = false;
                    break;
                }
			}
		}
		return result;
	}

	public int indexOf(Map<UnknownType, Type> layer) {
		return this.stack.indexOf(layer);
	}

	public void set(int index, Map<UnknownType, Type> map) {
		this.stack.set(index, map);
	}

	public int size() {
		return stack.size();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("TypesStack{");

		int size = this.stack.size();
		for (int i = size-1; i >= 0; i--) {
			s.append("\n\tlayer ").append(i).append(" : ");
			Map<UnknownType, Type> tmp = this.stack.get(i);
			s.append(tmp);
		}

		s.append("\n}");
		return s.toString();
	}
}
