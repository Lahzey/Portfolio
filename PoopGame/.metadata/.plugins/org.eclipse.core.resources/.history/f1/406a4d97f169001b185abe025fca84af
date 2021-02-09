package com.creditsuisse.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections4.keyvalue.DefaultMapEntry;
import org.apache.commons.collections4.map.MultiKeyMap;

/**
 * A map that uses the == operator to check for equality (instead of comparing the hash).
 */
public class ExactEqualityMap<K, V> implements Map<K, V>{
	
	private int size = 0;
	
	private Random keyGenerator = new Random();
	
	// the real storage
	private Map<Integer, Map<Integer, K>> keys = new HashMap<Integer, Map<Integer, K>>();
	private MultiKeyMap<Integer, V> values = new MultiKeyMap<Integer, V>();
	

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		int hash = System.identityHashCode(key);
		Map<Integer, K> possibleKeys = keys.get(hash);
		if(possibleKeys != null){
			for(K possibleKey : possibleKeys.values()){
				if(possibleKey == key){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for(V possibleValue : values.values()){
			if(possibleValue == value){
				return true;
			}
		}
		return false;
	}

	@Override
	public V get(Object key) {
		int hash = System.identityHashCode(key);
		Map<Integer, K> possibleKeys = keys.get(hash);
		if(possibleKeys != null){
			for(int i : possibleKeys.keySet()){
				K possibleKey = possibleKeys.get(i);
				if(possibleKey == key){
					return values.get(hash, i);
				}
			}
		}
		return null;
	}

	@Override
	public V put(K key, V value) {
		int hash = System.identityHashCode(key);
		Map<Integer, K> possibleKeys = keys.get(hash);
		if(possibleKeys == null){
			possibleKeys = new HashMap<Integer, K>();
			keys.put(hash, possibleKeys);
		}
		
		int keyIndex = -1;
		for(int i = 0; i < possibleKeys.size(); i++){
			K possibleKey = possibleKeys.get(i);
			if(possibleKey == key){
				keyIndex = i;
				break;
			}
		}
		
		if(keyIndex == -1){
			while(keyIndex < 0 || possibleKeys.containsKey(keyIndex)){
				keyIndex = keyGenerator.nextInt();
			}
			possibleKeys.put(keyIndex, key);
		}
		
		V prevValue = values.put(hash, keyIndex, value);
		if(prevValue == null){
			size++;
		}
		return prevValue;
	}

	@Override
	public V remove(Object key) {
		int hash = System.identityHashCode(key);
		Map<Integer, K> possibleKeys = keys.get(hash);
		if(possibleKeys != null){
			int keyIndex = -1;
			for(int i : possibleKeys.keySet()){
				K possibleKey = possibleKeys.get(i);
				if(possibleKey == key){
					keyIndex = i;
					possibleKeys.remove(i);
					if(possibleKeys.isEmpty()){
						keys.remove(hash);
					}
					break;
				}
			}
			
			if(keyIndex != -1){
				V value = values.removeMultiKey(hash, keyIndex);
				if(value != null){
					size--;
				}
				return value;
			}
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(K key : m.keySet()){
			put(key, m.get(key));
		}
	}

	@Override
	public void clear() {
		keys.clear();
		values.clear();
		size = 0;
	}

	/**
	 * <b>USAGE OF THIS METHOD IS HIGHLY DISCOURAGED!!!</b>
	 * <br/>Use {@link #keyList()} instead.
	 * <br/>
	 * <br/>Due to the nature of set (using equals() instead of ==) this method may not return all keys.
	 * @return all keys in this map, but objects with the same hash will replace each other
	 * @see #keyList()
	 */
	public Set<K> keySet() {
		Set<K> keySet = new HashSet<K>();
		for(Map<Integer, K> possibleKeys : keys.values()){
			keySet.addAll(possibleKeys.values());
		}
		return keySet;
	}
	
	/**
	 * The accurate replacement for {@link #keySet()}.
	 * @return all keys in this map
	 */
	public List<K> keyList() {
		List<K> keyList = new ArrayList<K>();
		for(Map<Integer, K> possibleKeys : keys.values()){
			keyList.addAll(possibleKeys.values());
		}
		return keyList;
	}

	@Override
	public Collection<V> values() {
		return values.values();
	}
	
	/**
	 * <b>USAGE OF THIS METHOD IS HIGHLY DISCOURAGED!!!</b>
	 * <br/>Use {@link #entryList()} instead.
	 * <br/>
	 * <br/>Due to the nature of set (using equals() instead of ==) this method may not return all entries.
	 * <br/>This is not very efficient because every Entry has to be created on each call of this method
	 * @return all entries in this map, but objects with the same hash will replace each other
	 */
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> entrySet = new HashSet<Entry<K, V>>();
		for(int hash : keys.keySet()){
			Map<Integer, K> keysForHash = keys.get(hash);
			for(int keyIndex : keysForHash.keySet()){
				K key = keysForHash.get(keyIndex);
				V value = values.get(hash, keyIndex);
				entrySet.add(new DefaultMapEntry<K, V>(key, value));
			}
		}
		return entrySet;
	}
	
	/**
	 * The accurate replacement for {@link #entrySet()}.
	 * <br/>This is not very efficient because every Entry has to be created on each call of this method
	 * @return all entries in this map
	 */
	public List<Entry<K, V>> entryList() {
		List<Entry<K, V>> entryList = new ArrayList<Entry<K, V>>();
		for(int hash : keys.keySet()){
			Map<Integer, K> keysForHash = keys.get(hash);
			for(int keyIndex : keysForHash.keySet()){
				K key = keysForHash.get(keyIndex);
				V value = values.get(hash, keyIndex);
				entryList.add(new DefaultMapEntry<K, V>(key, value));
			}
		}
		return entryList;
	}

}
