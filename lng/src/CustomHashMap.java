import java.util.*;

public class CustomHashMap<K, V> extends Object{

    private int size;
    private int DEFAULT_CAPACITY = 16;

    private MapEntry<K, V>[] values = new MapEntry[DEFAULT_CAPACITY];

    public V get(K key) {

        for (int i = 0; i < size; i++) {

            if (values[i] != null) {

                if (values[i].getKey().equals(key)) {
                    return values[i].getValue();
                }

            }

        }

        return null;

    }

    public void put(K key, V value) {

        boolean insert = true;

        for (int i = 0; i < size; i++) {

            if (values[i].getKey().equals(key)) {
                values[i].setValue(value);
                insert = false;
            }

        }

        if (insert) {
            ensureCapa();
            values[size++] = new MapEntry<K, V>(key, value);
        }

    }

    public boolean containsKey(K key){

        for (int i = 0; i < size; i++) {

            if (values[i].getKey().equals(key)) {
               return true;
            }

        }

        return false;

    }

    public boolean isEmpty(){
        return size == 0;
    }

    private void ensureCapa() {

        if (size == values.length) {
            int newSize = values.length * 2;
            values = Arrays.copyOf(values, newSize);
        }

    }

    public int size() {
        return size;
    }

    public void remove(K key) {

        for (int i = 0; i < size; i++) {
            if (values[i].getKey().equals(key)) {
                values[i] = null;
                size--;
                condenseArray(i);
            }
        }

    }

    private void condenseArray(int start) {

        for (int i = start; i < size; i++) {
            values[i] = values[i + 1];
        }

    }

    public Set<K> keySet() {

        Set<K> set = new HashSet<K>();

        for (int i = 0; i < size; i++) {
            set.add(values[i].getKey());
        }

        return set;

    }

    public void clear(){

        values = new MapEntry[DEFAULT_CAPACITY];

    }

    @Override
    public String toString(){

        if(this.size == 0){
            return "hsmp{"+this.hashCode()+"}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append('{');

        for(int i = 0; i < this.size; i++){

            sb.append(values[i].getKey());
            sb.append('=');
            sb.append(values[i].getValue());
            if(i != this.size-1)sb.append(',').append(' ');

        }

        return sb.append('}').toString();

    }

}

class MapEntry<K, V> {

    private final K key;
    private V value;

    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

}