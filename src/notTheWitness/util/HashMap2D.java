package notTheWitness.util;

import java.util.*;

public class HashMap2D<TKey1, TKey2, TValue> {
  private HashMap<TKey1, HashMap<TKey2, TValue>> map = new HashMap<TKey1, HashMap<TKey2, TValue>>();
  
  public HashMap2D() { }
  
  public TValue put(TKey1 key1, TKey2 key2, TValue value) {
    if (!map.containsKey(key1)) map.put(key1, new HashMap<TKey2, TValue>());
    
    return map.get(key1).put(key2, value);
  }
  
  public TValue get(TKey1 key1, TKey2 key2) {
    if (!map.containsKey(key1)) return null;
    return map.get(key1).get(key2);
  }
}
