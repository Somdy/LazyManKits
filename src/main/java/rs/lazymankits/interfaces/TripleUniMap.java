package rs.lazymankits.interfaces;

public interface TripleUniMap<K, V1, V2> {
    V1 accept(K k);
    V2 find(K k);
    boolean identify(K k);
}