import java.util.NoSuchElementException;

public class DoubleLinkedList<Type> extends Object{

    private ListNode<Type> front;

    public void DoublyLinkedList() {
        front = null;
    }

    private int size;

    public Type iterForward(){

        Type t = front.data;
        next();
        return t;

    }

    public Type iterBackward(){

        Type t = front.data;
        prev();
        return t;

    }

    public Type getCurrent(){

        if(front != null){
            return front.data;
        }else{
            return null;
        }

    }

    public Type getByIndex(int index){

        ListNode<Type> temp = front;

        while (temp.prev != null) {
            temp = temp.prev;
        }
        System.out.println(temp.data);

        for(int i = 0; i < index; i++){
            temp = temp.next;
        }
        System.out.println(temp.data);

        return temp.data;

    }

    public void next(){

        if(front.next != null){

            front = front.next;

        }

    }

    public void prev(){

        if(front.prev != null){

            front = front.prev;

        }

    }

    public void toFront(){

        while (front.prev != null){

            front = front.prev;

        }

    }

    public void toEnd(){

        while (front.next != null){

            front = front.next;

        }

    }

    public void addFront(Type x) {

        if (isEmpty())

            front = new ListNode<Type>(x);

        else {

            ListNode<Type> temp = front;

            while (temp.prev != null) {
                temp = temp.prev;
            }

            temp.prev = new ListNode<Type>(null, x, temp);

        }

        size++;

    }

    public void addEnd(Type x) {

        if (isEmpty())

            front = new ListNode<Type>(x);

        else {

            ListNode<Type> temp = front;

            while (temp.next != null) {
                temp = temp.next;
            }

            temp.next = new ListNode<Type>(temp, x, null);

        }

        size++;

    }


    public void addBefore(Type x, Type y) {

        if (isEmpty())
            throw new NoSuchElementException("Element " + x.toString() + " not found");

        ListNode<Type> current = front;

        while (current != null && !current.data.equals(x))
            current = current.next;

        if (current == null)
            throw new NoSuchElementException("Element " + x.toString() + " not found");

        ListNode<Type> newNode = new ListNode<Type>(current.prev, y, current);
        if (current.prev != null)
            current.prev.next = newNode;
        else
            front = newNode;
        current.prev = newNode;

        size++;

    }

    public void addAfter(Type x, Type y) {

        if (isEmpty())
            throw new NoSuchElementException("Element " + x.toString() + " not found");

        ListNode<Type> current = front;

        while (current != null && !current.data.equals(x))
            current = current.next;

        if (current == null)
            throw new NoSuchElementException("Element " + x.toString() + " not found");

        ListNode<Type> newNode = new ListNode<Type>(current, y, current.next);
        if (current.next != null)
            current.next.prev = newNode;
        current.next = newNode;

        size++;

    }

    public void remove(Type x) {

        if (isEmpty())
            throw new NoSuchElementException("Element " + x.toString() + " not found");

        if (front.data.equals(x)) {

            if(size != 1){
                front = front.next;
                //front.prev = null;
                size--;
                return;
            }else if(size==1){
                front.data = null;
                size--;
                return;
            }

        }

        ListNode<Type> current = front;

        while (current != null && !current.data.equals(x))
            current = current.next;

        if (current == null)
            throw new NoSuchElementException("Element " + x.toString() + " not found");

        if (current.next != null)
            current.next.prev = current.prev;

        current.prev.next = current.next;

        size--;

    }

    public void removeByIndex(int index){

        ListNode<Type> temp = front;

        while (temp.prev != null) {
            temp = temp.prev;
        }
        System.out.println(temp.data);

        for(int i = 0; i < index; i++){
            temp = temp.next;
        }
        System.out.println(temp.data);

        if(temp.prev != null){
            ListNode<Type> temp2 = temp.prev;
            temp2.next = temp.next;
            if(temp.next != null)temp.next.prev = temp2;
        }else{

            front = temp.next;
            front.prev = null;

        }

    }

    public void removeCurrent(){
        remove(front.data);
    }

    public boolean isEmpty() {
        //System.out.println("check if isEmpty + size " + size);
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void clear(){

        toFront();

        front.data = null;
        front.next = null;
        front.prev = null;

    }

    @Override
    public String toString() {

        ListNode<Type> temp = front;

        if(temp != null){
            while (temp.prev != null) {
                temp = temp.prev;
            }
        }

        StringBuilder builder = new StringBuilder();
        String buff = "";

        while (temp != null) {
            builder.append(temp.data);
            if(temp.next != null)builder.append(",");
            temp = temp.next;
        }

        if(builder.isEmpty()){
            builder.append("dll(" + this.hashCode()+")");
            buff = builder.toString();
        }else{
            builder.append("]");
            buff = "[" + builder.toString();
        }

        return buff;

    }

}

class ListNode<Type> {

    Type data;
    ListNode<Type> next;
    ListNode<Type> prev;


    ListNode(Type data) {
        this(null, data, null);
    }

    ListNode(ListNode<Type> prev, Type data, ListNode<Type> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

}
