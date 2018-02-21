package custom.utils.datastructures;

import java.util.*;

import custom.utils.*;


public class jvuDataStructures {
	
	//TODO ::: write a generic method for object arrays
	/**
	 * Concatenate byte arrays. The order of the input param-s is important.
	 * @param 	a
	 * @param 	b
	 * @return
	 */
	public static byte[] concat (byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		
		return result;
	}
	
	/**
	 * TODO - not good implementation, two copyOf() are resource intensive, should try to use generics
	 * @param in
	 * @param expandAmt
	 * @return
	 */
	public static String[] expand (String[] in, int expandAmt) {
		return Arrays.copyOf(expand(Arrays.copyOf(in, in.length, Object[].class), expandAmt), in.length+expandAmt, String[].class);
	}
	
	/**
	 * Dynamically increase the size of 1-dimensional array of Objects by the specified size.
	 * @param 	arrayToExpand
	 * @param 	expandAmt - advisable to be positive, so no data is lost
	 * @author 	Atanas P. Zlatarov
	 */
	public static Object[] expand (Object[] arrayToExpand, int expandAmt) {
		if (arrayToExpand == null)
			return null;
		
		int oldSize = arrayToExpand.length;
		Object[] newArray = new Object[oldSize + expandAmt];
	    System.arraycopy(arrayToExpand, 0, newArray, 0, oldSize);
	    
	    return newArray;
	}
	
	/**
	 * Dynamically increase the size of 2-dimensional array of Objects by the specified size.
	 * @param 	arrayToExpand
	 * @param 	expandAmt - advisable to be positive, so no data is lost
	 * @author 	Atanas P. Zlatarov
	 */
	public static Object[][] expand (Object[][] arrayToExpand, int expandAmt) {
		if (arrayToExpand == null)
			return null;
		
		int oldSize = arrayToExpand.length;
		Object[][] temp = new Object[oldSize + expandAmt][];
		System.arraycopy(arrayToExpand, 0, temp, 0, oldSize);
		
		return temp;
	}
	
	/**
	 * TODO - not good implementation, two copyOf() are resource intensive, should try to use generics
	 * @param in
	 * @param expandAmt
	 * @return
	 */
	public static String[] clean (String[] in, Object[] values) {
		Object[] cleaned = clean(Arrays.copyOf(in, in.length, Object[].class), values);
		return Arrays.copyOf(cleaned,  cleaned.length, String[].class);
	}
	
	/**
	 * Cleans all values from the given array. Resulting array will be resized.
	 * @param 	arrayToClean
	 * @param	values
	 * @author 	Atanas P. Zlatarov
	 */
	public static Object[] clean (Object[] arrayToClean, Object[] values) {
		if (arrayToClean == null || arrayToClean.length == 0)
			return arrayToClean;
		
		List<Object> list = new ArrayList<Object> (Arrays.asList(arrayToClean));
		list.removeAll(Arrays.asList(values));
		
		return list.toArray(new Object[0]);
	}
	
	/**
	 * The method splits every array value with the given separator. The input array is modified by reference, while arrayOfLeftover is the returned object.
	 * @param 	array
	 * @param 	separator
	 * @return 	arrayOfLeftover	-	the map which contains <firstPart, secondPart>
	 * @throws 	StringIndexOutOfBoundsException - If line does not contain the separator. Should be propagated to user for manual inspection.
	 * @author 	Atanas P. Zlatarov
	 */
	public static Map<String, String> splitValues (String[] array, String separator) throws CustomUtilsException {
		if (array == null || array.length == 0)
			return null;
		
		Map<String, String> leftover = new HashMap<String, String>();
		
		if (separator == null || "".equals(separator))
			return leftover;
		
		String currentArrayValue;
		int separationIndex;
		String firstPart;
		String secondPart;
		
		for(int i = 0; i < array.length; i++) {
			currentArrayValue = array[i];
			separationIndex = currentArrayValue.indexOf(separator);
			if (separationIndex < 0)
				throw new CustomUtilsException("Element "+i+" does not contain the separator: "+separator+" !");
			
			firstPart = currentArrayValue.substring(0, separationIndex).trim();
			secondPart = currentArrayValue.substring(separationIndex + separator.length()).trim();
			
			leftover.put(firstPart, secondPart);
			
			array[i] = firstPart;
		}
		
		return leftover;
	}
	
	/**
	 * Removes given string from all HashMap values. Skips keys in the exceptionKeys list. Pay attention, original map is modified!
	 * @param 	hm
	 * @param 	exceptionKeys - values for these keys will not to be touched
	 * @param	valueToRmv - value to remove
	 * @author 	Atanas P. Zlatarov
	 */
	public static void removeFromValues (HashMap<String, String> hm, ArrayList<String> exceptionKeys, String valueToRmv) {
		if (hm == null || hm.isEmpty())
			return;
		
		for (String key : hm.keySet()) {
			if(exceptionKeys == null || !exceptionKeys.contains(key)) 
				hm.put(key, jvuString.rmvAll(hm.get(key), valueToRmv));
		}
	}
	
	/**
	 * Tries to get size of the supplied object. If NULL or unsupported type, -1 is returned.
	 */
	public static int sizeOf(Object obj) {
		if (obj == null)
			return -1;
		else if (obj instanceof Collection)
			return ((Collection<?>) obj).size();
		else if (obj instanceof Map)
			return ((Map<?, ?>) obj).size();
		else if (obj instanceof Object[])
			return ((Object[]) obj).length;
		else if (obj instanceof String)
			return ((String) obj).length();
		else
			return -1;
	}

	
	/**
	 * Sorting of Collections, Maps, Arrays, custom objects
	 */
	public static void sort () {
		
		List<Integer> intList = Arrays.asList(new Integer[] {1,2,3,7,1,2,1});
		List<Double> doubleList = Arrays.asList(new Double[] {1.1,2.2,3.3,7.7,1.1,2.2,1.1});
		
		/* Sorting always goes according to Comparable or Comparator implementation. */
		class Person implements Comparable<Person> {
			private int egn, age;
			private String name;
			
			public Person(int egn, String name, int age) { this.egn = egn; this.name = name; this.age = age; }
			
			@Override
			public int compareTo(Person rhs) { return Integer.signum(this.egn - rhs.egn); }
			
			@Override
			public String toString() { return String.format("{egn=%d, name=%s, age=%d}", egn, name, age); }
		}
		
		class NameComparator implements Comparator<Person> {
			@Override
			public int compare(Person lhs, Person rhs) { return lhs.name.compareToIgnoreCase(rhs.name); }
		}
		
		class AgeComparator implements Comparator<Person> {
			@Override
			public int compare(Person lhs, Person rhs) { return Integer.signum(rhs.age - lhs.age); } // gives DESC order; use signum(lhs.age - rhs.age) for ASC order
		}
		List<Person> people = Arrays.asList(new Person(100001, "Zincho", 32), new Person(100002, "Strincho", 22));
		Collections.sort(people); // will sort according to Person.compareTo implementation i.e. Comparable implementation
		Collections.sort(people, new NameComparator()); // will sort according to custom Comparator implementation
		Collections.sort(people, new AgeComparator()); // --//--
		
		/* Some out-of-the-box implementations. */
		Arrays.sort(new int[] {1,2,3,4,2,2,3,4,5}); // natural order
		Arrays.sort(new Integer[] {1,2,3,4,2,2,3,4,5}); // natural order
		Arrays.sort(new Integer[] {1,2,3,4,2,2,3,4,5}, Collections.reverseOrder()); // reverse natural order
		
		Collections.sort(Arrays.asList(new Integer(1), new Integer(9), new Integer (10), new Integer(1))); // natural order
		Collections.reverse(Arrays.asList(new Integer[] {1,2,3,9,1,1,1})); // reverse input order
		Collections.sort(Arrays.asList(new Integer[] {1,2,3,7,1,2,1}), Collections.reverseOrder()); // reverse natural order
		Collections.sort(intList); Collections.reverse(intList); // NOT GOOD - poor performance, but easy to implement
		
		/* Some custom Comparator implementations in JAVA 8 */
		Collections.sort(Arrays.asList(new Integer[] {1,2,3,7,1,2,1}), (a,b) -> a.compareTo(b)); // natural order
		Collections.sort(Arrays.asList(new Integer[] {1,2,3,7,1,2,1}), (a,b) -> b.compareTo(a)); // reverse natural order
		
		Collections.sort(doubleList, (a,b) -> a<b ? -1 : a==b ? 0 : 1); // natural order
		Collections.sort(doubleList, (a,b) -> a<b ? 1 : a==b ? 0 : -1); // reverse natural order
		
		Collections.sort(people, new Comparator<Person>(){
			@Override
			public int compare(final Person lhs, final Person rhs) {
				return lhs.name.compareToIgnoreCase(rhs.name);
			}
		});
		
		// NOTE::: In general, using custom Comparator is best - requires no change of sorted object
		
		// NOTE::: Class shouldn't be its own Comparator i.e. Comparator implementation shouldn't be inside class being sorted
	}
	
	public static TreeMap<Object, Object> sortByKey (final Map<Object, Object> in) {
		return (in == null || in.isEmpty()) ? null : new TreeMap<Object, Object>(in);
	}
	
	/**
	 * Returns a sorted (by value) LinkedHashMap of the given Map.
	 */
	public static <K, V extends Comparable<V>> Map<K, V> sortByValue (final Map<K, V> inMap) {
		if (inMap == null || inMap.isEmpty())
			return inMap;
		
		final List<K> inputMapKeys = new ArrayList<K>();
		final List<V> inputMapValues = new ArrayList<V>();
		for (K key : inMap.keySet()) {
			inputMapKeys.add(key);
			inputMapValues.add(inMap.get(key));
		}
		
		final Map<K, V> toReturn = new LinkedHashMap<K, V>();
		List<V> elements = new ArrayList<V>(inMap.values());
		Collections.sort(elements);
		for (V elem : elements) {
			int index = inputMapValues.indexOf(elem);
			toReturn.put(inputMapKeys.get(index), elem);
			inputMapKeys.remove(index);
			inputMapValues.remove(index);
		}
		
		return toReturn;
	}
	
	public static Map<Object, Object> sortByValue_v2 (Map<Object, Object> hmap, boolean ascending) {
		HashMap<Object, Object> result = new LinkedHashMap<Object, Object>();
		
		List<Object> keys = new ArrayList<Object>(hmap.keySet());
		List<Object> values = new ArrayList<Object>(hmap.values());
		hmap.clear();
		
		TreeSet<Object> sortedSet = new TreeSet<Object>(values);
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;
		
		if (ascending) 
			for (int i=0; i < size; i++)
				result.put(keys.get(values.indexOf(sortedArray[i])), sortedArray[i]);
		else
			for (int i=size; i > -1; i--)
				result.put(keys.get(values.indexOf(sortedArray[i])), sortedArray[i]);
		
		return result;
	}
	
	
	/**
	 * Finds the elements present in <i>before</i>, and missing in <i>after</i>.
	 */
	public static Vector<Object> findMissingElements (Vector<Object> before, Vector<Object> after) {
		if (before == null)
			return null;

		if (after == null)
			return before;

		Vector<Object> result = new Vector<>();
		for (Object o : before)
			if (after.indexOf(o) < 0)
				result.add(o);

		return result;
	}
	
	/**
	 * Does the opposite of getMissing().
	 */
	public static Vector<Object> findAddedElements (Vector<Object> before, Vector<Object> after) {
		return findMissingElements(after, before);
	}
	
	
	static void sop(Object o) {System.out.println(o);}
}
